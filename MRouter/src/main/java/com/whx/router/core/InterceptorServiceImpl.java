package com.whx.router.core;

import com.whx.router.callback.InterceptorCallback;
import com.whx.router.core.interfaces.IInterceptorService;
import com.whx.router.exception.RouterException;
import com.whx.router.interfaces.IInterceptor;
import com.whx.router.model.Card;
import com.whx.router.thread.CancelableCountDownLatch;
import com.whx.router.utils.MapUtils;
import com.whx.router.MRouter;
import com.whx.router.callback.InterceptorCallback;
import com.whx.router.core.interfaces.IInterceptorService;
import com.whx.router.exception.RouterException;
import com.whx.router.interfaces.IInterceptor;
import com.whx.router.model.Card;
import com.whx.router.thread.CancelableCountDownLatch;
import com.whx.router.utils.MapUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InterceptorServiceImpl implements IInterceptorService {

    @Override
    public void doInterceptions(Card card, InterceptorCallback callback) {
        MRouter.logger.info(null, "[doInterceptions] " + Warehouse.interceptors);
        if (MapUtils.isNotEmpty(Warehouse.interceptors)) {
            Iterator<Map.Entry<Integer, IInterceptor>> iterator = Warehouse.interceptors.entrySet().iterator();
            MRouter.getInstance().getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    CancelableCountDownLatch interceptorCounter = new CancelableCountDownLatch(Warehouse.interceptors.size());
                    try {
                        execute(card, iterator, interceptorCounter);
                        interceptorCounter.await(card.getTimeout(), TimeUnit.SECONDS);
                        if (interceptorCounter.getCount() > 0) {
                            RouterException exception = new RouterException("The interceptor processing timed out.");
                            MRouter.logger.warning(null, "[doInterceptions] [onInterrupt] message:" + exception.getMessage());
                            callback.onInterrupt(card, exception);
                        } else if (card.getInterceptorException() != null) {
                            callback.onInterrupt(card, card.getInterceptorException());
                        } else {
                            callback.onContinue(card);
                        }
                    } catch (Exception e) {
                        callback.onInterrupt(card, new RouterException("The interceptor handles exceptions."));
                    }
                }
            });
        } else {
            callback.onContinue(card);
        }
    }

    private static void execute(Card card, Iterator<Map.Entry<Integer, IInterceptor>> iterator, final CancelableCountDownLatch counter) {
        if (iterator.hasNext()) {
            Map.Entry<Integer, IInterceptor> interceptorEntry = iterator.next();
            interceptorEntry.getValue().process(card, new InterceptorCallback() {
                @Override
                public void onContinue(Card card) {
                    counter.countDown();
                    execute(card, iterator, counter);
                }

                @Override
                public void onInterrupt(Card card, Throwable exception) {
                    card.setInterceptorException(exception == null ? new RouterException("The jump is blocked!") : exception);
                    MRouter.logger.warning(null, "[doInterceptions] [onInterrupt] {" + interceptorEntry.getKey() + "->" + interceptorEntry.getValue().getClass().getSimpleName() + "} message:" + card.getInterceptorException().getMessage());
                    counter.cancel();
                }
            });
        }
    }

    @Override
    public void init() {

    }
}

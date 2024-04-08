package com.whx.router.callback;

import androidx.annotation.NonNull;

import com.whx.router.model.Card;

public interface InterceptorCallback {

    void onContinue(Card card);

    void onInterrupt(Card card, @NonNull Throwable exception);
}

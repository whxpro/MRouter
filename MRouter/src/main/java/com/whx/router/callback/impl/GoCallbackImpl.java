package com.whx.router.callback.impl;

import androidx.annotation.NonNull;

import com.whx.router.callback.GoCallback;
import com.whx.router.callback.GoCallback;
import com.whx.router.model.Card;

public abstract class GoCallbackImpl implements GoCallback {
    @Override
    public void onFound(Card card) {

    }

    @Override
    public void onLost(Card card) {

    }

    @Override
    public abstract void onArrival(Card card);

    @Override
    public void onInterrupt(Card card, @NonNull Throwable exception) {

    }
}

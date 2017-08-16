package com.glcamera.rx;

import org.reactivestreams.Subscription;

import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;

public abstract class SimpleFlowableSubscriber<T> implements FlowableSubscriber<T> {
    public abstract void invokeOnNext(T t);

    public abstract void invokeOnError(Throwable t);

    public abstract void invokeOnComplete();

    @Override
    public void onSubscribe(@NonNull Subscription s) {
        s.request(1);
    }

    @Override
    public void onNext(T t) {
        invokeOnNext(t);
    }

    @Override
    public void onError(Throwable t) {
        invokeOnError(t);
    }

    @Override
    public void onComplete() {
        invokeOnComplete();
    }
}

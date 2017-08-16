package com.glcamera.rx;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;


public abstract class SimpleFlowableOnSubscribe<T> implements FlowableOnSubscribe<T> {
    protected abstract T callNext() throws Exception;

    @Override
    public void subscribe(@NonNull FlowableEmitter<T> e) throws Exception {
        T t = callNext();
        e.onNext(t);
        e.onComplete();
    }
}

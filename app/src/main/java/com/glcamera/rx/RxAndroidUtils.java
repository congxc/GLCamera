package com.glcamera.rx;

import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Rxjava工具类
 * Created by gaoshanjiang on 16/6/5.
 */
public class RxAndroidUtils {
    public static <T> void createObservable(FlowableOnSubscribe<T> observableObserveOn,
                                            FlowableSubscriber<T> subscriber, Consumer<Subscription> consumer) {
        Flowable.create(observableObserveOn, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io()).doOnSubscribe(consumer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public static <T> void createObservable(FlowableOnSubscribe<T> observableObserveOn,
                                            FlowableSubscriber<T> subscriber) {
        Flowable.create(observableObserveOn, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}

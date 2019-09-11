package com.melardev.xeytanj.concurrent.communicators.reactive.oneway;

import com.melardev.xeytanj.concurrent.communicators.ICommunicator;

import java.util.List;

public interface IReactiveCommunicator<T> extends ICommunicator<T> {

    List<ReactiveCommunicatorListener<T>> getListeners();

    default void dispatchEvent(T event) {
        getListeners().forEach(l -> l.onEvent(event));
    }

    default void addListener(ReactiveCommunicatorListener<T> listener) {
        getListeners().add(listener);
    }

    public interface ReactiveCommunicatorListener<T> {
        void onEvent(T event);
    }
}

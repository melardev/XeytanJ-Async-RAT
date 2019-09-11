package com.melardev.xeytanj.concurrent.communicators;

public interface ICommunicator<T> {

    boolean hasPendingEvents();

    T getEventAsync();

    T getEvent() throws InterruptedException;

    T getEventSync() throws InterruptedException;

    void queueAsync(T object);

    void queueAsync(Runnable object);

    void queueSync(T object);

    void queueSync(Runnable object);

    boolean isThreadSafe();

}

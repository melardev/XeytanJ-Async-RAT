package com.melardev.xeytanj.concurrent.communicators.reactive.oneway;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class QueueReactiveCommunicator<T> implements IReactiveCommunicator<T> {
    private List<ReactiveCommunicatorListener<T>> listeners;
    private ArrayBlockingQueue<T> queue;

    public QueueReactiveCommunicator() {
        this.queue = new ArrayBlockingQueue<T>(100);
        this.listeners = new ArrayList<>();
    }

    @Override
    public List<ReactiveCommunicatorListener<T>> getListeners() {
        return this.listeners;
    }


    @Override
    public boolean hasPendingEvents() {
        return !queue.isEmpty();
    }

    @Override
    public T getEventAsync() {
        return queue.poll();
    }

    @Override
    public T getEvent() throws InterruptedException {
        return queue.take();
    }

    @Override
    public T getEventSync() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void queueAsync(T object) {
        queue.add(object);
    }

    @Override
    public void queueAsync(Runnable object) {
        throw new IllegalArgumentException("Not supported, use RunnableEvent instead");
    }

    @Override
    public void queueSync(Runnable object) {
        throw new IllegalArgumentException("Not supported, The only way I could think of to support this is to make a Queue of Objects" +
                "Then in the receiving side should check if it is type Runnable if it is call .run(), otherwise check if other type." +
                "At this moment in time, I don't need this functionality so I will not implement it, I prefer this implementation, it is easierr to" +
                "read.");
    }

    @Override
    public void queueSync(T event) {
        try {
            queue.put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isThreadSafe() {
        return false;
    }
}

package com.melardev.xeytanj.concurrent.communicators;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class MessageQueueCommunicator<T> implements ICommunicator<T> {

    private LinkedBlockingQueue<T> queue;

    public MessageQueueCommunicator() {
        queue = new LinkedBlockingQueue<>(30);
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
    public void queueSync(T object) {
        try {
            queue.put(object);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean isThreadSafe() {
        return false;
    }

    public abstract boolean supportsMultiThreadedConsuming();
}

package com.melardev.xeytanj.concurrent.communicators;

public abstract class SingleThreadMessageQueueCommunicator<T> extends MessageQueueCommunicator<T> {

    @Override
    public boolean supportsMultiThreadedConsuming() {
        return false;
    }

}

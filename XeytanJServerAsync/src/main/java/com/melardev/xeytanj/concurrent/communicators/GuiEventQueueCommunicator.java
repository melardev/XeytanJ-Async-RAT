package com.melardev.xeytanj.concurrent.communicators;

import com.melardev.xeytanj.concurrent.messaging.IGuiEvent;

public class GuiEventQueueCommunicator extends SingleThreadMessageQueueCommunicator<IGuiEvent> {

    @Override
    public void queueAsync(Runnable object) {

    }

    @Override
    public void queueSync(Runnable object) {

    }
}

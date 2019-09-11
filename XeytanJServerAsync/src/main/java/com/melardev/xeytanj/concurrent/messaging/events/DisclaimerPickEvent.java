package com.melardev.xeytanj.concurrent.messaging.events;

import com.melardev.xeytanj.concurrent.messaging.IEvent;

public class DisclaimerPickEvent implements IEvent {
    private final boolean accepted;

    public DisclaimerPickEvent(boolean accepted) {
        this.accepted = accepted;
    }
}

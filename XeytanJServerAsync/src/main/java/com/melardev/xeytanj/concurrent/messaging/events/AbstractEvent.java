package com.melardev.xeytanj.concurrent.messaging.events;

import com.melardev.xeytanj.concurrent.messaging.IEvent;

public abstract class AbstractEvent implements IEvent {

    protected Object object;

    public AbstractEvent() {

    }

    public AbstractEvent(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}

package com.melardev.xeytanj.concurrent.messaging.events;

import com.melardev.xeytanj.enums.Action;
import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.enums.Target;

public class AppEvent extends AbstractEvent {

    protected Target target;
    protected Subject subject;
    protected Action action;


    public AppEvent() {
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Subject getSubject() {
        return subject;
    }
}

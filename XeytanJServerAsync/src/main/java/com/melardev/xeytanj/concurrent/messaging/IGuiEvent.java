package com.melardev.xeytanj.concurrent.messaging;

public interface IGuiEvent extends IEvent {

    public enum Intent {
        Start, Stop, Update
    }

    public enum Subject {
        MainWindow, CameraWindow,
    }

    Object getData();

}

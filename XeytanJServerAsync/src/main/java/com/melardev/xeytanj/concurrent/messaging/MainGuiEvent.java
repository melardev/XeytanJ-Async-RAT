package com.melardev.xeytanj.concurrent.messaging;

public class MainGuiEvent implements IGuiEvent {
    private Object data;

    public MainGuiEvent(Object data) {
        this.data = data;
    }

    public MainGuiEvent() {

    }

    @Override
    public Object getData() {
        return data;
    }

    private Intent intent;

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent() {
        return intent;
    }

}

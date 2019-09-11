package com.melardev.xeytanj.gui;

import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.services.IAppMessages;

public interface IGui<T> {
    void display();

    void addListener(T t);

    T getListener();

    default Class<? extends IGui> getWindowClass() {
        return getClass();
    }

    IUiMediator getMediator();

    void setMediator(IUiMediator mediator);

    void resetState();

    public void setMessageProvider(IAppMessages messageProvider);
    default void notifyMediatorOnClose() {
        getMediator().onWindowClose(this);
    }

    void dispose();
}

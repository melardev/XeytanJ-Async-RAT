package com.melardev.xeytanj.gui;

import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.models.Client;

public interface IGuiUserOwned<T> extends IGui<T> {
    Client getClient();

    void setClient(Client client);

    Subject getSubject();

    @Override
    default void notifyMediatorOnClose() {
        getMediator().onWindowUserOwnedClose(getClient(), this, getSubject());
    }

    void setStatus(String status);

    void disableUi();
}

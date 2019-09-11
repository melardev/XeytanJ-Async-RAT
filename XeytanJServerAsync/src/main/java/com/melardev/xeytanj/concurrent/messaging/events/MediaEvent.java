package com.melardev.xeytanj.concurrent.messaging.events;

import com.melardev.xeytanj.models.Client;

public class MediaEvent extends ClientAppEvent {

    public MediaEvent(Client client) {
        super(client);
    }
}

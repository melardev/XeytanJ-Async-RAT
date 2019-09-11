package com.melardev.xeytanj.concurrent.messaging.events;

import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.enums.Target;
import com.melardev.xeytanj.models.Client;

public class ClientAppEvent extends AppEvent {

    private Client client;

    public ClientAppEvent(Client client) {
        this.client = client;
        this.target = Target.CLIENT;
    }

    public ClientAppEvent(Subject subject, Client client) {
        this.subject = subject;
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}

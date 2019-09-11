package com.melardev.xeytanj.concurrent.messaging.events;

import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.enums.Target;
import com.melardev.xeytanj.models.Client;

public class ClientNetEvent extends NetEvent {

    private Client client;


    public ClientNetEvent(Client client) {
        this.client = client;
        this.target = Target.CLIENT;
    }

    public ClientNetEvent(Subject subject, Client client) {
        this.client = client;
        this.subject = subject;
    }

    public Client getClient() {
        return client;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}

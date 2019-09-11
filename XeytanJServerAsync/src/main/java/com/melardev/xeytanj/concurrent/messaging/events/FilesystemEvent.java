package com.melardev.xeytanj.concurrent.messaging.events;

import com.melardev.xeytanj.models.Client;

public class FilesystemEvent extends ClientNetEvent {

    public static enum FilesystemTarget {
        ROOT_LIST, FILE_LIST
    }

    public FilesystemTarget filesystemTarget;

    public FilesystemEvent(Client client) {
        super(client);
    }
}

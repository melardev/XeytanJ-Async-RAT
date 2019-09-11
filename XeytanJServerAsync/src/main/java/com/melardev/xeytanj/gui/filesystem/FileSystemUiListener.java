package com.melardev.xeytanj.gui.filesystem;

import com.melardev.xeytanj.models.Client;

public interface FileSystemUiListener {

    void onFileSystemPathRequested(Client client, String fileSystemPath);
}

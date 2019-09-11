package com.melardev.xeytanj.gui.process;

import com.melardev.xeytanj.models.Client;

public interface ProcessListUiListener {
    public void onProcessKillRequest(Client client, int pid);
}

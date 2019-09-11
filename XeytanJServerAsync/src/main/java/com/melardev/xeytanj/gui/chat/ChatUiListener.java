package com.melardev.xeytanj.gui.chat;

import com.melardev.xeytanj.models.Client;

public interface ChatUiListener {
    void onSendChatMessage(Client uuid, String text);
}

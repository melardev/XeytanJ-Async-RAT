package com.melardev.xeytanj.gui.desktop;

import com.melardev.xeytanj.enums.NetworkProtocol;
import com.melardev.xeytanj.gui.IUiListener;
import com.melardev.xeytanj.models.Client;

public interface RemoteDesktopUiListener extends IUiListener {
    void onRdpPlayBtnClicked(Client client, NetworkProtocol networkProtocol, String displayName, int scaleX, int scaleY, int delay);

    void onRdpPauseBtnCliked(Client client);

    void onRdpStopBtnClicked(Client client);
}

package com.melardev.xeytanj.gui.main;

import com.melardev.xeytanj.enums.NetworkProtocol;
import com.melardev.xeytanj.enums.ProcessInfoDetails;
import com.melardev.xeytanj.gui.IUiListener;
import com.melardev.xeytanj.models.Client;

public interface MainUiListener extends IUiListener {
    void getClientInfo(Client installerListener);

    void onRemoteShellClicked(Client id);

    void onProcessListClicked(ProcessInfoDetails infoLevel, Client client);

    void onMenuCameraClicked(NetworkProtocol mode, Client client);

    void onRemoteDesktopClicked(Client id);

    void onRemoteFileManagerClicked(Client client);

    void onStartChatRequested(Client client);

    void onTurnOnOffDisplayClicked(Client client, boolean on);

    void onRebootClicked(Client client);

    void onShutdownClicked(Client client);

    void onLogOffSystem(Client client);

    void onLockSystemClicked(Client client);

    void onSessionKeyloggerClicked(Client client);

    void onVoIpRawClicked(Client client);

    void onVoIpClicked(Client client);

    void onBuilderShowClicked();

    void onPreferencesClicked();

    void onCameraStartRequested(NetworkProtocol protocol, Client client);
}

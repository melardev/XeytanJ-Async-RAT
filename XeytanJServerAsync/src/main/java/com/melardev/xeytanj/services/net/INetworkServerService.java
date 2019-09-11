package com.melardev.xeytanj.services.net;

import com.melardev.xeytanj.concurrent.communicators.reactive.oneway.IReactiveCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.twoway.worker.AppAndNetCommunicator;
import com.melardev.xeytanj.concurrent.messaging.events.AppEvent;
import com.melardev.xeytanj.enums.MediaState;
import com.melardev.xeytanj.models.*;
import com.melardev.xeytanj.services.IService;
import com.melardev.xeytanj.services.config.IConfigService;
import com.melardev.xeytanj.services.logger.ILogger;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface INetworkServerService extends IService,
//         IReactiveCommunicator.ReactiveCommunicatorListener<NetEvent>
        IReactiveCommunicator.ReactiveCommunicatorListener<AppEvent> {


    void sendShellCommand(UUID id, String command);

    void setConfig(IConfigService configService);

    void startRebootSystem(UUID id);

    void startShutDownSystem(UUID id);

    void startLogOffSystem(UUID id);

    void startLockSystem(UUID id);

    void startTurnDisplay(UUID id, boolean on);

    void onClientLoginFail(INetworkClientService networkClientService);


    boolean isClientStreamingDesktop(UUID id);


    MediaState getDesktopMediaState(UUID id);

    void onCameraSessionClosed(Client client);


    void onFilesystemRootInfoReceived(Client client, FileInfoStructure[] fileInfoStructures);

    void getFileSystemView(UUID id, String fileSystemPath);

    void stopServer();

    ClientController getRemoteClientFromId(UUID id);


    void stopProcessMonitor(UUID id);

    void onProcessSessionClosed(Client client);

    void pauseCameraStreaming(UUID id);

    void stopCameraSession(UUID id);

    void sendChatMessage(UUID uuid, String text);

    void setLogger(ILogger logger);


    void onClientLoggedIn(Client client);

    void setCommunicator(AppAndNetCommunicator appAndNetCommunicator);

    Collection<? extends Client> getAllClients();

    void onPresentationData(Client client, Map<String, String> env, Map<String, String> properties);

    void onDesktopConfigInfoReceived(Client client, List<ScreenDeviceInfo> screenDeviceInfoList);

    void onDesktopImageReceived(Client client, ImageIcon image);

    void onCameraConfigInfoReceived(Client client, List<CameraDeviceInfo> cameras);

    void onCameraImageReceived(Client client, Icon image);

    void onFileSystemInfoUpdate(Client client, FileInfoStructure[] files);

    void onProcessInfoReceived(Client client, List<ProcessStructure> info);

    void onClientShellInfoReceived(Client client, String output);

    void onChatMessageReceived(Client client, String message);

    void onClientDisconnected(Client client);
}

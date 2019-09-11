package com.melardev.xeytanj.gui.mediator;

import com.melardev.xeytanj.concurrent.communicators.GuiCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.oneway.IReactiveCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.twoway.ui.GuiAndBackgroundReactiveCommunicator;
import com.melardev.xeytanj.concurrent.messaging.events.AppEvent;
import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.gui.IGui;
import com.melardev.xeytanj.gui.IGuiUserOwned;
import com.melardev.xeytanj.gui.builder.BuilderDialogListener;
import com.melardev.xeytanj.gui.camera.CameraUiListener;
import com.melardev.xeytanj.gui.chat.ChatUiListener;
import com.melardev.xeytanj.gui.desktop.RemoteDesktopUiListener;
import com.melardev.xeytanj.gui.disclaimer.DisclaimerUiListener;
import com.melardev.xeytanj.gui.filesystem.FileSystemUiListener;
import com.melardev.xeytanj.gui.language.LanguageUiListener;
import com.melardev.xeytanj.gui.main.MainUiListener;
import com.melardev.xeytanj.gui.process.ProcessListUiListener;
import com.melardev.xeytanj.gui.shell.ShellUiListener;
import com.melardev.xeytanj.maps.MapUiListener;
import com.melardev.xeytanj.models.*;
import com.melardev.xeytanj.services.IAppMessages;
import com.melardev.xeytanj.services.IService;
import com.melardev.xeytanj.services.logger.ILogger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public interface IUiMediator extends
        IService,
        MainUiListener,
        LanguageUiListener, DisclaimerUiListener,
        RemoteDesktopUiListener, CameraUiListener,
        FileSystemUiListener,
        ProcessListUiListener, ShellUiListener,
        ChatUiListener,
        BuilderDialogListener,
        MapUiListener,
        // GuiCommunicator.CommunicatorListener,
        // IReactiveCommunicator.ReactiveCommunicatorListener<IGuiEvent>
        IReactiveCommunicator.ReactiveCommunicatorListener<AppEvent> {

    void setMessageProvider(IAppMessages messageProvider);

    void showLanguageSelectorDialog();

    void showDisclaimerDialog();

    void showInstaller();

    void showDialogMessageError(String message);

    void showMainFrame();

    // void showClientInfo(Client client);

    void addClientRowTable(Client client);

    void showNewClientNotification(Client client);

    void removeClientRow(Client client);

    void showBuilderWindow(String defaultClientPath);


    void showOrUpdateRemoteDesktopConfigInfo(Client client, List<ScreenDeviceInfo> config);

    void showRemoteDesktopWindow(Client client, boolean reuseifAvailable);

    void showCameraWindow(Client client);

    void showOrUpdateCameraUi(Client client, List<CameraDeviceInfo> availableCameras);

    void showOrUpdateCameraUi(Client client, Icon image);

    void showRoots(Client client, FileInfoStructure[] fileInfoStructures);


    void closeAllUserOwnedWindows(Client client);

    void closeRemoteDesktopWindow(Client client);

    <T> void onWindowClose(IGui gui);

    <T> void onWindowUserOwnedClose(Client client, IGuiUserOwned gui, Subject subject);

    void updateProcessInfoWindow(Client client, List<ProcessStructure> processStructures);

    void showShellWindow(Client client);

    void appendShellOutput(Client client, String text);

    void notifyShellWindowDisconnectedForClient(Client client);

    void notifyCameraSessionClosedForClient(Client client);

    void showFileExplorerWindow(Client client);

    void updateFileSystemView(Client client, FileInfoStructure[] fileInfoStructures);

    void playAudioFromCamera(Client client, byte[] data, int bytesRead);

    void playAudioFile(Client client, byte[] data);

    void showChatMessage(Client client, String message);

    void showChatWindow(Client client);

    void closeBuilderUi();

    void setLogger(ILogger logger);

    void showErrorMessage(String message);

    void showErrorMessage(String title, String message);

    void showMapRequested();

    void showMap(String googleKey, ArrayList<Client> locations);

    void setCommunicator(GuiCommunicator communicator);

    void setCommunicator(GuiAndBackgroundReactiveCommunicator guiAndBackgroundReactiveCommunicator);
}

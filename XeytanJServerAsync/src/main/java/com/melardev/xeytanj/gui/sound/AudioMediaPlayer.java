package com.melardev.xeytanj.gui.sound;

import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.gui.IGuiUserOwned;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.IAppMessages;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class AudioMediaPlayer extends JFrame implements LineListener, IGuiUserOwned<AudioUiListener> {

    private SourceDataLine audioLine;
    private Clip audioClip;
    public volatile boolean completed;
    public ArrayList<String> outNames;
    public int current;
    private Client client;
    private AudioUiListener listener;
    private IUiMediator mediator;
    private IAppMessages messageProvider;

    public SourceDataLine getAudioLine() {
        return audioLine;
    }

    public void setAudioLine(SourceDataLine audioLine) {
        this.audioLine = audioLine;
    }

    public Clip getAudioClip() {
        return audioClip;
    }

    public void setAudioClip(Clip audioClip) {
        this.audioClip = audioClip;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public ArrayList<String> getOutNames() {
        return outNames;
    }

    public void setOutNames(ArrayList<String> outNames) {
        this.outNames = outNames;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void setListener(AudioUiListener listener) {
        this.listener = listener;
    }

    public AudioMediaPlayer() {
        completed = true;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                notifyMediatorOnClose();
            }
        });

    }

    public void initMedia() throws UnsupportedAudioFileException, IOException {
        //System.getProperty("java.io.tmpdir");
        //TODO: getProperty one file to use its format instead of hardcoding
        //File audioFile = new File("D:/Users/rabheus/Desktop\\CPSC\\videos\\recordingRecordAudio.wav");
        //AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        //AudioFormat format = audioStream.getFormat();
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        try {
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open();
            audioLine.start();
        } catch (LineUnavailableException e1) {
            e1.printStackTrace();
        }
    }


    public void release() {
        //audioLine.stop();
        audioLine.drain();
        audioLine.close();
    }

    public void playRaw(byte[] input, int bytesRead) {
        audioLine.write(input, 0, bytesRead);
    }

    public void playFile(byte[] data) {
        completed = false;
        File audioFile = new File("D:/Users/rabheus/Desktop\\CPSC\\videos\\Received.wav");
        Path path = audioFile.toPath();
        try {
            Files.write(path, data);
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                audioClip = (Clip) AudioSystem.getLine(info);
                audioClip.addLineListener(this);
                audioClip.open(audioStream);
                audioClip.start();
            } catch (UnsupportedAudioFileException | IOException e) {

                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (NegativeArraySizeException e) {
                e.printStackTrace();
                completed = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(LineEvent event) {
        javax.sound.sampled.LineEvent.Type type = event.getType();
        if (type == LineEvent.Type.START) {

        } else if (type == LineEvent.Type.STOP) {
            audioClip.close();
            completed = true;
        }

    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public Subject getSubject() {
        return null;
        //     return Subject.AUDIO;
    }

    @Override
    public void setStatus(String status) {

    }

    @Override
    public void disableUi() {

    }

    @Override
    public void display() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{42, 359, 0, 0};
        gridBagLayout.rowHeights = new int[]{140, 79, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        JLabel lblStatus = new JLabel("Not Recording");
        GridBagConstraints gbc_lblStatus = new GridBagConstraints();
        gbc_lblStatus.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatus.gridx = 1;
        gbc_lblStatus.gridy = 0;
        getContentPane().add(lblStatus, gbc_lblStatus);

        JButton btnRecord = new JButton("Record");
        GridBagConstraints gbc_btnRecord = new GridBagConstraints();
        gbc_btnRecord.insets = new Insets(0, 0, 5, 5);
        gbc_btnRecord.fill = GridBagConstraints.BOTH;
        gbc_btnRecord.gridx = 1;
        gbc_btnRecord.gridy = 1;
        getContentPane().add(btnRecord, gbc_btnRecord);
        completed = true;
        outNames = new ArrayList<String>();
        outNames.add("received01.wav");
        outNames.add("received02.wav");
        outNames.add("received03.wav");
        outNames.add("received04.wav");
        current = 0;

        setVisible(true);
        try {
            initMedia();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AudioUiListener getListener() {
        return listener;
    }

    @Override
    public IUiMediator getMediator() {
        return null;
    }

    @Override
    public void setMediator(IUiMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void resetState() {

    }

    @Override
    public void addListener(AudioUiListener listener) {
        this.listener = listener;
    }

    @Override
    public void setMessageProvider(IAppMessages messageProvider) {
        this.messageProvider = messageProvider;
    }

}

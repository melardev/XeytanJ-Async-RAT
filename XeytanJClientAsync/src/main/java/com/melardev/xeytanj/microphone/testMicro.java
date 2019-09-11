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
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineEvent.Type;

public class testMicro implements LineListener {

	private boolean completed;
	private Clip audioClip;
	private TargetDataLine line;

	public testMicro() {
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

	public static void main(String[] args) {
		new testMicro();
	}

}

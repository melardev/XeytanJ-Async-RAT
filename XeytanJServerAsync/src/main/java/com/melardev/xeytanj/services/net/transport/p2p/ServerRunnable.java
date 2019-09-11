package com.melardev.xeytanj.services.net.transport.p2p;

import com.melardev.xeytanj.services.net.transport.p2p.sync.P2PSyncTcpServerService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRunnable implements Runnable {
    private final P2PSyncTcpServerService serverService;
    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean hasToRun;

    public ServerRunnable(P2PSyncTcpServerService serverService, int port) {
        this.serverService = serverService;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            hasToRun = true;
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));
            while (hasToRun) {
                Socket clientSocket = null;
                clientSocket = serverSocket.accept();
                serverService.onNewSocketConnection(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isHasToRun() {
        return hasToRun;
    }

    public void setHasToRun(boolean hasToRun) {
        this.hasToRun = hasToRun;
        if (!hasToRun) {
            // if false then unblock the accept and close
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

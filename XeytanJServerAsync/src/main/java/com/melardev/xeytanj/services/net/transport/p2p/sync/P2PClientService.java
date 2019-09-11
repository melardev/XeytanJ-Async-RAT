package com.melardev.xeytanj.services.net.transport.p2p.sync;

import com.melardev.xeytanj.errors.AlreadyInteractingWithClientException;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.services.net.transport.p2p.AbstractClientService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class P2PClientService extends AbstractClientService {


    private Socket sock;
    private ObjectInputStream sockIn;
    private ObjectOutputStream sockOut;

    private String remoteIp;
    private boolean interactingWithClient;

    public P2PClientService(Socket socket) {
        sock = socket;
        remoteIp = sock.getInetAddress().getHostAddress();
        loggedIn = false;
    }

    @Override
    public void interactAsync() {
        logger.traceCurrentMethodName();

        if (interactingWithClient)
            throw new AlreadyInteractingWithClientException();

        interactingWithClient = true;

        try {
            sockIn = new ObjectInputStream(getSocket().getInputStream());
            sockOut = new ObjectOutputStream(getSocket().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            logger.traceCurrentMethodName();
            Packet packet;
            while (true) {
                try {
                    Object response = sockIn.readObject();
                    packet = (Packet) response;
                    dispatchPacket(packet);
                } catch (ClassNotFoundException e) {
                    clientController.onClientDisconnected();
                    return;
                } catch (SocketTimeoutException ex) {
                    try {
                        // triggered if the user does not send a valid login packet after the first fail + 20 seconds
                        sock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                } catch (IOException e) {
                    clientController.onClientDisconnected();
                    return;
                }
            }
        }).start();
    }


    public synchronized void sendPacket(Packet packet) {
        try {
            sockOut.writeObject(packet);
            sockOut.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return sock;
    }


    @Override
    public String getRemoteIp() {
        return remoteIp;
    }


}

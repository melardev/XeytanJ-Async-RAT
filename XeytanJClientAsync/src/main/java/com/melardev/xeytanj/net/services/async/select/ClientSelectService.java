package com.melardev.xeytanj.net.services.async.select;

import com.melardev.xeytanj.XeytanJClient;
import com.melardev.xeytanj.config.IConfigService;
import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketLogin;
import com.melardev.xeytanj.net.services.async.AbstractAsyncClientService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ClientSelectService extends AbstractAsyncClientService {
    private static final int PACKET_LENGTH_SIZE = 4;
    private boolean headerOk;
    private final XeytanJClient app;
    private final String host;
    private final int port;

    private SocketChannel clientSocketChannel;
    private ByteBuffer headerBuffer;
    private ByteBuffer payloadBuffer;

    private ByteBuffer bufferOut;

    public ClientSelectService(XeytanJClient application, String host, int port, ILogger logger, IConfigService config) {
        super(logger, config);
        this.app = application;
        this.host = host;
        this.port = port;
        this.headerOk = false;
        this.headerBuffer = ByteBuffer.allocate(PACKET_LENGTH_SIZE);
    }

    @Override
    public void interact() {

        try {
            InetAddress serverIPAddress = InetAddress.getByName(this.host);
            InetSocketAddress serverAddress = new InetSocketAddress(
                    serverIPAddress, this.port);
            Selector selector = Selector.open();
            clientSocketChannel = SocketChannel.open();
            clientSocketChannel.configureBlocking(false);
            clientSocketChannel.connect(serverAddress);

            // SocketChannel supports OP_CONNECT , and Read
            // OP_WRITE will always be enabled after we connected, this will flood our loop, so skip it
            // I dont care about reading neither, in this demo
            int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ;

            clientSocketChannel.register(selector, operations);
            while (true) {
                try {
                    if (selector.select() > 0) {
                        processSelectedKeys(selector.selectedKeys());
                    }
                } catch (IOException e) {
                    // If server closed connection then exit, if our user does not have connection retry later
                    e.printStackTrace();
                    break;
                }
            }
        } catch (
                UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (clientSocketChannel != null) {
                try {
                    clientSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void closeConnection() {
        // TODO
    }

    public void processSelectedKeys(Set selectedKeySet) {
        Iterator iterator = selectedKeySet.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();
            System.out.printf("isAcceptable %b isConnectable %b isReadable %b isWritable %b\n"
                    , key.isAcceptable(), key.isConnectable(), key.isReadable(), key.isWritable());

            if (key.isConnectable()) {
                {
                    try {
                        while (clientSocketChannel.isConnectionPending()) {
                            clientSocketChannel.finishConnect();
                        }
                        PacketLogin packetLogin = new PacketLogin(null, PacketLogin.LoginType.LOGIN_REQUEST);
                        packetLogin.setOs(System.getProperty("os.name"));

                        packetLogin.setLocalIp(((InetSocketAddress) clientSocketChannel.getLocalAddress()).getAddress().getHostAddress());
                        packetLogin.setPcName(InetAddress.getLocalHost().getHostName());
                        sendPacket(packetLogin);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (key.isReadable())
                onReadAvailable(key);
        }

    }

    private void onReadAvailable(SelectionKey key) {
        int bytesCount = 0;
        try {
            if (!headerOk) {
                bytesCount = clientSocketChannel.read(headerBuffer);
                if (headerBuffer.position() != headerBuffer.limit())
                    return;

                headerBuffer.flip();
                int packetLength = headerBuffer.getInt();

                // Check if we have to allocate a new ByteBuffer or perhaps just reuse the one
                // we created
                if (payloadBuffer == null || payloadBuffer.capacity() < packetLength)
                    payloadBuffer = ByteBuffer.allocate(packetLength);

                payloadBuffer.limit(packetLength);

                headerOk = true;
                headerBuffer.clear();
            }

            bytesCount = clientSocketChannel.read(payloadBuffer);

            // If we have not finished reading the whole packet then return, the next time
            // this function is called we will keep reading and hopefully get the whole expected packet
            if (payloadBuffer.position() != payloadBuffer.limit())
                return;

            if (bytesCount > 0) {

                payloadBuffer.flip();
                // For a demo of reusing the same buffer + serialization look the handlers_attachment demo
                byte[] receivedMessageBytes = new byte[payloadBuffer.limit()];
                payloadBuffer.get(receivedMessageBytes);

                ByteArrayInputStream bais = new ByteArrayInputStream(receivedMessageBytes);
                ObjectInputStream obis = new ObjectInputStream(bais);
                Packet packet = (Packet) obis.readObject();
                payloadBuffer.clear();
                headerOk = false;

                bais.close();
                obis.close();

                app.handlePacket(packet);

            } else {
                clientSocketChannel.close();
                System.out.println("Client disconnected");
            }

        } catch (IOException e) {
            e.printStackTrace();
            try {
                clientSocketChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void sendPacketInternal(ByteBuffer bufferOut) {
        if (this.bufferOut != bufferOut)
            this.bufferOut = bufferOut;
        try {
            int bytesWritten = 0;
            while (bytesWritten < bufferOut.limit()) {
                bytesWritten += clientSocketChannel.write(bufferOut);
            }

            bufferOut.clear();
            isSendingPacket = false;
            onDataSent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ByteBuffer getBufferOut() {
        return bufferOut;
    }
}

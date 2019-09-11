package com.melardev.xeytanj.net.services.async.completion;

import com.melardev.xeytanj.XeytanJClient;
import com.melardev.xeytanj.config.IConfigService;
import com.melardev.xeytanj.logger.ConsoleLogger;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketLogin;
import com.melardev.xeytanj.net.services.NetClientService;
import com.melardev.xeytanj.net.services.async.AbstractAsyncClientService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncCompletionHandlerNetService extends AbstractAsyncClientService implements CompletionHandler<Void, AsynchronousSocketChannel>, NetClientService {


    private final int port;
    private final String host;

    private final XeytanJClient app;
    private AsynchronousSocketChannel channel;

    private AttachmentSocket attachment;

    public AsyncCompletionHandlerNetService(XeytanJClient xeytanJClient, String host, int port, ConsoleLogger logger, IConfigService config) {
        super(logger, config);

        this.app = xeytanJClient;
        this.host = host;
        this.port = port;
    }

    @Override
    public void completed(Void result, AsynchronousSocketChannel socketChannel) {

        attachment = new AttachmentSocket(this);

        attachment.socketChannel = socketChannel;
        attachment.writeHandler = new WriteHandler();
        attachment.readHandler = new ReadHandler();

        PacketLogin packetLogin = new PacketLogin(null, PacketLogin.LoginType.LOGIN_REQUEST);
        packetLogin.setOs(System.getProperty("os.name"));
        try {
            packetLogin.setLocalIp(((InetSocketAddress) socketChannel.getLocalAddress()).getAddress().getHostAddress());
            packetLogin.setPcName(InetAddress.getLocalHost().getHostName());

            attachment.socketChannel.read(attachment.headerBuffer, attachment, attachment.readHandler);

            sendPacket(packetLogin);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
        exc.printStackTrace();
    }

    @Override
    public void interact() {
        try {
            channel = AsynchronousSocketChannel.open();

            SocketAddress serverAddr = new InetSocketAddress(this.host, this.port);

            channel.connect(serverAddr, channel, this);

            Thread.currentThread().join();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        // TODO:
    }


    public void onDataReceived(byte[] payload) {
        ByteArrayInputStream in = new ByteArrayInputStream(payload);
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(in);
            Packet packet = (Packet) is.readObject();
            app.handlePacket(packet);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void sendPacketInternal(ByteBuffer bufferOut) {
        if (attachment.bufferOut != bufferOut)
            attachment.bufferOut = bufferOut;

        attachment.socketChannel.write(attachment.bufferOut, attachment, attachment.writeHandler);
    }


    @Override
    protected ByteBuffer getBufferOut() {
        return attachment.bufferOut;
    }
}

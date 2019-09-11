package com.melardev.xeytanj.services.net.transport.p2p.async.select;

import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.services.net.transport.p2p.async.AbstractAsyncClientService;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class AsyncClientSelectKey extends AbstractAsyncClientService {

    protected final SocketChannel clientSocketChannel;
    private boolean headerOk;

    // For reading
    private final ByteBuffer headerBuffer;
    private ByteBuffer payloadBuffer;

    // For writing
    private ByteBuffer bufferOut;

    public AsyncClientSelectKey(SocketChannel socketChannel) {
        super();
        this.clientSocketChannel = socketChannel;
        headerBuffer = ByteBuffer.allocate(4);
        headerOk = false;
    }


    @Override
    protected ByteBuffer getBufferOut() {
        return bufferOut;
    }

    @Override
    public void interactAsync() {
        // Nothing to do, the client events are triggered on the server class
        // which in turn calls dataReadyForRead
    }

    @Override
    public String getRemoteIp() {
        try {
            return ((InetSocketAddress) clientSocketChannel.getRemoteAddress()).getAddress().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SocketChannel getSocketChannel() {
        return clientSocketChannel;
    }

    public void dataReadyForRead() {
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
                if (payloadBuffer.position() != 0)
                    System.out.println("Error");

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

                Packet packet = SerializationUtils.deserialize(receivedMessageBytes);
                payloadBuffer.clear();
                headerOk = false;

                dispatchPacket(packet);

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
        }
    }

    @Override
    protected void sendPacketInternal(ByteBuffer bufferOut) {
        // AbstractAsyncClient may have allocated a new ByteBuffer if the one we provided
        // was not enough or was null, so make sure to update our reference to the one given as parameter
        // which may OR MAY NOT be the same that we returned in getBufferOut()

        if (this.bufferOut != bufferOut)
            this.bufferOut = bufferOut;
        try {
            int bytesWritten = 0;
            while (bytesWritten < bufferOut.limit()) {
                bytesWritten += clientSocketChannel.write(bufferOut);
            }
            bufferOut.clear();
            onDataSent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

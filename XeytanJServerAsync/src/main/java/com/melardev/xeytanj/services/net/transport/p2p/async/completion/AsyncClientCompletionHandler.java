package com.melardev.xeytanj.services.net.transport.p2p.async.completion;

import com.melardev.xeytanj.services.net.transport.p2p.async.AbstractAsyncClientService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class AsyncClientCompletionHandler extends AbstractAsyncClientService {

    private AttachmentSocket attachment;
    // private final Queue<Packet> packetsPendingForSend;


    public AsyncClientCompletionHandler(AsynchronousSocketChannel client) {
        super();
        // packetsPendingForSend = new ArrayBlockingQueue<>(40);
        attachment = new AttachmentSocket();

        attachment.clientSocketChannel = client;
        attachment.readHandler = new ReadHandler();
        attachment.writeHandler = new WriteHandler();
        attachment.listener = this;
    }

    @Override
    public void interactAsync() {
        // Schedule an async read
        attachment.clientSocketChannel.read(attachment.headerBuffer, attachment, attachment.readHandler);
    }



    @Override
    protected void sendPacketInternal(ByteBuffer bufferOut) {
        if(attachment.bufferOut != bufferOut)
            attachment.bufferOut = bufferOut;

        attachment.clientSocketChannel.write(bufferOut, attachment, attachment.writeHandler);
    }

    @Override
    protected ByteBuffer getBufferOut() {
        return attachment.bufferOut;
    }

    @Override
    public String getRemoteIp() {
        try {
            return ((InetSocketAddress) attachment.clientSocketChannel.getRemoteAddress())
                    .getAddress().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

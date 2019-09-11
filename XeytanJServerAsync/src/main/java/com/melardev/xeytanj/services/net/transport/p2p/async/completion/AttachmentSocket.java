package com.melardev.xeytanj.services.net.transport.p2p.async.completion;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

public class AttachmentSocket {


    public ByteBuffer bufferOut;
    public boolean isSendingPacket = false;

    public AsynchronousServerSocketChannel serverSocketChannel;
    AsynchronousSocketChannel clientSocketChannel;
    private Thread listeningThread;

    public ByteBuffer headerBuffer;

    public ReadHandler readHandler;
    public WriteHandler writeHandler;

    public ByteBuffer payloadBuffer;
    public int payloadLen;

    public AsyncClientCompletionHandler listener;

    public AttachmentSocket() {
        this.headerBuffer = ByteBuffer.allocate(4);
    }

}

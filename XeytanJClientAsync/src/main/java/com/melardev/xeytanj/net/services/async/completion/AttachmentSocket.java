package com.melardev.xeytanj.net.services.async.completion;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class AttachmentSocket {
    public final AsyncCompletionHandlerNetService controller;
    public AsynchronousSocketChannel socketChannel;
    public WriteHandler writeHandler;

    // Read
    public ByteBuffer headerBuffer;
    public boolean isSendingPacket = false;
    public ReadHandler readHandler;
    ByteBuffer payloadBuffer;
    public int payloadLen;

    // Write
    public ByteBuffer bufferOut;

    public AttachmentSocket(AsyncCompletionHandlerNetService controller) {
        this.headerBuffer = ByteBuffer.allocate(4);
        this.controller = controller;
    }
}

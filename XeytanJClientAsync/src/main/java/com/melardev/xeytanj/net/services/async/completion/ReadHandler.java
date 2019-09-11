package com.melardev.xeytanj.net.services.async.completion;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ReadHandler implements CompletionHandler<Integer, AttachmentSocket> {

    @Override
    public void completed(Integer result, AttachmentSocket attachment) {

        if (result <= 0) {
            // TODO: Error handling
            return;
        }


        // Keep reading the header until we got it
        if (attachment.headerBuffer.position() != attachment.headerBuffer.limit()) {
            attachment.socketChannel.read(attachment.headerBuffer, attachment, this);
            return;
        }

        if (attachment.payloadBuffer == null) {
            // Up this point headerBuffer.pos = 4, limit=4
            attachment.headerBuffer.flip();
            // Up this point headerBuffer.pos =0, limit=4
            attachment.payloadLen = attachment.headerBuffer.getInt();
            // Up this point headerBuffer.pos = 4, limit=4
            attachment.payloadBuffer = ByteBuffer.allocate(attachment.payloadLen);
            attachment.socketChannel.read(attachment.payloadBuffer, attachment, this);
            return;
        }

        if (attachment.payloadBuffer.position() == 0) {
            // This is triggered for n packet, where n > 1(second, third, etc packet)
            // Decide if reuse the payloadBuffer or another one should be allocated

            // Up this point headerBuffer.pos = 4, limit=4
            attachment.headerBuffer.flip();
            // Up this point headerBuffer.pos =0, limit=4
            int newPacketSize = attachment.headerBuffer.getInt();
            // Up this point headerBuffer.pos = 4, limit=4
            int currentBufferSize = attachment.payloadBuffer.capacity();

            // What we have can not hold the incoming packet, so create another one
            if (currentBufferSize < newPacketSize)
                attachment.payloadBuffer = ByteBuffer.allocate(newPacketSize);

            // Update the payloadLen
            attachment.payloadLen = newPacketSize;
        }


        // If we have not read the whole packet yet, then schedule another async read
        if (attachment.payloadBuffer.position() != attachment.payloadLen) {
            attachment.socketChannel.read(attachment.payloadBuffer, attachment, this);
            return;
        }

        // Finally, here we are, we have the whole packet
        byte[] payload = new byte[attachment.payloadLen];
        attachment.payloadBuffer.flip();
        // Up this point headerBuffer.pos = 0, limit=attachement.payloadLen
        attachment.payloadBuffer.get(payload);


        attachment.headerBuffer.clear();
        attachment.payloadBuffer.clear();

        attachment.socketChannel.read(attachment.headerBuffer, attachment, this);

        attachment.controller.onDataReceived(payload);

    }

    @Override
    public void failed(Throwable exc, AttachmentSocket attachment) {

    }
}

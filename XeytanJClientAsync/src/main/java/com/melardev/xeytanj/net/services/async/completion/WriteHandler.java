package com.melardev.xeytanj.net.services.async.completion;

import java.nio.channels.CompletionHandler;

public class WriteHandler implements CompletionHandler<Integer, AttachmentSocket> {

    @Override
    public void completed(Integer result, AttachmentSocket attachment) {
        if (attachment.bufferOut.position() != attachment.bufferOut.limit())
            attachment.socketChannel.write(attachment.bufferOut, attachment, this);
        else {
            attachment.bufferOut.clear();
            attachment.controller.onDataSent();
            attachment.isSendingPacket = false;
        }
    }

    @Override
    public void failed(Throwable exc, AttachmentSocket attachment) {

    }
}

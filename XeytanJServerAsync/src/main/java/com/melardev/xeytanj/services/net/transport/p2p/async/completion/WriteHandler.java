package com.melardev.xeytanj.services.net.transport.p2p.async.completion;

import java.nio.channels.CompletionHandler;

public class WriteHandler implements CompletionHandler<Integer, AttachmentSocket> {
    @Override
    public void completed(Integer result, AttachmentSocket attachment) {
        if (attachment.bufferOut.position() != attachment.bufferOut.limit())
            attachment.clientSocketChannel.write(attachment.bufferOut, attachment, this);
        else {
            attachment.isSendingPacket = false;
            attachment.bufferOut.clear();
            attachment.listener.onDataSent();
        }
    }

    @Override
    public void failed(Throwable exc, AttachmentSocket attachment) {

    }
}

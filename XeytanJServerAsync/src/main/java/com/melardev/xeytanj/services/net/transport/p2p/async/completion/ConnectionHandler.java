package com.melardev.xeytanj.services.net.transport.p2p.async.completion;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncServerCompletionHandler> {
    @Override
    public void completed(AsynchronousSocketChannel client, AsyncServerCompletionHandler server) {
        server.onNewConnection(client);
    }

    @Override
    public void failed(Throwable exc, AsyncServerCompletionHandler attachment) {
        exc.printStackTrace();
    }
}

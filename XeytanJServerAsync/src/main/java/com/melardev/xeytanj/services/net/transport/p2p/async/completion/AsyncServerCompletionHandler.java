package com.melardev.xeytanj.services.net.transport.p2p.async.completion;

import com.melardev.xeytanj.services.net.ClientController;
import com.melardev.xeytanj.services.net.transport.AbstractServerService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

@Service("server_sync_completion")
public class AsyncServerCompletionHandler extends AbstractServerService {

    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    private ConnectionHandler connectionHandler;

    public AsyncServerCompletionHandler() {
        connectionHandler = new ConnectionHandler();
    }

    @Override
    protected void startServer() {
        try {
            asynchronousServerSocketChannel =
                    AsynchronousServerSocketChannel.open();

            asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            asynchronousServerSocketChannel.bind(new InetSocketAddress("127.0.0.1", 3002));
            asynchronousServerSocketChannel.accept(this, connectionHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onNewConnection(AsynchronousSocketChannel client) {
        // Schedule an async accept
        asynchronousServerSocketChannel.accept(this, connectionHandler);

        ClientController clientController = new ClientController(this, client, logger, config);
        clients.put(clientController.getClientId(), clientController);
        clientController.interactAsync();
    }

    @Override
    public void stopServer() {
        try {
            if (asynchronousServerSocketChannel != null)
                asynchronousServerSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "server_async_completion";
    }

}

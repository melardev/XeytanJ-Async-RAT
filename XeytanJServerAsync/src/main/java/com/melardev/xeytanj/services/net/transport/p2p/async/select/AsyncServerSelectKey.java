package com.melardev.xeytanj.services.net.transport.p2p.async.select;

import com.melardev.xeytanj.services.net.ClientController;
import com.melardev.xeytanj.services.net.INetworkClientService;
import com.melardev.xeytanj.services.net.transport.AbstractServerService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class AsyncServerSelectKey extends AbstractServerService {


    private Selector selector;

    @Override
    protected void startServer() {
        try {
            // Create new selector
            selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().setReuseAddress(true);
            // By default this is true, so set it to false for nio sockets
            serverSocketChannel.configureBlocking(false);
            InetAddress loopbackAddress = InetAddress.getLoopbackAddress();
            // Bind to localhost and specified port
            serverSocketChannel.socket().bind(new InetSocketAddress(loopbackAddress, 3002));


            // ServerSocketChannel only supports OP_ACCEPT (see ServerSocketChannel::validOps())
            // it makes sense, server can only accept sockets
            int operations = SelectionKey.OP_ACCEPT;

            serverSocketChannel.register(selector, operations);
            while (true) {
                if (selector.select() <= 0) {
                    continue;
                }
                try {
                    processReadySet(selector.selectedKeys());
                } catch (IOException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void processReadySet(Set readySet) throws IOException {
        Iterator iterator = readySet.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            // After processing a key, it still persists in the Set, we wanna remove it
            // otherwise we will get it back the next time processReadySet is called
            // We would end up processing the same "event" as many times this method is called
            iterator.remove();

            if (key.isAcceptable()) {
                ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();

                // Get the client socket channel
                SocketChannel clientSocketChannel = (SocketChannel) ssChannel.accept();

                ClientController clientController = new ClientController(this, clientSocketChannel, logger, config);
                clients.put(clientController.getClientId(), clientController);
                // Configure it as non-blocking socket
                clientSocketChannel.configureBlocking(false);
                // Register the socket with the key selector, we want to get notified when we have
                // something to read from socket(OP_READ)
                clientSocketChannel.register(key.selector(), SelectionKey.OP_READ);

            } else if (key.isReadable()) {
                // Get the socket who sent the message
                SocketChannel sender = (SocketChannel) key.channel();
                AsyncClientSelectKey clientSelectKey = getClientSelectBySocketChannel(sender);
                if (clientSelectKey == null)
                    return;

                clientSelectKey.dataReadyForRead();
            }

        }
    }

    private AsyncClientSelectKey getClientSelectBySocketChannel(SocketChannel sender) {
        for (ClientController controller : clients.values()) {
            INetworkClientService clientService = controller.getClientContext().getNetClientService();
            if (clientService.getClass() == AsyncClientSelectKey.class) {
                if (((AsyncClientSelectKey) clientService).getSocketChannel().equals(sender)) {
                    return (AsyncClientSelectKey) clientService;
                }
            }
        }
        return null;
    }


    @Override
    public void stopServer() {

    }

    @Override
    public String getName() {
        return "server_async_select";
    }
}

package com.melardev.xeytanj.net.services.async;

import com.melardev.xeytanj.config.IConfigService;
import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.services.NetClientService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class AbstractAsyncClientService implements NetClientService {
    private final ILogger logger;
    private final IConfigService config;
    protected boolean isSendingPacket;
    private ArrayBlockingQueue<Packet> packetsPendingForSend;

    final int PACKET_LENGTH_SIZE = 4;

    public AbstractAsyncClientService(ILogger logger, IConfigService config) {
        this.isSendingPacket = false;
        packetsPendingForSend = new ArrayBlockingQueue<>(40);
        this.logger = logger;
        this.config = config;
    }

    @Override
    public void sendPacket(Packet packet) {

        if (isSendingPacket) {
            packetsPendingForSend.add(packet);
            return;
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {


            // I assume the bufferOut is clear() ed, we cleared it at the end of this function
            objectOutputStream.writeObject(packet);
            byte[] serialized = byteArrayOutputStream.toByteArray();

            ByteBuffer bufferOut = getBufferOut();
            // If this is the first packet we sent, the bufferOut is null, create a buffer
            // big enough to hold this first packet
            // || when not first packet, check if the bufferOut we had is enough to hold this new packet
            // if not, create one big enough
            if (bufferOut == null
                    ||
                    bufferOut.capacity() < (PACKET_LENGTH_SIZE + serialized.length))
                bufferOut = ByteBuffer.allocate(PACKET_LENGTH_SIZE + serialized.length);

            // Check if bufferOut we had before may hold this new packet, if yes reuse it, if no,
            // create a new one big enough

            bufferOut.putInt(serialized.length);
            bufferOut.put(serialized);
            bufferOut.flip();
            isSendingPacket = true;

            sendPacketInternal(bufferOut);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void onDataSent() {
        // We just finished sending a packet, let's send another one if available
        isSendingPacket = false;
        Packet packet = packetsPendingForSend.poll();
        if (packet != null)
            sendPacket(packet);
    }

    /**
     * Called to use the specific API needed to write the bytes into a socket
     * The argument is a ByteBuffer containing the data, it is appropriately setup, no need to flip()
     * adjust position or write additional data, everything is done before, just send the buffer
     *
     * @param bufferOut
     */
    protected abstract void sendPacketInternal(ByteBuffer bufferOut);

    protected abstract ByteBuffer getBufferOut();
}

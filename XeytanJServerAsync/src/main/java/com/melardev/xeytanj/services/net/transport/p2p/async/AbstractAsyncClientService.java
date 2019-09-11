package com.melardev.xeytanj.services.net.transport.p2p.async;

import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.services.net.transport.p2p.AbstractClientService;
import org.apache.commons.lang3.SerializationUtils;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class AbstractAsyncClientService extends AbstractClientService {

    protected boolean isSendingPacket;
    protected final Queue<Packet> packetsPendingForSend;

    public AbstractAsyncClientService() {
        packetsPendingForSend = new ArrayBlockingQueue<>(40);
        isSendingPacket = false;
    }


    @Override
    protected void sendPacket(Packet packet) {

        if (isSendingPacket) {
            packetsPendingForSend.add(packet);
            return;
        }

        // I assume the bufferOut is clear() ed, we cleared it at the end of this function
        byte[] serialized = SerializationUtils.serialize(packet);
        ByteBuffer bufferOut = getBufferOut();
        // If this is the first packet we sent, the bufferOut is null, create a buffer
        // big enough to hold this first packet
        // || when not first packet, check if the bufferOut we had is enough to hold this new packet
        // if not, create one big enough
        if (bufferOut == null
                ||
                bufferOut.capacity() < (4 + serialized.length))
            bufferOut = ByteBuffer.allocate(4 + serialized.length);

        // Check if bufferOut we had before may hold this new packet, if yes reuse it, if no,
        // create a new one big enough

        bufferOut.putInt(serialized.length);
        bufferOut.put(serialized);
        bufferOut.flip();
        isSendingPacket = true;

        sendPacketInternal(bufferOut);
    }

    public void onDataSent() {
        // We just finished sending a packet, let's send another one if available
        isSendingPacket = false;
        Packet packet = packetsPendingForSend.poll();
        if (packet != null)
            sendPacket(packet);
    }

    protected abstract void sendPacketInternal(ByteBuffer bufferOut);

    protected abstract ByteBuffer getBufferOut();
}

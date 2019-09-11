package com.melardev.xeytanj.services.net.transport.p2p.services;


/*
public class XeytanVoipThread implements RemoteMediaPacketHandler<PacketVoice> {

    //    private SoundPlayer gui;
    private PacketVoice.DataType dataType;
    private Socket socket;
    private MediaState mediaState;

    public XeytanVoipThread() {

    }

    public void initService() {
       /* Packet packet;
        try {
            while (true) {
                ObjectInputStream sockIn = new ObjectInputStream(socket.getInputStream());
                packet = (PacketVoice) sockIn.readObject();
                if (packet.get() == PacketVoice.DataType.FILE) {
                    while (!gui.completed)
                        continue;
                    if (com.melardev.net.packet instanceof PacketFile)
                        gui.playFile(((PacketFile) com.melardev.net.packet).data);
                } else if (protocol == PacketVoice.DataType.RAW) {
                    if (com.melardev.net.packet instanceof PacketVoice) {
                        PacketVoice pv = (PacketVoice) com.melardev.net.packet;
                        if (Arrays.equals(pv.data, pv.dataS.getBytes())) {
                            System.out.println("not the same");
                        }
                        gui.playRaw(pv.data, pv.bytesRead);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*//*
    }

    @Override
    public void pause() {
        mediaState = MediaState.PAUSED;
    }

    @Override
    public boolean isAlreadyStreaming() {
        return mediaState != null && mediaState == MediaState.STREAMING;
    }

    @Override
    public MediaState getMediaState() {
        return mediaState;
    }

    @Override
    public void startSession() {

    }

    @Override
    public void stopSession() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ServiceType getSubject() {
        return null;
    }

    @Override
    public void handlePacket(PacketVoice packet) {
        packet.getData();
    }
}
*/
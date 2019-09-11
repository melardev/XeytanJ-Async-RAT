package com.melardev.xeytanj.net.packets;

public class PacketLogin extends Packet {

    private String os;
    private String pcName;

    private String loginKey;
    private LoginType loginType;
    private String localIp;

    public static enum LoginType {
        LOGIN_REQUEST, LOGIN_RESPONSE
    }

    public PacketLogin(String loginKey, LoginType loginType) {
        super(PacketType.LOGIN);
        this.loginKey = loginKey;
        this.loginType = loginType;
    }

    public String getlocalIp() {
        return localIp;
    }


    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getPcName() {
        return pcName;
    }

    public void setPcName(String pcName) {
        this.pcName = pcName;
    }

    public String getLoginKey() {
        return loginKey;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public String getKey() {
        return loginKey;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }
}

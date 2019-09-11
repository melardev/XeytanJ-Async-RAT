package com.melardev.xeytanj.models;

import com.melardev.xeytanj.maps.ClientGeoStructure;

import java.util.Map;
import java.util.UUID;

public class ClientInfoStructure {

    //public Map<String, String> map;
    public Map<String, String> properties;
    public Map<String, String> env;
    public String globalIp;
    public String localIp;
    private UUID id;
    private String os;
    private String country;
    private String pcName;
    private ClientGeoStructure geoData;

    public ClientInfoStructure(UUID id, Map<String, String> _properties, Map<String, String> _env,
                               String _remoteIp, String _localIp, String country, String os) {
        //map =_map;
        this.id = id;
        this.country = country;
        this.os = os;
        properties = _properties;
        env = _env;
        globalIp = _remoteIp;
        localIp = _localIp;
    }

    public ClientInfoStructure(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public String getOs() {
        return os;
    }

    public String getGlobalIp() {
        return globalIp;
    }

    public String getLocalIp() {
        return localIp;
    }

    public String getPcName() {
        return pcName;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setPcName(String pcName) {
        this.pcName = pcName;
    }

    public void setGlobalIp(String globalIp) {
        this.globalIp = globalIp;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public ClientGeoStructure getGeoData() {
        return geoData;
    }

    public void setGeoData(ClientGeoStructure map) {
        this.geoData = map;
    }

}

package com.melardev.xeytanj.models;

import com.melardev.xeytanj.enums.LoadedFrom;
import com.melardev.xeytanj.maps.ClientGeoStructure;

import java.util.UUID;

import static com.melardev.xeytanj.enums.LoadedFrom.FROM_DATABASE;

public class Client {
    private String os;
    private String pcName;
    private UUID id;
    private String globalIp;
    private String localIp;
    private ClientGeoStructure geoData;
    private String jreVersion;
    private LoadedFrom loadedFrom;

    public Client(UUID id) {
        this.id = id;
        geoData = new ClientGeoStructure();
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setPcName(String pcName) {
        this.pcName = pcName;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setGlobalIp(String globalIp) {
        this.globalIp = globalIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }


    public String getOs() {
        return os;
    }

    public String getPcName() {
        return pcName;
    }

    public UUID getId() {
        return id;
    }

    public String getGlobalIp() {
        return globalIp;
    }

    public String getLocalIp() {
        return localIp;
    }

    public String getJreVersion() {
        return jreVersion;
    }

    public void setJreVersion(String version) {
        this.jreVersion = version;
    }

    public ClientGeoStructure getGeoData() {
        return geoData;
    }

    public void setGeoData(ClientGeoStructure geoData) {
        this.geoData = geoData;
    }

    public LoadedFrom getLoadedFrom() {
        return loadedFrom;
    }

    public boolean isLoadedFromDb() {
        return loadedFrom == FROM_DATABASE;
    }
}

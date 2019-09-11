package com.melardev.xeytanj.services.net;

import com.melardev.xeytanj.models.ClientInfoStructure;

import java.util.Map;
import java.util.UUID;

public class ClientContext {
    private INetworkClientService netClientService;
    private Map<String, String> env;
    private Map<String, String> properties;
    private ClientInfoStructure clientInfoStructure;
    private INetworkServerService netServerService;

    public ClientContext(UUID id) {
        clientInfoStructure = new ClientInfoStructure(id);
    }

    public void setNetClientService(INetworkClientService networkClientService) {
        this.netClientService = networkClientService;
    }

    public INetworkClientService getNetClientService() {
        return netClientService;
    }

    public void setProps(Map<String, String> properties) {
        this.properties = properties;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public ClientInfoStructure getInfoStructure() {
        return clientInfoStructure;
    }

    public void setClientStructure(ClientInfoStructure clientStructure) {
        this.clientInfoStructure = clientStructure;
    }

    public void setNetServerService(INetworkServerService netServerService) {
        this.netServerService=netServerService;
    }

    public INetworkServerService getNetServerService() {
        return netServerService;
    }
}

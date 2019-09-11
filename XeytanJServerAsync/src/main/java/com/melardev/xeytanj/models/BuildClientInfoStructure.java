package com.melardev.xeytanj.models;

import java.io.File;

public class BuildClientInfoStructure {

    private final String key;
    private final boolean persistenceEnabled;
    private final File outputFile;
    private String serverHost;
    private String serverPort;
    private long connectionRetry;

    public File getOutputFile() {
        return outputFile;
    }

    public BuildClientInfoStructure(String serverHost, String serverPort, long millisecondsRetry, String key, boolean persistenceEnabled, File outputFile) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.connectionRetry = millisecondsRetry;
        this.key = key;
        this.persistenceEnabled = persistenceEnabled;
        this.outputFile = outputFile;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public long getConnectionRetry() {
        return connectionRetry;
    }

    public void setConnectionRetry(long connectionRetry) {
        this.connectionRetry = connectionRetry;
    }

    public String getKey() {
        return key;
    }

    public boolean isPersistenceEnabled() {
        return persistenceEnabled;
    }
}

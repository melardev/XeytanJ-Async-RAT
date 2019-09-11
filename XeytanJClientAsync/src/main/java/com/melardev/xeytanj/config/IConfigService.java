package com.melardev.xeytanj.config;

public interface IConfigService {

    String getLoginKey();

    String getServerHost();

    String getServerPort();

    boolean isPersistenceEnabled();
}

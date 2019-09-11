package com.melardev.xeytanj.services.logger;

import com.melardev.xeytanj.services.IService;

public interface ILogger extends IService {
    public void debug(String s);
    public void wran(String s);
    public void error(String s);
    public void errorFormat(String s, String... args);
    public void trace(String s);

    void traceCurrentMethodName();
}

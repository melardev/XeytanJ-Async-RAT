package com.melardev.xeytanj.utils;

import java.net.URL;

public class EnvUtils {

    public static boolean runningInJar() {

        // jar:com.melardev.... is Jar
        // file:com.melardev... is File
        URL url = EnvUtils.class.getResource("EnvUtils.class");
        return url.getProtocol().startsWith("jar:");
    }
}

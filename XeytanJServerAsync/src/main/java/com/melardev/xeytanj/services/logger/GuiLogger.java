package com.melardev.xeytanj.services.logger;

import javax.swing.*;

public class GuiLogger implements ILogger {
    private final JFrame frame;

    public GuiLogger() {
        frame = new JFrame();


    }

    @Override
    public void debug(String s) {

    }

    @Override
    public void wran(String s) {

    }

    @Override
    public void error(String s) {

    }

    @Override
    public void errorFormat(String s, String... args) {

    }

    @Override
    public void trace(String s) {

    }

    @Override
    public void traceCurrentMethodName() {

    }
}

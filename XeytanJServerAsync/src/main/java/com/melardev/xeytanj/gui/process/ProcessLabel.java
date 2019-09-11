package com.melardev.xeytanj.gui.process;

import javax.swing.*;

public class ProcessLabel extends JLabel {

    public ProcessLabel(String text) {
        super(text);
    }

    public ProcessLabel(Icon imageIcon, String text) {
        super(text);
        if (imageIcon != null)
            setIcon(imageIcon);
    }

    @Override
    public String toString() {
        return getText();
    }
}


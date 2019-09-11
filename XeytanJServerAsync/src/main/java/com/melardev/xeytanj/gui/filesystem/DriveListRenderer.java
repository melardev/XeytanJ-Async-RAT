package com.melardev.xeytanj.gui.filesystem;

import com.melardev.xeytanj.models.FileInfoStructure;

import javax.swing.*;
import java.awt.*;

public class DriveListRenderer extends DefaultListCellRenderer {

    public DriveListRenderer() {
        setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> paramJList, Object paramObject, int paramInt,
                                                  boolean paramBoolean1, boolean paramBoolean2) {
        FileInfoStructure original = (FileInfoStructure) paramObject;
        JLabel label = (JLabel) super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
        label.setIcon(original.getIcon());
        label.setText(original.fullPath);
        return label;
    }
}

package com.melardev.xeytanj.gui.keylog;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.text.DefaultCaret;

public class KeylogsUI extends JFrame {

    private JTextArea txtLogs;

    public KeylogsUI() {

        txtLogs = new JTextArea();
        txtLogs.setEditable(false);
        DefaultCaret caret = (DefaultCaret) txtLogs.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollLogs = new JScrollPane(txtLogs);
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(0)
                                .addComponent(scrollLogs, GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                                .addGap(428))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(0)
                                .addComponent(scrollLogs)
                                .addGap(237))
        );
        groupLayout.setAutoCreateContainerGaps(false);
        groupLayout.setAutoCreateGaps(false);
        getContentPane().setLayout(groupLayout);
        setVisible(true);

    }

    public static void main(String[] args) {
        KeylogsUI ui = new KeylogsUI();
        ui.pack();
        ui.setVisible(true);
    }

    public void appendMsg(String s) {
        txtLogs.append(s);
    }

    public void setText(String logs) {
        txtLogs.setText(logs);
    }
}

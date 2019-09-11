package com.melardev.xeytanj.gui.chat;


import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.gui.IGuiUserOwned;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.IAppMessages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatGUI extends JFrame implements IGuiUserOwned<ChatUiListener> {

    private JTextArea txtMsg;
    private JTextArea txtChat;
    private Client client;
    private ChatUiListener listener;
    private IUiMediator mediator;
    private IAppMessages messageProvider;


    public ChatGUI() {
        setupUi();
    }

    private void setupUi() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                notifyMediatorOnClose();
            }
        });


        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{53, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{7, 571, 41, 47, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        txtChat = new JTextArea();
        GridBagConstraints gbc_txtChat = new GridBagConstraints();
        gbc_txtChat.gridwidth = 2;
        gbc_txtChat.insets = new Insets(0, 0, 5, 5);
        gbc_txtChat.fill = GridBagConstraints.BOTH;
        gbc_txtChat.gridx = 1;
        gbc_txtChat.gridy = 1;

        JScrollPane scrollChat = new JScrollPane(txtChat);
        getContentPane().add(scrollChat, gbc_txtChat);
                GridBagConstraints gbc_txtMsg_1_1 = new GridBagConstraints();
                gbc_txtMsg_1_1.insets = new Insets(0, 0, 5, 5);
                gbc_txtMsg_1_1.fill = GridBagConstraints.BOTH;
                gbc_txtMsg_1_1.gridx = 1;
                gbc_txtMsg_1_1.gridy = 3;
                
                        JScrollPane scrollMsg = new JScrollPane();
                        getContentPane().add(scrollMsg, gbc_txtMsg_1_1);
                        
                                txtMsg = new JTextArea();
                                scrollMsg.setViewportView(txtMsg);
                                txtMsg.addKeyListener(new KeyAdapter() {

                                    @Override
                                    public void keyPressed(KeyEvent e) {
                                        super.keyPressed(e);
                                        if (e.getKeyCode() == KeyEvent.VK_ENTER)
                                            onSendTriggered();

                                    }

                                });
        
                JButton btnSend = new JButton("Send");
                GridBagConstraints gbc_btnSend = new GridBagConstraints();
                gbc_btnSend.fill = GridBagConstraints.BOTH;
                gbc_btnSend.insets = new Insets(0, 0, 5, 5);
                gbc_btnSend.gridx = 2;
                gbc_btnSend.gridy = 3;
                getContentPane().add(btnSend, gbc_btnSend);
        btnSend.addActionListener((ActionEvent e) -> {
            onSendTriggered();
        });
        setSize(1204, 706);
        setResizable(false);
        setTitle("Master");
    }

    private void onSendTriggered() {
        String msg = txtMsg.getText().trim();
        appendMsg("Me: " + msg + "\n");
        txtMsg.setText("");
        getMediator().onSendChatMessage(getClient(), msg);

    }

    public void appendMsg(String message) {
        if (!message.endsWith("\n"))
            txtChat.append(client.getPcName() + ": " + message + "\n");
        else
            txtChat.append(client.getPcName() + ": " + message);
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public Subject getSubject() {
        return Subject.CHAT;
    }

    @Override
    public void setStatus(String status) {

    }

    @Override
    public void disableUi() {

    }

    @Override
    public void display() {
        setVisible(true);
    }

    @Override
    public void addListener(ChatUiListener listener) {
        this.listener = listener;
    }

    @Override
    public ChatUiListener getListener() {
        return this.listener;
    }

    @Override
    public IUiMediator getMediator() {
        return mediator;
    }

    @Override
    public void setMediator(IUiMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void resetState() {

    }

    @Override
    public void setMessageProvider(IAppMessages messageProvider) {
        this.messageProvider = messageProvider;
    }

}

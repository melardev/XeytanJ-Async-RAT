package com.melardev.xeytanj.gui.disclaimer;

import com.melardev.xeytanj.gui.IGui;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.services.IAppMessages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class DisclaimerDialog extends JDialog implements IGui<DisclaimerUiListener> {

    private static final String DISCLAIMER_TEXT = null;

    private JTextArea txtDisclaimer;
    private JButton btnAccept;
    private JButton btnDecline;
    private DisclaimerUiListener listener;
    private IUiMediator mediator;
    private IAppMessages messageProvider;

    public DisclaimerDialog() {

    }

    public void display() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        txtDisclaimer = new JTextArea();
        txtDisclaimer.setMaximumSize(new Dimension(400, 600));
        txtDisclaimer.setText(messageProvider.getText("gui.disclaimer.text"));
        GridBagConstraints gbc_textArea = new GridBagConstraints();
        gbc_textArea.insets = new Insets(0, 0, 5, 5);
        gbc_textArea.fill = GridBagConstraints.BOTH;
        gbc_textArea.gridx = 1;
        gbc_textArea.gridy = 1;
        getContentPane().add(txtDisclaimer, gbc_textArea);

        Box horizontalBox = Box.createHorizontalBox();
        GridBagConstraints gbc_horizontalBox = new GridBagConstraints();
        gbc_horizontalBox.insets = new Insets(0, 0, 5, 5);
        gbc_horizontalBox.gridx = 1;
        gbc_horizontalBox.gridy = 3;
        getContentPane().add(horizontalBox, gbc_horizontalBox);

        btnAccept = new JButton("Accept");
        MyActionListener listener = new MyActionListener();
        btnAccept.addActionListener(listener);
        btnAccept.setEnabled(false);
        horizontalBox.add(btnAccept);

        btnDecline = new JButton(messageProvider.getText("gui.disclaimer.btn.refuse"));
        btnDecline.addActionListener(listener);
        horizontalBox.add(btnDecline);

        //setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        pack();

        new Timer("disclaimerTimer").schedule(new TimerTask() {
            int count = 10;

            @Override
            public void run() {
                btnAccept.setText(String.format("%s(%d)", messageProvider.getText("gui.disclaimer.btn.accept"), --count));
                if (count < 0) {
                    btnAccept.setEnabled(true);
                    btnAccept.setText(messageProvider.getText("gui.disclaimer.btn.accept"));
                    cancel();
                }
            }
        }, 0, 1000);
        setVisible(true);
    }


    public class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == btnAccept)
                mediator.onDisclaimerAccepted();
            else
                mediator.onDisclaimerRefused();
            //dispose();
        }
    }

    private void notifyDisclaimerAccepted() {
        mediator.onDisclaimerAccepted();
    }

    @Override
    public void addListener(DisclaimerUiListener listener) {
        this.listener = listener;
    }

    @Override
    public DisclaimerUiListener getListener() {
        return listener;
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

package com.melardev.xeytanj.gui.chat;

import com.melardev.xeytanj.services.ChatHandler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatGUI extends JFrame {

	private JTextArea txtMsg;
	private JTextArea txtChat;
	private ChatHandler handler;

	public ChatGUI(ChatHandler ch) {
		handler = ch;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 53, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 7, 611, 28, 63, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		txtChat = new JTextArea();
		GridBagConstraints gbc_txtChat = new GridBagConstraints();
		gbc_txtChat.insets = new Insets(0, 0, 5, 5);
		gbc_txtChat.fill = GridBagConstraints.BOTH;
		gbc_txtChat.gridx = 1;
		gbc_txtChat.gridy = 1;

		JScrollPane scrollChat = new JScrollPane(txtChat);
		getContentPane().add(scrollChat, gbc_txtChat);

		txtMsg = new JTextArea();
		GridBagConstraints gbc_txtMsg = new GridBagConstraints();
		gbc_txtMsg.insets = new Insets(0, 0, 5, 5);
		gbc_txtMsg.fill = GridBagConstraints.BOTH;
		gbc_txtMsg.gridx = 1;
		gbc_txtMsg.gridy = 3;

		JScrollPane scrollMsg = new JScrollPane(txtMsg);
		txtMsg.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					onSendTriggered();
			}

		});
		getContentPane().add(scrollMsg, gbc_txtMsg);

		JButton btnSend = new JButton("Send");
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 5, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 3;
		getContentPane().add(btnSend, gbc_btnSend);
		btnSend.addActionListener((ActionEvent e) -> {
			onSendTriggered();
		});

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent paramWindowEvent) {
				closeWindow();
				super.windowClosing(paramWindowEvent);
			}

		});
		setSize(1204, 706);
		setResizable(false);
		setTitle("Client");
		setVisible(true);
	}

	private void onSendTriggered() {
		String msg = txtMsg.getText().trim();
		handler.sendMessage(msg);
		appendMsg("Me: " + msg + "\n");
		txtMsg.setText("");

	}

	public void appendMsg(String message) {
		txtChat.append(message);
	}

	public void closeWindow() {
		setVisible(false);
		dispose();
		handler.onGuiDispose();
	}

	public void setHanlder(ChatHandler chatHandler) {
		handler = chatHandler;
	}
}

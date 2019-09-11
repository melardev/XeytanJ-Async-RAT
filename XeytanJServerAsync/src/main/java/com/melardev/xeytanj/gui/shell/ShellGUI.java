package com.melardev.xeytanj.gui.shell;

import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.gui.IGuiUserOwned;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.IAppMessages;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ShellGUI extends JFrame implements IGuiUserOwned<ShellUiListener> {

	private JScrollPane scrollCmd;
	private JTextField txtCmd;
	private JTextArea txtResults;
	private ArrayList<String> historyCmd;
	private int cursorHistory;
	private ShellUiListener listener;
	private IUiMediator mediator;
	private Client client;
	private IAppMessages messageProvider;

	public ShellGUI() {

		setupUi();
	}

	private void setupUi() {
		historyCmd = new ArrayList<String>();
        cursorHistory = 0;
        // caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 61, 119, 153, 0, 180, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 388, 18, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);
                txtResults = new JTextArea();
                txtResults.setEditable(false);
                JScrollPane scrollResults = new JScrollPane(txtResults);
                DefaultCaret caret = (DefaultCaret) txtResults.getCaret();
                GridBagConstraints gbc_scrollResults = new GridBagConstraints();
                gbc_scrollResults.fill = GridBagConstraints.BOTH;
                gbc_scrollResults.insets = new Insets(0, 0, 5, 5);
                gbc_scrollResults.gridwidth = 6;
                gbc_scrollResults.gridx = 1;
                gbc_scrollResults.gridy = 1;
                getContentPane().add(scrollResults, gbc_scrollResults);
                        scrollCmd = new JScrollPane();
                        GridBagConstraints gbc_scrollCmd = new GridBagConstraints();
                        gbc_scrollCmd.gridwidth = 4;
                        gbc_scrollCmd.fill = GridBagConstraints.BOTH;
                        gbc_scrollCmd.insets = new Insets(0, 0, 0, 5);
                        gbc_scrollCmd.gridx = 1;
                        gbc_scrollCmd.gridy = 2;
                        getContentPane().add(scrollCmd, gbc_scrollCmd);
                        txtCmd = new JTextField();
                        scrollCmd.setViewportView(txtCmd);
                        
                                txtCmd.addKeyListener(new KeyAdapter() {
                        
                                    @Override
                                    public void keyPressed(KeyEvent e) {
                                        super.keyPressed(e);
                                        if (e.getKeyCode() == KeyEvent.VK_UP) {
                                            cursorHistory = cursorHistory > 1 ? cursorHistory-- : 0;
                                            txtCmd.setText(historyCmd.get(cursorHistory));
                                        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                                            if (cursorHistory >= historyCmd.size() - 1)
                                                cursorHistory++;
                                            txtCmd.setText(historyCmd.get(cursorHistory));
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                            onSendTrigger();
                                        }
                                    }
                        
                                });
                        
                                JButton btnSend = new JButton("Send");
                                btnSend.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent args) {
                                        onSendTrigger();
                                    }
                                });
                                GridBagConstraints gbc_btnSend = new GridBagConstraints();
                                gbc_btnSend.insets = new Insets(0, 0, 0, 5);
                                gbc_btnSend.fill = GridBagConstraints.BOTH;
                                gbc_btnSend.gridx = 5;
                                gbc_btnSend.gridy = 2;
                                getContentPane().add(btnSend, gbc_btnSend);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                notifyMediatorOnClose();
            }
        });

	}

	protected void onSendTrigger() {
		getMediator().onSendShellCommandRequested(getClient(), txtCmd.getText());
		historyCmd.add(txtCmd.getText());
		if (txtCmd.getText() != "") {
			cursorHistory++;
			historyCmd.add(txtCmd.getText());
		}
		txtCmd.setText("");
	}

	public void appendText(String results) {
		txtResults.append(results);
	}

	@Override
	public void display() {
		pack();
		setVisible(true);

	}

	@Override
	public void addListener(ShellUiListener shellUiListener) {
		this.listener = shellUiListener;
	}

	@Override
	public ShellUiListener getListener() {
		return null;
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
	public Client getClient() {
		return this.client;
	}

	@Override
	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public Subject getSubject() {
		return Subject.REVERSE_SHELL;
	}

	@Override
	public void setStatus(String status) {

	}

	@Override
	public void disableUi() {

	}

	public void notifyDisconnected() {
		txtResults.append("User is Disconnected\n");
		txtResults.setBackground(Color.GRAY);
	}

	@Override
	public void setMessageProvider(IAppMessages messageProvider) {
		this.messageProvider = messageProvider;
	}

}

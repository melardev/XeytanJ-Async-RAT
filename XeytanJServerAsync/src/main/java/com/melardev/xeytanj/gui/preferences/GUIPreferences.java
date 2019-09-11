package com.melardev.xeytanj.gui.preferences;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUIPreferences extends JDialog implements ActionListener {
    private JTextField textField;
    private JTextField txtGoogleKey;
    private JTextField txtMySQLHost;
    private JTextField txtMySQLUser;
    private JTextField txtMySQLPassword;
    private JButton btnCancel;
    private JCheckBox chckbxUseMysql;
    private JTextField txtMySQLPort;
    private boolean firstTime = true;
    private JCheckBox chckbxUseGoogleMaps;
    private JButton btnSave;

    public GUIPreferences() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
        gbc_tabbedPane.insets = new Insets(0, 0, 5, 5);
        gbc_tabbedPane.fill = GridBagConstraints.BOTH;
        gbc_tabbedPane.gridx = 0;
        gbc_tabbedPane.gridy = 0;
        getContentPane().add(tabbedPane, gbc_tabbedPane);

        JPanel panelGeo = new JPanel();
        tabbedPane.addTab("Geo Config", null, panelGeo, null);
        GridBagLayout gbl_panelGeo = new GridBagLayout();
        gbl_panelGeo.columnWidths = new int[]{0, 0, 149, 0, 0, 0, 0};
        gbl_panelGeo.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_panelGeo.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panelGeo.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        panelGeo.setLayout(gbl_panelGeo);

        chckbxUseGoogleMaps = new JCheckBox("Use Google Maps");
        GridBagConstraints gbc_chckbxUseGoogleMaps = new GridBagConstraints();
        gbc_chckbxUseGoogleMaps.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxUseGoogleMaps.gridx = 2;
        gbc_chckbxUseGoogleMaps.gridy = 2;
        panelGeo.add(chckbxUseGoogleMaps, gbc_chckbxUseGoogleMaps);
        chckbxUseGoogleMaps.addActionListener(this);

        JLabel lblPublicKeyGoogle = new JLabel("Public Key Google Maps");
        GridBagConstraints gbc_lblPublicKeyGoogle = new GridBagConstraints();
        gbc_lblPublicKeyGoogle.insets = new Insets(0, 0, 5, 5);
        gbc_lblPublicKeyGoogle.anchor = GridBagConstraints.EAST;
        gbc_lblPublicKeyGoogle.gridx = 1;
        gbc_lblPublicKeyGoogle.gridy = 3;
        panelGeo.add(lblPublicKeyGoogle, gbc_lblPublicKeyGoogle);

        txtGoogleKey = new JTextField();
        GridBagConstraints gbc_txtGoogleKey = new GridBagConstraints();
        gbc_txtGoogleKey.insets = new Insets(0, 0, 5, 5);
        gbc_txtGoogleKey.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtGoogleKey.gridx = 2;
        gbc_txtGoogleKey.gridy = 3;
        panelGeo.add(txtGoogleKey, gbc_txtGoogleKey);
        txtGoogleKey.setColumns(10);

        chckbxUseMysql = new JCheckBox("Use MySQL");
        GridBagConstraints gbc_chckbxUseMysql = new GridBagConstraints();
        gbc_chckbxUseMysql.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxUseMysql.gridx = 2;
        gbc_chckbxUseMysql.gridy = 4;
        panelGeo.add(chckbxUseMysql, gbc_chckbxUseMysql);
        chckbxUseMysql.addActionListener(this);

        JLabel lblHost = new JLabel("Host");
        GridBagConstraints gbc_lblHost = new GridBagConstraints();
        gbc_lblHost.insets = new Insets(0, 0, 5, 5);
        gbc_lblHost.anchor = GridBagConstraints.EAST;
        gbc_lblHost.gridx = 1;
        gbc_lblHost.gridy = 5;
        panelGeo.add(lblHost, gbc_lblHost);

        txtMySQLHost = new JTextField();
        GridBagConstraints gbc_txtMySQLHost = new GridBagConstraints();
        gbc_txtMySQLHost.insets = new Insets(0, 0, 5, 5);
        gbc_txtMySQLHost.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtMySQLHost.gridx = 2;
        gbc_txtMySQLHost.gridy = 5;
        panelGeo.add(txtMySQLHost, gbc_txtMySQLHost);
        txtMySQLHost.setColumns(10);

        JLabel lblPort = new JLabel("port");
        GridBagConstraints gbc_lblPort = new GridBagConstraints();
        gbc_lblPort.anchor = GridBagConstraints.EAST;
        gbc_lblPort.insets = new Insets(0, 0, 5, 5);
        gbc_lblPort.gridx = 1;
        gbc_lblPort.gridy = 6;
        panelGeo.add(lblPort, gbc_lblPort);

        txtMySQLPort = new JTextField();
        GridBagConstraints gbc_txtMySQLPort = new GridBagConstraints();
        gbc_txtMySQLPort.insets = new Insets(0, 0, 5, 5);
        gbc_txtMySQLPort.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtMySQLPort.gridx = 2;
        gbc_txtMySQLPort.gridy = 6;
        panelGeo.add(txtMySQLPort, gbc_txtMySQLPort);
        txtMySQLPort.setColumns(10);

        JLabel lblUsername = new JLabel("Username");
        GridBagConstraints gbc_lblUsername = new GridBagConstraints();
        gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
        gbc_lblUsername.anchor = GridBagConstraints.EAST;
        gbc_lblUsername.gridx = 1;
        gbc_lblUsername.gridy = 7;
        panelGeo.add(lblUsername, gbc_lblUsername);

        txtMySQLUser = new JTextField();
        GridBagConstraints gbc_txtMySQLUser = new GridBagConstraints();
        gbc_txtMySQLUser.insets = new Insets(0, 0, 5, 5);
        gbc_txtMySQLUser.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtMySQLUser.gridx = 2;
        gbc_txtMySQLUser.gridy = 7;
        panelGeo.add(txtMySQLUser, gbc_txtMySQLUser);
        txtMySQLUser.setColumns(10);

        JLabel lblPassword = new JLabel("Password");
        GridBagConstraints gbc_lblPassword = new GridBagConstraints();
        gbc_lblPassword.anchor = GridBagConstraints.EAST;
        gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
        gbc_lblPassword.gridx = 1;
        gbc_lblPassword.gridy = 8;
        panelGeo.add(lblPassword, gbc_lblPassword);

        txtMySQLPassword = new JTextField();
        GridBagConstraints gbc_txtMySQLPassword = new GridBagConstraints();
        gbc_txtMySQLPassword.insets = new Insets(0, 0, 5, 5);
        gbc_txtMySQLPassword.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtMySQLPassword.gridx = 2;
        gbc_txtMySQLPassword.gridy = 8;
        panelGeo.add(txtMySQLPassword, gbc_txtMySQLPassword);
        txtMySQLPassword.setColumns(10);
        JPanel panelLang = new JPanel();
        tabbedPane.addTab("Language", null, panelLang, null);
        JPanel panelDefaults = new JPanel();
        tabbedPane.addTab("Defaults", null, panelDefaults, null);
        GridBagLayout gbl_panelDefaults = new GridBagLayout();
        gbl_panelDefaults.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_panelDefaults.rowHeights = new int[]{0, 0, 0};
        gbl_panelDefaults.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panelDefaults.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        panelDefaults.setLayout(gbl_panelDefaults);

        textField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 0, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 2;
        gbc_textField.gridy = 1;
        panelDefaults.add(textField, gbc_textField);
        textField.setColumns(10);

        Box horizontalBox = Box.createHorizontalBox();
        GridBagConstraints gbc_horizontalBox = new GridBagConstraints();
        gbc_horizontalBox.insets = new Insets(0, 0, 5, 5);
        gbc_horizontalBox.anchor = GridBagConstraints.EAST;
        gbc_horizontalBox.gridx = 0;
        gbc_horizontalBox.gridy = 1;
        getContentPane().add(horizontalBox, gbc_horizontalBox);

        btnSave = new JButton("Save");
        btnSave.addActionListener(this);
        horizontalBox.add(btnSave);

        btnCancel = new JButton("Cancel");
        horizontalBox.add(btnCancel);
        btnCancel.addActionListener(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    public void display() {
        updateMySQLCheckBox();
        updateGoogleMaps();
        firstTime = false;
        pack();
        setVisible(true);
    }

    private void updateMySQLCheckBox() {
	/*	boolean flag = false;
		if (firstTime)
			if (getProperty(PREF_USE_MYSQL).equals(XEYTAN_TRUE)) {
				chckbxUseMysql.setSelected(true);
				flag = true;
			}

		if (chckbxUseMysql.isSelected() || flag == true) {

			txtMySQLHost.setEnabled(true);
			txtMySQLHost.setEditable(true);
			
			txtMySQLPort.setEnabled(true);
			txtMySQLPort.setEditable(true);
			
			txtMySQLUser.setEnabled(true);
			txtMySQLUser.setEditable(true);
			
			txtMySQLPassword.setEnabled(true);
			txtMySQLPassword.setEditable(true);
						
		} else {

			txtMySQLHost.setEnabled(false);
			txtMySQLHost.setEditable(false);

			txtMySQLPort.setEnabled(false);
			txtMySQLPort.setEditable(false);

			txtMySQLUser.setEnabled(false);
			txtMySQLUser.setEditable(false);

			txtMySQLPassword.setEnabled(false);
			txtMySQLPassword.setEditable(false);
		}
		txtMySQLHost.setText(getProperty(MYSQL_HOST, "localhost"));
		txtMySQLPort.setText(getProperty(MYSQL_PORT, "3306"));
		txtMySQLUser.setText(getProperty(MYSQL_USER, ""));
		txtMySQLPassword.setText(getProperty(MYSQL_PASSWORD, ""));*/
    }

    private void updateGoogleMaps() {/*
		boolean flag = false;
		if (firstTime)
			if (getProperty(PREF_USE_GOOGLE_MAPS).equals(XEYTAN_TRUE)) {
				chckbxUseGoogleMaps.setSelected(true);
				flag = true;
			}

		if (chckbxUseGoogleMaps.isSelected() || flag == true) {
			txtGoogleKey.setEnabled(true);
			txtGoogleKey.setEditable(true);
		} else {
			txtGoogleKey.setEnabled(false);
			txtGoogleKey.setEditable(false);
		}
		txtGoogleKey.setText(getProperty(GOOGLE_KEY, ""));*/
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnCancel)
            dispose();
        else if (source == chckbxUseMysql) {
            updateMySQLCheckBox();
        } else if (source == chckbxUseGoogleMaps)
            updateGoogleMaps();
        else if (source == btnSave) {
            savePreferences();
        }
    }

    private void savePreferences() {
	/*	if(chckbxUseGoogleMaps.isSelected()){
			setProperty(PREF_USE_GOOGLE_MAPS, XEYTAN_TRUE);
			setProperty(GOOGLE_KEY, txtGoogleKey.getText());
		}
		if(chckbxUseMysql.isSelected()){
			setProperty(PREF_USE_MYSQL, XEYTAN_TRUE);
			setProperty(MYSQL_HOST, txtMySQLHost.getText());
			setProperty(MYSQL_USER, txtMySQLUser.getText());
			setProperty(MYSQL_PASSWORD, txtMySQLPassword.getText());
			if(FormValidator.validateInteger(txtMySQLPort.getText()))
				setProperty(MYSQL_PORT, txtMySQLPort.getText());
		}
		*/
        dispose();
    }

}

package com.melardev.xeytanj.gui;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractDialogHelp extends JDialog{

	protected JLabel lblHeader;
	protected JLabel lblDescr;
	private int dialogHeight;
	private int dialogWidth;

	public AbstractDialogHelp(String title, String header, String description) {
		this();
		setTitle(title);
		setHeader(header);
		setDescription(description);

	}

	public AbstractDialogHelp() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 75, 66, 104, 220, 54, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		Component verticalGlue_1 = Box.createVerticalGlue();
		GridBagConstraints gbc_verticalGlue_1 = new GridBagConstraints();
		gbc_verticalGlue_1.gridheight = 2;
		gbc_verticalGlue_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_verticalGlue_1.insets = new Insets(0, 0, 5, 5);
		gbc_verticalGlue_1.gridx = 3;
		gbc_verticalGlue_1.gridy = 0;
		getContentPane().add(verticalGlue_1, gbc_verticalGlue_1);

		JLabel lblIconbrand = new JLabel("IconBrand");
		GridBagConstraints gbc_lblIconbrand = new GridBagConstraints();
		gbc_lblIconbrand.insets = new Insets(0, 0, 5, 5);
		gbc_lblIconbrand.gridx = 1;
		gbc_lblIconbrand.gridy = 2;
		getContentPane().add(lblIconbrand, gbc_lblIconbrand);

		lblHeader = new JLabel("<html><h1>XeytaJ</h1></html>");
		GridBagConstraints gbc_lblHeader = new GridBagConstraints();
		gbc_lblHeader.insets = new Insets(0, 0, 5, 5);
		gbc_lblHeader.gridx = 3;
		gbc_lblHeader.gridy = 2;
		getContentPane().add(lblHeader, gbc_lblHeader);

		lblDescr = new JLabel("<html>No description is available for now</html>");
		GridBagConstraints gbc_lblAboutDescr = new GridBagConstraints();
		gbc_lblAboutDescr.gridheight = 2;
		gbc_lblAboutDescr.insets = new Insets(0, 0, 5, 5);
		gbc_lblAboutDescr.gridx = 3;
		gbc_lblAboutDescr.gridy = 3;
		getContentPane().add(lblDescr, gbc_lblAboutDescr);

		JLabel lblAuthor = new JLabel("MelarDev");
		GridBagConstraints gbc_lblAuthor = new GridBagConstraints();
		gbc_lblAuthor.anchor = GridBagConstraints.SOUTH;
		gbc_lblAuthor.insets = new Insets(0, 0, 5, 5);
		gbc_lblAuthor.gridwidth = 2;
		gbc_lblAuthor.gridx = 2;
		gbc_lblAuthor.gridy = 7;
		getContentPane().add(lblAuthor, gbc_lblAuthor);

	}

	private void setHeader(String header) {
		lblHeader.setText("<html>" + header + "</html>");
	}

	public void setDescription(String descr){
		lblDescr.setText("<html><p>" + descr + "</p>");
	}

	public abstract void setDialogSize();

	public void display() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setDialogSize();
		setResizable(false);
		setVisible(true);
	}

}

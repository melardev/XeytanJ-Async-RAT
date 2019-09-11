package com.melardev.xeytanj.gui.builder;

import com.melardev.xeytanj.gui.IGui;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.models.BuildClientInfoStructure;
import com.melardev.xeytanj.services.IAppMessages;
import com.melardev.xeytanj.services.config.ConfigService;
import com.melardev.xeytanj.services.ioc.DependencyResolverFactory;
import com.melardev.xeytanj.services.ioc.IAppDependencyResolver;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class BuilderGUI extends JDialog implements ActionListener, ChangeListener, IGui<BuilderDialogListener> {

    private IAppDependencyResolver loc;
    private JTextField txtHost;
    private JTextField txtPort;
    private JTextField txtKey;
    private JTextField txtConnectionRetry;
    private ButtonGroup group;
    private JRadioButton rdbtnGetGeoRemote;
    private JRadioButton rdbtnGetGeoLocally;
    private JCheckBox chckbxPersistence;
    private JSlider sliderConnectionRetry;
    private JButton btnBuild;
    private JLabel lblMs;
    private Component horizontalGlue;
    private JCheckBox chckbxSavePreferences;
    private ConfigService config;
    BuilderDialogListener listener;
    private long id;
    private IUiMediator mediator;
    private IAppMessages messageProvider;
    private String defaultOutputPath;

    public BuilderGUI() {

    }

    public void display() {
        loc = DependencyResolverFactory.getDependencyResolver();
        config = loc.lookup(ConfigService.class);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 73, 55, 0, 0, 90, 0, 35, 0, 0};
        gridBagLayout.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        JLabel lblHost = new JLabel("Host");
        GridBagConstraints gbc_lblHost = new GridBagConstraints();
        gbc_lblHost.anchor = GridBagConstraints.EAST;
        gbc_lblHost.insets = new Insets(0, 0, 5, 5);
        gbc_lblHost.gridx = 2;
        gbc_lblHost.gridy = 3;
        getContentPane().add(lblHost, gbc_lblHost);

        txtHost = new JTextField();
        GridBagConstraints gbc_txtHost = new GridBagConstraints();
        gbc_txtHost.gridwidth = 3;
        gbc_txtHost.insets = new Insets(0, 0, 5, 5);
        gbc_txtHost.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtHost.gridx = 3;
        gbc_txtHost.gridy = 3;
        getContentPane().add(txtHost, gbc_txtHost);
        txtHost.setColumns(10);
        txtHost.setText(config.getDefaultHostname());

        JLabel lblPort = new JLabel(messageProvider.getText("net.port"));
        GridBagConstraints gbc_lblPort = new GridBagConstraints();
        gbc_lblPort.anchor = GridBagConstraints.EAST;
        gbc_lblPort.insets = new Insets(0, 0, 5, 5);
        gbc_lblPort.gridx = 2;
        gbc_lblPort.gridy = 4;
        getContentPane().add(lblPort, gbc_lblPort);

        txtPort = new JTextField();
        GridBagConstraints gbc_txtPort = new GridBagConstraints();
        gbc_txtPort.gridwidth = 3;
        gbc_txtPort.insets = new Insets(0, 0, 5, 5);
        gbc_txtPort.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtPort.gridx = 3;
        gbc_txtPort.gridy = 4;
        getContentPane().add(txtPort, gbc_txtPort);
        txtPort.setColumns(10);
        txtPort.setText(String.valueOf(config.getDefaultPort()));

        btnBuild = new JButton(messageProvider.getText("generic.build"));
        btnBuild.addActionListener(this);

        JLabel lblKey = new JLabel(messageProvider.getText("generic.key"));
        GridBagConstraints gbc_lblKey = new GridBagConstraints();
        gbc_lblKey.anchor = GridBagConstraints.EAST;
        gbc_lblKey.insets = new Insets(0, 0, 5, 5);
        gbc_lblKey.gridx = 2;
        gbc_lblKey.gridy = 5;
        getContentPane().add(lblKey, gbc_lblKey);

        txtKey = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.gridwidth = 3;
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 3;
        gbc_textField.gridy = 5;
        getContentPane().add(txtKey, gbc_textField);
        txtKey.setColumns(10);
        txtKey.setText(config.getLoginKey());

        chckbxPersistence = new JCheckBox(messageProvider.getText("generic.persistence"));
        chckbxPersistence.setSelected(true);
        GridBagConstraints gbc_chckbxPersistence = new GridBagConstraints();
        gbc_chckbxPersistence.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxPersistence.gridx = 5;
        gbc_chckbxPersistence.gridy = 7;
        getContentPane().add(chckbxPersistence, gbc_chckbxPersistence);
        chckbxPersistence.addActionListener(this);

        JLabel lblGeolocation = new JLabel(messageProvider.getText("generic.milliseconds"));
        GridBagConstraints gbc_lblGeolocation = new GridBagConstraints();
        gbc_lblGeolocation.insets = new Insets(0, 0, 5, 5);
        gbc_lblGeolocation.gridx = 2;
        gbc_lblGeolocation.gridy = 8;
        getContentPane().add(lblGeolocation, gbc_lblGeolocation);

        sliderConnectionRetry = new JSlider();
        sliderConnectionRetry.setMaximum(1000);
        GridBagConstraints gbc_slider = new GridBagConstraints();
        gbc_slider.insets = new Insets(0, 0, 5, 5);
        gbc_slider.gridx = 5;
        gbc_slider.gridy = 8;
        getContentPane().add(sliderConnectionRetry, gbc_slider);
        sliderConnectionRetry.addChangeListener(this);

        txtConnectionRetry = new JTextField();
        GridBagConstraints gbc_textField_1 = new GridBagConstraints();
        gbc_textField_1.insets = new Insets(0, 0, 5, 5);
        gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_1.gridx = 6;
        gbc_textField_1.gridy = 8;
        getContentPane().add(txtConnectionRetry, gbc_textField_1);
        txtConnectionRetry.setColumns(10);
        txtConnectionRetry.setText(String.valueOf(sliderConnectionRetry.getValue()));
        group = new ButtonGroup();

        lblMs = new JLabel(messageProvider.getText("generic.milliseconds"));
        GridBagConstraints gbc_lblMs = new GridBagConstraints();
        gbc_lblMs.anchor = GridBagConstraints.WEST;
        gbc_lblMs.insets = new Insets(0, 0, 5, 5);
        gbc_lblMs.gridx = 7;
        gbc_lblMs.gridy = 8;
        getContentPane().add(lblMs, gbc_lblMs);

        horizontalGlue = Box.createHorizontalGlue();
        GridBagConstraints gbc_horizontalGlue = new GridBagConstraints();
        gbc_horizontalGlue.insets = new Insets(0, 0, 5, 0);
        gbc_horizontalGlue.gridx = 8;
        gbc_horizontalGlue.gridy = 8;
        getContentPane().add(horizontalGlue, gbc_horizontalGlue);

        rdbtnGetGeoRemote = new JRadioButton(messageProvider.getText("gui.builder.geo_from_client"));
        rdbtnGetGeoRemote.setSelected(true);
        GridBagConstraints gbc_rdbtnGetGeoFrom = new GridBagConstraints();
        gbc_rdbtnGetGeoFrom.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnGetGeoFrom.gridx = 2;
        gbc_rdbtnGetGeoFrom.gridy = 9;
        getContentPane().add(rdbtnGetGeoRemote, gbc_rdbtnGetGeoFrom);

        group.add(rdbtnGetGeoRemote);

        rdbtnGetGeoLocally = new JRadioButton("Get Geo Locally");
        GridBagConstraints gbc_rdbtnGetGeoLocally = new GridBagConstraints();
        gbc_rdbtnGetGeoLocally.anchor = GridBagConstraints.WEST;
        gbc_rdbtnGetGeoLocally.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnGetGeoLocally.gridx = 2;
        gbc_rdbtnGetGeoLocally.gridy = 10;
        getContentPane().add(rdbtnGetGeoLocally, gbc_rdbtnGetGeoLocally);
        group.add(rdbtnGetGeoLocally);

        chckbxSavePreferences = new JCheckBox("Save Preferences");
        chckbxSavePreferences.setSelected(true);
        GridBagConstraints gbc_chckbxSavePreferences = new GridBagConstraints();
        gbc_chckbxSavePreferences.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxSavePreferences.gridx = 2;
        gbc_chckbxSavePreferences.gridy = 11;
        getContentPane().add(chckbxSavePreferences, gbc_chckbxSavePreferences);
        GridBagConstraints gbc_btnBuild = new GridBagConstraints();
        gbc_btnBuild.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnBuild.insets = new Insets(0, 0, 5, 5);
        gbc_btnBuild.anchor = GridBagConstraints.NORTHWEST;
        gbc_btnBuild.gridx = 5;
        gbc_btnBuild.gridy = 11;
        getContentPane().add(btnBuild, gbc_btnBuild);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    @Override
    public void addListener(BuilderDialogListener builderDialogListener) {
        listener = builderDialogListener;
    }

    @Override
    public BuilderDialogListener getListener() {
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

    @Override
    public void stateChanged(ChangeEvent evt) {
        txtConnectionRetry.setText(String.valueOf(((JSlider) evt.getSource()).getValue()));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == btnBuild) {

            JFileChooser fChooser = new JFileChooser();
            if (defaultOutputPath == null || !(new File(defaultOutputPath).exists()))
                fChooser.setCurrentDirectory(new File("./"));
            else
                fChooser.setCurrentDirectory(new File(defaultOutputPath));

            if (fChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                defaultOutputPath = fChooser.getSelectedFile().getParent();
                config.setDefaultClientPath(defaultOutputPath);
                //defaultName = fChooser.getSelectedFile().getName();

                //File outputFile = new File("./output.jar");
                File outputFile = fChooser.getSelectedFile();
                listener.onBuildRequested(new BuildClientInfoStructure(
                        txtHost.getText(), txtPort.getText(),
                        sliderConnectionRetry.getValue(), txtKey.getText(),
                        chckbxPersistence.isEnabled(), outputFile));
            }

        } else if (evt.getSource() == chckbxPersistence) {
            if (sliderConnectionRetry.isEnabled()) {
                sliderConnectionRetry.setEnabled(false);
                txtConnectionRetry.setEnabled(false);
            } else {
                sliderConnectionRetry.setEnabled(true);
                txtConnectionRetry.setEnabled(true);
            }
        }
    }


    public void setDefaultOutputPath(String defaultOutputPath) {
        this.defaultOutputPath = defaultOutputPath;
    }
}

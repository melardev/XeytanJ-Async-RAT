package com.melardev.xeytanj.gui.process;


import com.melardev.xeytanj.enums.ProcessInfoDetails;
import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.gui.IGuiUserOwned;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.models.ProcessStructure;
import com.melardev.xeytanj.services.IAppMessages;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessListGui extends JFrame implements IGuiUserOwned<ProcessListUiListener>, ActionListener, PopupMenuListener {
    private JTable tableProc;
    private JTextField txtProc;
    public static ArrayList<ImageIcon> icons;
    private JPopupMenu popupMenu;
    private JMenuItem mntmKillProcess;
    private JMenuItem mntmProperties;

    private JButton btnRefresh;
    private JComboBox<ProcessInfoDetails> comboProc;
    private Component horizontalStrut;
    private JScrollPane scrollPane;
    private Client client;
    private ProcessListUiListener listener;
    private IUiMediator mediator;
    private IAppMessages messageProvider;

    public ProcessListGui() {
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
        addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                notifyMediatorOnClose();
            }
        });
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{65, 64, 237, 165, 69, 114, 28, 0};
        gridBagLayout.rowHeights = new int[]{28, 0, 0, 0, 373, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        comboProc = new JComboBox<>();
        comboProc.setModel(new DefaultComboBoxModel<ProcessInfoDetails>(ProcessInfoDetails.values()));
        GridBagConstraints gbc_comboProc = new GridBagConstraints();
        gbc_comboProc.insets = new Insets(0, 0, 5, 5);
        gbc_comboProc.fill = GridBagConstraints.BOTH;
        gbc_comboProc.gridx = 2;
        gbc_comboProc.gridy = 1;
        getContentPane().add(comboProc, gbc_comboProc);

        btnRefresh = new JButton("Refresh");
        GridBagConstraints gbc_btnRefresh = new GridBagConstraints();
        gbc_btnRefresh.insets = new Insets(0, 0, 5, 5);
        gbc_btnRefresh.gridx = 3;
        gbc_btnRefresh.gridy = 1;
        getContentPane().add(btnRefresh, gbc_btnRefresh);

        btnRefresh.addActionListener(this);

        JSeparator separator = new JSeparator();
        GridBagConstraints gbc_separator = new GridBagConstraints();
        gbc_separator.insets = new Insets(0, 0, 5, 5);
        gbc_separator.gridx = 0;
        gbc_separator.gridy = 2;
        getContentPane().add(separator, gbc_separator);

        txtProc = new JTextField();
        GridBagConstraints gbc_txtProc = new GridBagConstraints();
        gbc_txtProc.insets = new Insets(0, 0, 5, 5);
        gbc_txtProc.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtProc.gridx = 2;
        gbc_txtProc.gridy = 3;
        getContentPane().add(txtProc, gbc_txtProc);
        txtProc.setColumns(10);

        horizontalStrut = Box.createHorizontalStrut(20);
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.insets = new Insets(0, 0, 5, 0);
        gbc_horizontalStrut.gridheight = 5;
        gbc_horizontalStrut.gridx = 6;
        gbc_horizontalStrut.gridy = 0;
        getContentPane().add(horizontalStrut, gbc_horizontalStrut);

        tableProc = new JTable();
        GridBagConstraints gbc_tableProc_1 = new GridBagConstraints();
        gbc_tableProc_1.anchor = GridBagConstraints.SOUTHEAST;
        gbc_tableProc_1.gridwidth = 5;
        gbc_tableProc_1.gridheight = 2;
        gbc_tableProc_1.insets = new Insets(0, 0, 5, 5);
        gbc_tableProc_1.gridx = 1;
        gbc_tableProc_1.gridy = 4;


        DefaultTableModel modelProcessTable = new DefaultTableModel();
        modelProcessTable.setColumnCount(0);
        modelProcessTable.addColumn("Process Name");
        modelProcessTable.addColumn("Executable Path");
        modelProcessTable.addColumn("Pid");

        tableProc.setModel(modelProcessTable);

        tableProc.getColumn(tableProc.getModel().getColumnName(0))
                .setCellRenderer(new ProcessTableRenderer("Process Name"));
        tableProc.getColumn(tableProc.getModel().getColumnName(1))
                .setCellRenderer(new DefaultTableCellRenderer());
        tableProc.getColumn(tableProc.getModel().getColumnName(2))
                .setCellRenderer(new DefaultTableCellRenderer());


        tableProc.setShowVerticalLines(false);
        tableProc.setFillsViewportHeight(true);
        //scrollPane.setSize(200, 600);
        tableProc.setAutoCreateRowSorter(true);

        //tableProc.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(this);
        addPopup(tableProc, popupMenu);

        mntmKillProcess = new JMenuItem("Kill Process");
        popupMenu.add(mntmKillProcess);
        mntmKillProcess.addActionListener(this);

        mntmProperties = new JMenuItem("Properties");
        popupMenu.add(mntmProperties);

        scrollPane = new JScrollPane(tableProc);
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridwidth = 5;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.gridx = 1;
        gbc_scrollPane.gridy = 4;
        getContentPane().add(scrollPane, gbc_scrollPane);
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

    }

    public void populateTable(List<ProcessStructure> processStructures) {
        // Reset state
        ((DefaultTableModel) tableProc.getModel()).setColumnCount(0);
        ((DefaultTableModel) tableProc.getModel()).setRowCount(0);

        ((DefaultTableModel) tableProc.getModel()).addColumn("Process Name");
        ((DefaultTableModel) tableProc.getModel()).addColumn("Executable Path");
        ((DefaultTableModel) tableProc.getModel()).addColumn("Pid");
        tableProc.getColumn("Process Name").setCellRenderer(new ProcessTableRenderer("Lolilol"));
        processStructures.forEach(p -> {
            ProcessLabel[] labels = new ProcessLabel[3];

            labels[0] = new ProcessLabel(p.getIcon(), p.getProcessName());
            labels[1] = new ProcessLabel(p.getProcessPath());
            labels[2] = new ProcessLabel(String.valueOf(p.getPid()));
            ((DefaultTableModel) tableProc.getModel()).addRow(labels);
        });

        pack();
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
        return Subject.PROCESS;
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
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //setSize(600, 600);
    }

    @Override
    public void addListener(ProcessListUiListener processListUiListener) {
        this.listener = processListUiListener;
    }

    @Override
    public ProcessListUiListener getListener() {
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


    public ImageIcon getIconAt(int index) {
        return icons.get(index);
    }

    private static void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            private void showMenu(MouseEvent e) {
                if (!(e.getComponent() instanceof JTable))
                    return;
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
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        //XeytanProcessClientThread handler = (XeytanProcessClientThread) serverHandler;

        if (source == btnRefresh) {
            onProcessInfoDetailsUpdateClicked((ProcessInfoDetails) comboProc.getSelectedItem());

            System.out.println(comboProc.getModel().getSelectedItem().toString());
            //System.out.println(comboProc.getSelectedItem().toString());

        } else if (source == mntmKillProcess) {
            String objPid = tableProc.getModel().getValueAt(tableProc.getSelectedRow(), tableProc.getColumn("ProcessId").getModelIndex()).toString();
            int pid = Integer.parseInt(objPid);
            System.out.println("PID to be killed " + pid);
            onProcessKillRequest(getClient(), pid);

        }

    }

    private void onProcessInfoDetailsUpdateClicked(ProcessInfoDetails selectedItem) {

    }

    private void onProcessKillRequest(Client client, int pid) {
        getMediator().onProcessKillRequest(client, pid);
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

    }

    @Override
    public void setMessageProvider(IAppMessages messageProvider) {
        this.messageProvider = messageProvider;
    }

}

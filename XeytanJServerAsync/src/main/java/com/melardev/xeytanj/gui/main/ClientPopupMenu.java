package com.melardev.xeytanj.gui.main;

import com.melardev.xeytanj.enums.NetworkProtocol;
import com.melardev.xeytanj.enums.ProcessInfoDetails;
import com.melardev.xeytanj.errors.UnexpextedStateException;
import com.melardev.xeytanj.models.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientPopupMenu implements ActionListener {


    private final MainGui mainGui;

    public ClientPopupMenu(MainGui mainGui) {
        this.mainGui = mainGui;
    }

    @Override
    public void actionPerformed(ActionEvent paramActionEvent) {
        //TODO:change new thread by Runnable r = () -> { };
        Object source = paramActionEvent.getSource();
        JTable tableClients = mainGui.getTableClients();
        int row = tableClients.getSelectedRow();

        Client client = null;
        if (row != -1)
            client = (Client) (tableClients.getModel().getValueAt(row, tableClients.getModel().getColumnCount() - 1/*The last column contains the Client object*/));
        if (client == null)
            throw new UnexpextedStateException();

        if (source == mainGui.menuInfo) {
            mainGui.getClientInfo(client);
        } else if (source == mainGui.menuRemoteShell) {
            mainGui.onRemoteShellClicked(client);
        } else if (source == mainGui.menuListProcess)
            mainGui.onProcessListClicked(ProcessInfoDetails.BASIC, client);
        else if (source == mainGui.menuCamera)
            mainGui.onMenuCameraClicked(NetworkProtocol.TCP, client);
        else if (source == mainGui.menuDesktop)
            mainGui.onRemoteDesktopClicked(client);
        else if (source == mainGui.menuFileManager)
            mainGui.onRemoteFileManagerClicked(client);
        else if (source == mainGui.menuChat) {
            mainGui.onStartChatClicked(client);
        } else if (source == mainGui.mntmFile)
            mainGui.onVoIpClicked(client);
        else if (source == mainGui.mntmRaw)
            mainGui.onVoIpRawClicked(client);
        else if (source == mainGui.menuKeylogs)
            mainGui.onSessionKeyloggerClicked(client);
        else if (source == mainGui.menuLockSystem)
            mainGui.onLockSystemClicked(client);
        else if (source == mainGui.menuLogOff)
            mainGui.onLogOffSystem(client);
        else if (source == mainGui.menuShutdown)
            mainGui.onShutdownClicked(client);
        else if (source == mainGui.menuReboot)
            mainGui.onRebootClicked(client);
        else if (source == mainGui.menuTurnOnDisplay)
            mainGui.onTurnOnOffDisplayClicked(client, true);
        else if (source == mainGui.menuTurnOffDisplay)
            mainGui.onTurnOnOffDisplayClicked(client, false);
    }
}

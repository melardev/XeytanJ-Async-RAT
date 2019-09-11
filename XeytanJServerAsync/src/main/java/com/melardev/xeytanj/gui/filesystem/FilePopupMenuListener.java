package com.melardev.xeytanj.gui.filesystem;

import com.melardev.xeytanj.models.FileInfoStructure;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;


public class FilePopupMenuListener implements PopupMenuListener {

    private final FileSystemGui fileSystemGui;

    public FilePopupMenuListener(FileSystemGui fileSystemGui) {
        this.fileSystemGui = fileSystemGui;
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        JTable table = fileSystemGui.getExplorerTable();
        FileInfoStructure f = (FileInfoStructure) table.getValueAt(table.getSelectedRow(), 0);
        JPopupMenu menu = (JPopupMenu) e.getSource();
        if (f.isFile) {
            menu.add(fileSystemGui.getMntmOpenFile(), 0);
            menu.remove(fileSystemGui.getMntmUpload());
        } else {
            menu.add(fileSystemGui.getMntmUpload(), 0);
            menu.remove(fileSystemGui.getMntmOpenFile());
        }
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent paramPopupMenuEvent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent paramPopupMenuEvent) {
        // TODO Auto-generated method stub

    }
}

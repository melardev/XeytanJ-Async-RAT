package com.melardev.xeytanj.gui.filesystem;

import javax.swing.table.DefaultTableModel;

public class NonEditableTableModel extends DefaultTableModel {

    public NonEditableTableModel(Object[][] data, String[] columnNames) {
        super(data, columnNames);
    }

    public NonEditableTableModel() {
        super();
    }

    @Override
    public boolean isCellEditable(int paramInt1, int paramInt2) {
        return false;
    }

}

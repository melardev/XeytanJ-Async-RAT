package com.melardev.xeytanj.gui.main;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TableClientsMouseHandler extends MouseAdapter {
	private JTable jtable;

	public TableClientsMouseHandler(JTable _table) {
		super();
		jtable = _table;
	}

	@Override
	public void mouseClicked(MouseEvent paramMouseEvent) {
		int row = jtable.rowAtPoint(paramMouseEvent.getPoint());
		if(row == -1)
			return;
		System.out.println(jtable.getValueAt(row, 2));
		
	}
}

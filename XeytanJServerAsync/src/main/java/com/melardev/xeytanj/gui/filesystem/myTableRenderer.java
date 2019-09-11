package com.melardev.xeytanj.gui.filesystem;

import com.melardev.xeytanj.models.FileInfoStructure;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class myTableRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1,
			boolean paramBoolean2, int paramInt1, int paramInt2) {
		
		JLabel label = (JLabel)super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1,
				paramInt2);
		if(paramObject instanceof FileInfoStructure){
			FileInfoStructure original = (FileInfoStructure) paramObject;
			label.setIcon(original.getIcon());
			label.setText(original.getBaseName());
			return label;
		}
		
//		JLabel label = (JLabel)super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
		
		label.setText(paramObject.toString());
		label.setIcon(null);
		
		return label;
	}

}

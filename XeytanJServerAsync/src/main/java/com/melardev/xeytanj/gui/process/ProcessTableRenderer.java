package com.melardev.xeytanj.gui.process;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ProcessTableRenderer extends DefaultTableCellRenderer {

    public ProcessTableRenderer(String processName) {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean isSelected,
                                                   boolean paramBoolean2, int row, int col) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(paramJTable, paramObject, isSelected, paramBoolean2,
                row, col);
        if (col == 0) {
            Object parent;
            if (row > 52)
                parent = paramJTable.getParent();
            /*
             * while (!(parent instanceof ProcessListGui))
             * continue;
             */
            //ImageIcon icon = ((ProcessListGui) parent).getIconAt(row);
            ProcessLabel la = (ProcessLabel) paramObject;
            if (paramObject == null)
                return label;
            //ImageIcon icon = ProcessListGui.icons.getProperty(row);
            ImageIcon icon = (ImageIcon) la.getIcon();
            int c;
            if (icon == null)
                c = 2;
            label.setIcon(la.getIcon());

            return label;

        }

        //		JLabel label = (JLabel)super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);

        label.setText(paramObject == null ? "" : paramObject.toString());
        label.setIcon(null);

        return label;
    }
}

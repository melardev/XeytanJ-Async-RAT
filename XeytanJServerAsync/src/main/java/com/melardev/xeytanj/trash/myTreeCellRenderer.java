package com.melardev.xeytanj.trash;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class myTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree paramJTree, Object node, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		DefaultMutableTreeNode n = (DefaultMutableTreeNode)node;
		FileTreeNode fnode = (FileTreeNode)n.getUserObject();
		leaf = fnode.isLeaf();
		int debug = 2;
		if(leaf)
			debug = 0;
		
		DefaultTreeCellRenderer label= (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(paramJTree, node, selected, expanded, false, row, hasFocus);
		
		//label.setIcon(new ImageIcon(getClass().getResource("/icons/flags/Spain.png")));
		label.setIcon( (fnode.getIcon()));
		return label;
		/*DefaultMutableTreeNode n = (DefaultMutableTreeNode)node;
		
		JLabel label = new JLabel(n.getUserObject().toString());
		
		return label;*/
	}

}

package com.melardev.xeytanj.gui.filesystem;


import com.melardev.xeytanj.trash.FileTreeNode;
import com.melardev.xeytanj.trash.myTreeCellRenderer;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class test extends JFrame implements TreeSelectionListener, TreeWillExpandListener {
	private JTree tree;

	private JEditorPane htmlPane;
	private JScrollPane treeView;

	protected boolean doubleClickFired;

	public test() {
		setSize(500, 500);
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new FileTreeNode("PC"));

		tree = new JTree(top);
		File[] roots = new File(".").listRoots();
		for (File r : roots) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(new FileTreeNode(r.getAbsolutePath()));
			top.add(node);
			for (String s : r.list()) {
				node.add(new DefaultMutableTreeNode(new FileTreeNode(s)));
			}
		}

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		tree.addTreeWillExpandListener(this);
		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getClickCount() == 2) {

					TreePath count = tree.getSelectionModel().getSelectionPath();
					TreePath parentPath = null;
					StringBuilder sb = new StringBuilder();
					DefaultMutableTreeNode tempModel = (DefaultMutableTreeNode)count.getLastPathComponent();
					FileTreeNode tempnode = (FileTreeNode)tempModel.getUserObject();
					if(!tempModel.isLeaf())
						return;
					if(!tempnode.isFolder())
						return;
					sb.append(tempnode.toString());
					while((parentPath = count.getParentPath()) != null){
						tempnode = (FileTreeNode)((DefaultMutableTreeNode)parentPath.getLastPathComponent()).getUserObject();
						if(tempnode.toString().equals("PC"))
							break;
						sb.insert(0, tempnode.toString());
						count = parentPath;
					}
					System.out.println(sb.toString());
					/*
					FileTreeNode myfnode = (FileTreeNode) ((DefaultMutableTreeNode) count.getLastPathComponent())
							.getUserObject();
					System.out.println("inside clicked : " + myfnode);

					doubleClickFired = true;
				*/}
				

			}

		});
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setRootVisible(true);
		treeView = new JScrollPane(tree);
		tree.setCellRenderer(new myTreeCellRenderer());
		getContentPane().add(treeView, BorderLayout.NORTH);

		htmlPane = new JEditorPane();
		htmlPane.setEditable(false);
		JScrollPane htmlView = new JScrollPane(htmlPane);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		//splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(htmlView);

		tree.setShowsRootHandles(true);
		htmlView.setMinimumSize(new Dimension(100, 50));
		treeView.setMinimumSize(new Dimension(100, 50));

		try {
			htmlPane.setPage(getClass().getResource("TreeDemoHelp.html"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(500, 300));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		add(splitPane);

	}

	public static void main(String[] args) {
		new test().setVisible(true);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {

		/*
		 * if(doubleClickFired){
		 * doubleClickFired = false;
		 * }
		 * DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		 * tree.getLastSelectedPathComponent();
		 * System.out.println(node.getUserObject());
		 * for (int i = 1; i < e.getPath().getPathCount(); i++) {
		 * System.out.print(e.getPath().getPath()[i]);
		 * }
		 */

	}

	@Override
	public void treeWillExpand(TreeExpansionEvent e) {

		/*
		 * System.out.println("will be expandend : ");
		 * for (int i = 1; i < e.getPath().getPathCount(); i++) {
		 * System.out.print(e.getPath().getPath()[i]);
		 * }
		 */

	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent paramTreeExpansionEvent) {

	}
}

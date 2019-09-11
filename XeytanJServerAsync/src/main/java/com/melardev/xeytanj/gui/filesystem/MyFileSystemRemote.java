package com.melardev.xeytanj.gui.filesystem;


import com.melardev.xeytanj.models.FileInfoStructure;
import com.melardev.xeytanj.net.packets.filesystem.PacketFileExplorer;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class MyFileSystemRemote extends JFrame {
	private class MyDispatcher implements KeyEventDispatcher {

		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				System.out.println(e.getKeyCode());
			} else if (e.getID() == KeyEvent.KEY_RELEASED) {
				System.out.println("2test2");
			} else if (e.getID() == KeyEvent.KEY_TYPED) {
				System.out.println("3test3");
			}
			return false;
		}

	}

	private class MyStack extends Stack<String> {

		@Override
		public synchronized String toString() {
			Iterator<String> it = this.iterator();
			String result = "";
			String temp;
			while (it.hasNext()) {
				temp = it.next();
				if (temp.endsWith("/"))
					temp += "/";
				result += temp;
			}

			return result;
		}

	}

	private JTable explorerTable;
	private JTextField txtPath;
	private static HashMap<String, Icon> icons = new HashMap<String, Icon>();
	private static FileSystemView fsv = FileSystemView.getFileSystemView();
	private static File extF = new File("src/res/extensions");
	private JList<FileInfoStructure> listDrive;
	private DefaultListModel<FileInfoStructure> listModel;
	private JMenuItem mntmUpload;
	private JMenuItem mntmOpenFile;
	private MyStack pathTrack;

	public MyFileSystemRemote() {
		pathTrack = new MyStack();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{143, 302, 363, 302, 1};
		gridBagLayout.rowHeights = new int[]{38, 159, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		JButton btnGo = new JButton("Go");
		GridBagConstraints gbc_btnGo = new GridBagConstraints();
		gbc_btnGo.insets = new Insets(0, 0, 5, 5);
		gbc_btnGo.gridx = 0;
		gbc_btnGo.gridy = 0;
		getContentPane().add(btnGo, gbc_btnGo);

		txtPath = new JTextField();
		GridBagConstraints gbc_txtPath = new GridBagConstraints();
		gbc_txtPath.gridwidth = 2;
		gbc_txtPath.insets = new Insets(0, 0, 5, 5);
		gbc_txtPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPath.gridx = 1;
		gbc_txtPath.gridy = 0;
		getContentPane().add(txtPath, gbc_txtPath);
		txtPath.setColumns(10);

		listDrive = new JList<FileInfoStructure>();
		JScrollPane listPane = new JScrollPane(listDrive);
		listModel = new DefaultListModel<FileInfoStructure>();

		listDrive.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listDrive.setLayoutOrientation(JList.VERTICAL);
		listDrive.setCellRenderer(new DriveListRenderer());
		listDrive.setModel(listModel);
		listDrive.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() == 2) {
					Component comp = e.getComponent();
					String toLs = listDrive.getSelectedValue().fullPath;
					pathTrack.clear();
					pathTrack.push(toLs);
					populateTable(toLs);
				}
			}

		});
		GridBagConstraints gbc_listDrive = new GridBagConstraints();
		gbc_listDrive.insets = new Insets(0, 0, 0, 5);
		gbc_listDrive.fill = GridBagConstraints.VERTICAL;
		gbc_listDrive.gridx = 0;
		gbc_listDrive.gridy = 1;
		getContentPane().add(listPane, gbc_listDrive);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
		getContentPane().add(scrollPane, gbc_scrollPane);

		explorerTable = new JTable();
		explorerTable.setShowGrid(false);
		explorerTable.setShowVerticalLines(false);

		String[] columnNames = {"Name", "fileSize", "Executable", "Readable", "Writeable"};
		Object[][] data = new Object[0][];
		explorerTable.setModel(new NonEditableTableModel(data, columnNames));
		explorerTable.setDefaultRenderer(Object.class, new myTableRenderer());
		explorerTable.setShowHorizontalLines(false);
		explorerTable.setAutoCreateRowSorter(true);

		explorerTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					FileInfoStructure toLsPath = (FileInfoStructure) explorerTable.getValueAt(explorerTable.getSelectedRow(), 0);
					if (!toLsPath.isFile) {
						pathTrack.push("/" + toLsPath.getBaseName());
						populateTable(toLsPath.getFullPath());
					}
				}
			}

		});
		explorerTable.setFillsViewportHeight(true);
		scrollPane.setViewportView(explorerTable);

		setSize(800, 500);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(this, popupMenu);

		mntmUpload = new JMenuItem("Upload to ...");
		mntmOpenFile = new JMenuItem("Open File");

		JMenuItem mntmDownload = new JMenuItem("Download");
		popupMenu.add(mntmDownload);

		JMenuItem mntmRename = new JMenuItem("Rename");
		popupMenu.add(mntmRename);

		JMenuItem mntmDelete = new JMenuItem("Delete");
		popupMenu.add(mntmDelete);

		popupMenu.addPopupMenuListener(new myTablePopupMenuListener());
		addPopup(explorerTable, popupMenu);

		explorerTable.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(e.getKeyCode());
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					moveBackHistory();

				}
			}

		});
		requestFocus();
	}

	protected void moveBackHistory() {
		if (!pathTrack.isEmpty()) {
			if (pathTrack.size() > 1)
				pathTrack.pop();
			populateTable(pathTrack.toString());
		}

	}

	protected void populateTable(String toLs) {
		((NonEditableTableModel) explorerTable.getModel()).setRowCount(0);
		String extension;
		toLs = toLs.replace("\\", "/");
		if (!toLs.endsWith("/"))
			toLs += "/";
		File[] listFiles = new File(toLs).listFiles();
		int count = 0;

		FileInfoStructure[] files = new FileInfoStructure[listFiles.length];
		for (File f : listFiles) {
			Icon icon = fsv.getSystemIcon(f);
			if (f.isFile()) {

				/*
				 * int dotIndex = f.getName().lastIndexOf(".");
				 * if (dotIndex > -1)
				 * extension = f.getName().substring(dotIndex);
				 * else
				 * extension = "void";
				 * if (!icons.containsKey(extension)) {
				 * File fi = new File("src/res/extensions/" + f.getName());
				 * try {
				 * fi.createNewFile();
				 * icons.put(extension, fsv.getSystemIcon(fi));
				 * } catch (IOException e) {
				 * // TODO Auto-generated catch block
				 * e.printStackTrace();
				 * }
				 * }
				 */

				files[count] = new FileInfoStructure(f.getAbsolutePath(), f.getName(), (ImageIcon) icon, f.length(),
						f.canExecute(), f.canRead(), f.canWrite());

			} else {
				//is Directory
				files[count] = new FileInfoStructure(f.getAbsolutePath(), f.getName(), (ImageIcon) icon);
			}

			if (files[count].isFile)
				((DefaultTableModel) explorerTable.getModel()).addRow(new Object[]{files[count],
						files[count].fileSize, files[count].canExecute, files[count].canRead, files[count].canWrite});
			else
				((DefaultTableModel) explorerTable.getModel()).addRow(new Object[]{files[count], "", "", "", ""});

			count++;
		}

	}

	public void drawGUI() {
		String path = "c:/";
		File directory = new File(path);
		if (!directory.isDirectory() || !directory.exists())
			return;
		if (icons.isEmpty()) {
			icons.put("folder", fsv.getSystemIcon(extF));
			File voidFile = new File("src/res/extensions/voidfile");
			if (voidFile.exists())
				try {
					voidFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			icons.put("void", fsv.getSystemIcon(voidFile));
			File[] roots = File.listRoots();
			int c = 0;
			for (File root : roots) {
				icons.put("root" + c++, fsv.getSystemIcon(root));
			}

			for (File f : extF.listFiles()) {
				if (f == null)
					continue;
				int dotIndex = f.getName().lastIndexOf(".");
				if (dotIndex == -1)
					continue;
				if (!icons.containsKey(f.getName().substring(dotIndex))) {
					icons.put(f.getName().substring(dotIndex), fsv.getSystemIcon(f));
				}
			}
		}

		String extension;
		File[] listFiles = directory.listFiles();
		int count = 0;

		FileInfoStructure[] files = new FileInfoStructure[listFiles.length];
		for (File f : listFiles) {
			if (f.isFile()) {
				int dotIndex = f.getName().lastIndexOf(".");
				if (dotIndex > -1)
					extension = f.getName().substring(dotIndex);
				else
					extension = "void";
				if (!icons.containsKey(extension)) {
					File fi = new File("src/res/extensions/" + f.getName());
					try {
						fi.createNewFile();
						icons.put(extension, fsv.getSystemIcon(fi));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				files[count++] = new FileInfoStructure(f.getAbsolutePath(), f.getName(), (ImageIcon) icons.get(extension),
						f.length(), f.canExecute(), f.canRead(), f.canWrite());

			} else {
				//is Directory
				files[count++] = new FileInfoStructure(f.getAbsolutePath(), f.getName(), (ImageIcon) icons.get("folder"));
			}
		}

		PacketFileExplorer packetFiles = new PacketFileExplorer(files, false);
		File[] roots = File.listRoots();
		count = 0;
		FileInfoStructure[] rootWrappers = new FileInfoStructure[roots.length];
		for (File f : roots) {
			rootWrappers[count] = new FileInfoStructure(f.getAbsolutePath(), (ImageIcon) icons.get("root" + count++));
		}

		PacketFileExplorer packetRoots = new PacketFileExplorer(rootWrappers, true);

		if (packetRoots.dataContent == PacketFileExplorer.DataContent.LIST_ROOTS) {
			for (FileInfoStructure f : packetRoots.fileInfoStructures) {
				listModel.addElement(f);
			}
		}
	}

	public static void main(String[] args) {
		MyFileSystemRemote filesystem = new MyFileSystemRemote();
		filesystem.setVisible(true);
		filesystem.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		filesystem.drawGUI();

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

	public class myTablePopupMenuListener implements PopupMenuListener {

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			FileInfoStructure f = (FileInfoStructure) explorerTable.getValueAt(explorerTable.getSelectedRow(), 0);
			JPopupMenu menu = (JPopupMenu) e.getSource();
			if (f.isFile) {
				menu.add(mntmOpenFile, 0);
				menu.remove(mntmUpload);
			} else {
				menu.add(mntmUpload, 0);
				menu.remove(mntmOpenFile);
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
}

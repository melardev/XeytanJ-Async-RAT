package com.melardev.xeytanj.trash;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class FileTreeNode implements Serializable {

	private static HashMap<String, Icon> icons = new HashMap<String, Icon>();
	private static FileSystemView fsv = FileSystemView.getFileSystemView();
	private static File extF = new File("src/res/extensions");
	private ImageIcon icon;
	private String fullPath;
	private String extension;
	private String basename;

	public FileTreeNode(String path) {
		fullPath = path.replace("\\", "/");
		basename = fullPath;
		if (fullPath.contains(".")) {
			extension = path.substring(path.lastIndexOf("."));
			if(fullPath.contains("/"))
				basename = path.substring(path.lastIndexOf("/"));
		} else {
			extension = "folder";
			basename = fullPath;
		}
		if (icons.isEmpty()) {
			icons.put("folder", fsv.getSystemIcon(extF));

			for (File f : extF.listFiles()) {
				if(f==null)
					continue;
				if (!icons.containsKey(f.getName())) {
					icons.put(f.getName().substring(f.getName().lastIndexOf(".")), fsv.getSystemIcon(f));
				}
			}
		}

		if (!icons.containsKey(extension)) {
			File fi = new File("src/res/extensions/" + basename);
			try {
				fi.createNewFile();
				icons.put(extension, fsv.getSystemIcon(fi));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Icon getIcon() {
		return icons.get(extension);
	}

	@Override
	public String toString() {
		return basename;
	}

	public boolean isLeaf(){
		return !extension.equals("folder");
	}
	public String getExtension() {
		return extension;
	}

	public boolean isFolder() {
		return extension.equals("folder");
	}

}

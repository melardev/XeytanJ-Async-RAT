package com.melardev.xeytanj.models;

import javax.swing.*;
import java.io.Serializable;

public class FileInfoStructure implements Serializable{

	public boolean canExecute;
	public String fullPath;
	public String name;
	public ImageIcon icon;
	public boolean canWrite;
	public boolean isFile;
	public boolean canRead;
	public long fileSize;
	public boolean isRoot;

	public boolean canExecute() {
		return canExecute;
	}

	public String getFullPath() {
		return fullPath;
	}

	public String getBaseName() {
		return name;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public boolean canWrite() {
		return canWrite;
	}

	public boolean isFile() {
		return isFile;
	}

	public boolean canRead() {
		return canRead;
	}

	public long getFileSize() {
		return fileSize;
	}

	public FileInfoStructure(String fullPath, String name, ImageIcon icon, long fileSize, boolean canExecute, boolean canRead,
							 boolean canWrite) {
		this.fullPath = fullPath;
		this.name = name;
		this.icon = icon;
		this.fileSize = fileSize;
		this.canExecute = canExecute;
		this.canWrite = canWrite;
		this.canRead = canRead;
		isFile = true;

	}

	public FileInfoStructure(String root, ImageIcon rootIcon) {
		isRoot = true;
		fullPath = root;
		icon = rootIcon;
	}

	public FileInfoStructure(String fullPath, String name, ImageIcon icon) {
		this.fullPath = fullPath;
		this.name = name;
		this.icon = icon;
	}

	@Override
	public String toString() {
		return fullPath;
	}

}

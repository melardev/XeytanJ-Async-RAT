package com.melardev.xeytanj.utils;

import com.melardev.xeytanj.XeytanJClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map.Entry;
import java.util.Properties;
import static com.melardev.xeytanj.globals.Globals.*;

public class FileUtils {

	private static FileUtils fileUtils;

	public static enum OS {
		LINUX, WINDOWS, MAC_OSX
	}

	private FileUtils() {
	}

	public static FileUtils getInstance() {
		if (fileUtils == null)
			fileUtils = new FileUtils();
		return fileUtils;
	}

	public void loadModule(String... resPath) {
		try {
			for (int i = 0; i < resPath.length; i++) {
				InputStream is = getClass().getResourceAsStream(resPath[i]);
				File out = File.createTempFile("xeytan", ".dll");
				Files.copy(is, Paths.get(out.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
				System.load(out.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void launchExecutable(String... resPath) {
		try {
			for (int i = 0; i < resPath.length; i++) {
				InputStream is = getClass().getResourceAsStream(resPath[i]);
				File out = File.createTempFile("xeytan", ".dll");
				Files.copy(is, Paths.get(out.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
				if (System.getProperty("os.name").toLowerCase().contains("windows")) {
					Runtime.getRuntime().exec("start " + out.getAbsolutePath());
				} else if (System.getProperty("os.name").toLowerCase().contains("linux")) {

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Properties props = System.getProperties();
		for (Entry<Object, Object> entry : props.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}

	public static void moveToTemp() {

	}

	public static String exportResFile(String filePath) {
		return exportResFile(filePath, OS.WINDOWS);
	}

	private static String exportResFile(String filePath, OS os) {
		String fileName = filePath.replace("\\", "/").substring(filePath.lastIndexOf("/"));//;.replaceAll("(/)|(.exe)", "");
		String extension;
		if (fileName.contains(".")) {
			extension = fileName.substring(fileName.lastIndexOf("."));
			fileName = fileName.replace(extension, "").replace("/", "");
		} else {
			if (os == OS.WINDOWS)
				extension = ".exe";
			else
				extension = ".bin";
		}
		if (DEBUG) {
			File file = new File(System.getProperty("java.io.tmpdir") + "\\" + fileName + extension);
			if (!file.exists()) {
				File fout;
				try {
					fout = File.createTempFile(fileName, extension);
					InputStream is = XeytanJClient.class.getResourceAsStream(filePath);
					Files.copy(is, Paths.get(fout.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
					File renamed = new File(System.getProperty("java.io.tmpdir") + "/" + fileName + extension);
					if(fout.renameTo(renamed))
						return renamed.getAbsolutePath();
					else
						return fout.getAbsolutePath();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return file.getAbsolutePath();
		} else {
			//move To current Directory
		}
		return null;
	}
}

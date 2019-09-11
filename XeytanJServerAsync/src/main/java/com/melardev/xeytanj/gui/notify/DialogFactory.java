package com.melardev.xeytanj.gui.notify;

import javax.swing.*;

public class DialogFactory {
	private static final JLabel lblIP = new JLabel("New label");

	public enum FrameType {
		NEW_CONNECTION
	}

	/**
	 * 
	 * @param type
	 *            if NEW_CONNECTION; 
	 *            then pass country,OS,pcName,globalIp
	 * @param s
	 * @return
	 */
	public static JDialog getFrame(FrameType type, String... s) {
		switch (type) {
		case NEW_CONNECTION:
			if(s.length == 4)
				return new ConnectionFrame(s[0], s[1], s[2], s[3]);
			else
				return new ConnectionFrame(null,null,null,null);
		}
		
		return null;
	}

}

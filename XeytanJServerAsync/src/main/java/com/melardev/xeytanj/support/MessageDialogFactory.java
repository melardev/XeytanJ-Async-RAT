package com.melardev.xeytanj.support;

import javax.swing.*;
import java.awt.*;

public class MessageDialogFactory {

	public static void showErrorMessage(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public static void showErrorMessage(Component component, String title, String message) {
		JOptionPane.showMessageDialog(component, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void showNotImplementedDialog() {
		showErrorMessage("Internal Error", "This feature is not implemented yet");
	}

	public static void showErrorMessageAsync(String title, String message) {
		new Thread(()->showErrorMessage(title, message)).start();
	}

}

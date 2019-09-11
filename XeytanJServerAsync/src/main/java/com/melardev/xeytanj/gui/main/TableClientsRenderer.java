package com.melardev.xeytanj.gui.main;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.net.URL;

public class TableClientsRenderer extends DefaultTableCellRenderer {

    private final String MODE;

    TableClientsRenderer(String mode) {
        MODE = mode;
    }

    @Override
    protected void setValue(Object paramObject) {
        super.setValue(paramObject);
        String path = null;

        if (paramObject == null)
            return;

        if (MODE.equals("os")) {
            if (paramObject.toString().toLowerCase().contains("windows"))
                path = "icons/os/windows_32.png";
            else if (paramObject.toString().toLowerCase().contains("android"))
                path = "icons/os/android_48.png";
            else if (paramObject.toString().toLowerCase().contains("linux"))
                path = "icons/os/linux_32.png";
        } else if (MODE.equals("country")) {
            // path = "icons/flags/" + paramObject + ".png";
            path = "icons/flags/Morocco.png";
        }

        URL icon = null;

        if (getClass().getClassLoader() != null && path != null)
            icon = getClass().getClassLoader().getResource(path);
        else
            System.out.println("Lol");


        if (icon != null) {
            setIcon(new ImageIcon(icon));
        } else
            setText("Null");
    }

    /*
     * @Override
     * public Component getTableCellRendererComponent(JTable paramJTable, Object
     * data, boolean isSelected,
     * boolean hasFocus, int row, int col) {
     *
     * //"Country", "External IP", "Internal IP", "Operating System", "Pc-Name",
     * "JRE version"
     * JLabel label = new JLabel();
     * if(col==0){
     * //data is the OS
     * String osPath = "/icons/os/"+data+".png";
     * URL urlIcon = getClass().getResource(osPath);
     * label.setIcon(new ImageIcon(urlIcon));
     * }else if(col==1){
     * //data is the country flag path
     * String countryFlagPath = "/icons/flags/"+data+".png";
     * URL urlIcon = getClass().getResource(countryFlagPath);
     * label.setIcon(new ImageIcon(urlIcon));
     *
     * }else
     * label.setText((String)data);
     *
     *
     *
     * if(isSelected){
     * super.setForeground(Color.black);
     * super.setBackground(Color.blue);
     * }
     * return label;
     * }
     */

}

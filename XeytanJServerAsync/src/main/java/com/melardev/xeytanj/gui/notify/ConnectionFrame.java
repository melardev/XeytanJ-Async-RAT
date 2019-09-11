package com.melardev.xeytanj.gui.notify;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ConnectionFrame extends JDialog {
    public ConnectionFrame() {
        setUndecorated(true);
    }

    private static final long serialVersionUID = 1L;
    private JLabel lblCountry;
    private JLabel lblOS;
    private JLabel lblPcName;
    private JLabel lblGlobalIP;

    public ConnectionFrame(String country, String OS, String pcName, String globalIp) {
        setUndecorated(true);
        setOpacity(0.8f);
        getContentPane().setBackground(Color.BLACK);
        getContentPane().setLayout(
                new MigLayout("", "[82px][][58px][72px][][]", "[][][][][][][][][][][][][][][32px][][][][][][][]"));
        lblCountry = new JLabel("");
        if (country != null)
            lblCountry.setIcon(new ImageIcon(getClass().getResource(country)));

        lblPcName = new JLabel("default");
        lblPcName.setForeground(Color.WHITE);
        getContentPane().add(lblPcName, "cell 3 1,alignx left,aligny center");

        lblCountry.setIcon(new ImageIcon(ConnectionFrame.class.getResource("/icons/flags/Morocco.png")));
        getContentPane().add(lblCountry, "cell 0 3,alignx center,aligny center");
        lblOS = new JLabel("");
        if (OS != null)
            lblOS.setIcon(new ImageIcon(ConnectionFrame.class.getResource(OS)));//"/icons/os/windows_32.png"
        if (pcName != null)
            lblPcName.setText(pcName);
        lblGlobalIP = new JLabel("default ip");
        if (globalIp != null)
            lblGlobalIP.setText(globalIp);

        lblOS.setIcon(new ImageIcon(ConnectionFrame.class.getResource("/icons/os/linux_32.png")));
        getContentPane().add(lblOS, "cell 1 3,alignx center,aligny center");

        lblGlobalIP.setForeground(Color.WHITE);
        getContentPane().add(lblGlobalIP, "cell 3 3,alignx left,aligny center");
        setSize(324, 81);
        setAlwaysOnTop(true);
    }

    public ConnectionFrame setIconFlag(String path) {
        URL icon = getClass().getClassLoader().getResource(path);
        if (icon != null)
            return setIconFlag(new ImageIcon());
        else
            return this;
    }

    public ConnectionFrame setIconFlag(ImageIcon icon) {
        lblCountry.setIcon(icon);
        return this;
    }

    public ConnectionFrame setIconOS(String os) {
        URL icon = getClass().getClassLoader().getResource(os);
        if (icon == null)
            getClass().getClassLoader().getResource("icons/os/a.png");
        return setIconOS(new ImageIcon());
    }

    public ConnectionFrame setIconOS(ImageIcon icon) {
        lblOS.setIcon(icon);
        return this;
    }

    public ConnectionFrame setPCName(String path) {
        lblPcName.setText(path);
        return this;
    }

    public ConnectionFrame setIP(String ip) {
        lblGlobalIP.setText(ip);
        return this;
    }

    public void animate() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) (d.getWidth() - getWidth()) - 10, (int) d.getHeight());
        setVisible(true);
        int untilY = getY() - getHeight()
                - Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration()).bottom - 20;
        int fromy = getY();
        for (int fromyy = getY(); fromyy > untilY; fromyy--) {
            setLocation(getX(), fromyy);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int y = getY(); y < fromy; y++) {
            setLocation(getX(), y);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        dispose();

    }

    public static void main(String[] args) {
        ConnectionFrame t = new ConnectionFrame("/icons/flags/Morocco.png", "/icons/os/windows_32.png",
                "pi�a", "192.1.1.1");
        t.animate();
    }

}

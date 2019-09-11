package com.melardev.xeytanj.gui.info;

import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.gui.IGuiUserOwned;
import com.melardev.xeytanj.gui.IUiListener;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.IAppMessages;
import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Component
public class InfoGUI extends JDialog implements IGuiUserOwned<IUiListener> {
    private JScrollPane scrollPane;
    private JLabel txtInfo;
    private IUiListener listener;
    private IUiMediator mediator;
    private String basic;

    private String props;
    private String env;
    private WindowAdapter windowListener;
    private Client client;
    private IAppMessages messageProvider;

    public InfoGUI() {
        windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                notifyMediatorOnClose();
            }
        };
    }

    @Override
    public void display() {

        getContentPane().setLayout(new MigLayout("", "[][grow][]", "[][grow][]"));

        scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, "cell 1 1,grow");

        txtInfo = new JLabel();
        scrollPane.setViewportView(txtInfo);
        if (getWindowListeners() != null && getWindowListeners().length == 0)
            addWindowListener(windowListener);
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        //TODO: change the JTextArea to JTree
        txtInfo.setText("<html> <body><h2>Basic<h2>"
                + "<p>" + basic + "</p>"
                + "<h2> Properties </h2>"
                + "<p>" + props + "</p>"
                + "<h2> Environment </h2>"
                + "<p>" + env + "</p>"
                + "</body></html>");
        // pack();
        setSize(600, 600);
    }

    @Override
    public void addListener(IUiListener iUiListener) {
        this.listener = listener;
    }

    @Override
    public IUiListener getListener() {
        return listener;
    }

    @Override
    public Client getClient() {
        return this.client;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public Subject getSubject() {
        return Subject.CLIENT_INFORMATION;
    }

    @Override
    public void setStatus(String status) {

    }

    @Override
    public void disableUi() {
        setEnabled(false);
    }

    @Override
    public IUiMediator getMediator() {
        return mediator;
    }

    @Override
    public void setMediator(IUiMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void resetState() {

    }

    public void setData(String basic, String env, String props) {
        this.basic = basic;
        this.env = env;
        this.props = props;
    }

    @Override
    public void setMessageProvider(IAppMessages messageProvider) {
        this.messageProvider = messageProvider;
    }

}

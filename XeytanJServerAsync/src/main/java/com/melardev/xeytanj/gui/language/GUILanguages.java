package com.melardev.xeytanj.gui.language;

import com.melardev.xeytanj.enums.Language;
import com.melardev.xeytanj.gui.IGui;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.services.IAppMessages;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Component
public class GUILanguages extends JDialog implements IGui<LanguageUiListener> {

    private JLabel iconFrench;
    private JLabel iconEnglish;
    private JLabel iconExit;
    private JLabel iconSpain;
    private LanguageUiListener listener;
    private IAppMessages messageProvider;
    private IUiMediator mediator;

    public GUILanguages() {
    }


    public class MouseClickListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseReleased(e);
            Object source = e.getSource();
            if (source == iconExit)
                System.exit(0);

            Language language = Language.SPANISH;
            if (source == iconEnglish) {
                language = Language.ENGLISH;
            } else if (source == iconFrench) {
                language = Language.FRENCH;
            } else if (source == iconSpain) {
                language = Language.SPANISH;
            }

            listener.onLanguageSelected(language);

        }

    }

    public void display() {

        //setModal(true);
//setModalityType(ModalityType.APPLICATION_MODAL);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                listener.onLanguageUiClosed();
            }
        });

        //setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        MouseClickListener clickHandler = new MouseClickListener();
        getContentPane().setBackground(Color.BLACK);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        /*iconExit = new JLabel("");
        iconExit.setIcon(new ImageIcon(GUILanguages.class.getResource("/icons/utils/Delete_16.png")));
        iconExit.addMouseListener(clickHandler);*/
        GridBagConstraints gbc_imgExit = new GridBagConstraints();
        gbc_imgExit.insets = new Insets(0, 0, 5, 5);
        gbc_imgExit.gridx = 9;
        gbc_imgExit.gridy = 0;
        //getContentPane().add(iconExit, gbc_imgExit);

        iconSpain = new JLabel("");
        iconSpain.setIcon(new ImageIcon(GUILanguages.class.getResource("/icons/flags/Spain.png")));
        iconSpain.addMouseListener(clickHandler);
        GridBagConstraints gbc_iconSpain = new GridBagConstraints();
        gbc_iconSpain.insets = new Insets(0, 0, 5, 5);
        gbc_iconSpain.gridx = 2;
        gbc_iconSpain.gridy = 1;
        getContentPane().add(iconSpain, gbc_iconSpain);

        iconEnglish = new JLabel("");
        iconEnglish.setIcon(new ImageIcon(GUILanguages.class.getResource("/icons/flags/England.png")));
        iconEnglish.addMouseListener(clickHandler);
        GridBagConstraints gbc_iconEnglish = new GridBagConstraints();
        gbc_iconEnglish.insets = new Insets(0, 0, 5, 5);
        gbc_iconEnglish.gridx = 4;
        gbc_iconEnglish.gridy = 1;
        getContentPane().add(iconEnglish, gbc_iconEnglish);

        iconFrench = new JLabel("");
        iconFrench.setIcon(new ImageIcon(GUILanguages.class.getResource("/icons/flags/France.png")));
        iconFrench.addMouseListener(clickHandler);
        GridBagConstraints gbc_iconFrench = new GridBagConstraints();
        gbc_iconFrench.insets = new Insets(0, 0, 5, 5);
        gbc_iconFrench.gridx = 6;
        gbc_iconFrench.gridy = 1;
        getContentPane().add(iconFrench, gbc_iconFrench);

        JLabel lblNewLabel_3 = new JLabel("Espa\u00F1ol");
        lblNewLabel_3.setForeground(Color.WHITE);
        GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
        gbc_lblNewLabel_3.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel_3.gridx = 2;
        gbc_lblNewLabel_3.gridy = 2;
        getContentPane().add(lblNewLabel_3, gbc_lblNewLabel_3);

        JLabel lblNewLabel_4 = new JLabel("ENGLISH");
        lblNewLabel_4.setForeground(Color.WHITE);
        GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
        gbc_lblNewLabel_4.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel_4.gridx = 4;
        gbc_lblNewLabel_4.gridy = 2;
        getContentPane().add(lblNewLabel_4, gbc_lblNewLabel_4);

        JLabel lblNewLabel_5 = new JLabel("Fran\u00E7ais");
        lblNewLabel_5.setForeground(Color.WHITE);
        GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
        gbc_lblNewLabel_5.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel_5.gridx = 6;
        gbc_lblNewLabel_5.gridy = 2;
        getContentPane().add(lblNewLabel_5, gbc_lblNewLabel_5);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setSize(305, 125);
        setResizable(false);
        setVisible(true);
    }

    public void addListener(LanguageUiListener listener) {
        this.listener = listener;
    }

    @Override
    public LanguageUiListener getListener() {
        return listener;
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


    @Override
    public void setMessageProvider(IAppMessages messageProvider) {
        this.messageProvider = messageProvider;
    }

}

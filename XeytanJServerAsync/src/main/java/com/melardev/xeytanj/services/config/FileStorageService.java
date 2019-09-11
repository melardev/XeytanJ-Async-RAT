package com.melardev.xeytanj.services.config;


import com.melardev.xeytanj.XeytanJApplication;
import com.melardev.xeytanj.errors.IOStorageException;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.data.IStorageService;
import com.melardev.xeytanj.services.logger.ILogger;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.Properties;

@Service
public class FileStorageService implements IStorageService {

    private Properties props;
    public static final String CONFIG_FILE_PATH = "./config.properties";
    public static final String CONFIG_COMMENT = "Configuration File for XeytanApplication RAT";

    public void init() {
        try {
            File configFile = null;
            if (!configFile.exists()) {

                System.out.println("Creating Configuration File");
                createConfigFile();

            } else {
                System.out.print("Loading Configuration File : ");
                FileInputStream fis = null;

                fis = new FileInputStream(configFile);

                props.load(fis);
                fis.close();
                if (props.size() > 0)
                    System.out.println("OK");
                else
                    System.err.println("Failed");
                boolean showDisclaimer;
           /*     if (props.getProperty(DISCLAIMER_ACCEPTED).equals(XEYTAN_TRUE))
                    showDisclaimer = false;*/
            }
            //We had either created the config file or readen it
            boolean success = true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConfigFile() throws IOException {
     /*   File configFile = new File(CONFIG_FILE_PATH);
        configFile.createNewFile();
        Properties props = Loader.getProps();

        synchronized (props) {
            props.setProperty(DEBUG, XEYTAN_TRUE);
            props.setProperty(PREF_USE_MYSQL, XEYTAN_FALSE);
            props.setProperty(USE_GEOIP, XEYTAN_FALSE);
            props.setProperty(USE_IP_NET, XEYTAN_TRUE);
            props.setProperty(DISCLAIMER_ACCEPTED, XEYTAN_FALSE);
            props.store(new FileOutputStream(configFile), CONFIG_COMMENT);
        }
        */
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }


    @Override
    public Client save(Client client) {
        return null;
    }

    @Override
    public void insertClient(Client client) throws IOStorageException {

    }

    @Override
    public List<Client> getAllClients() {
        return null;
    }

    @Override
    public void setLogger(ILogger logger) {

    }

    @Override
    public ConfigService.ConfigModel loadConfig() {
        return null;
    }

    @Override
    public void saveConfig(ConfigService.ConfigModel configModel) {

    }

    @Override
    public void dispose() {

        if (props != null && !props.values().isEmpty()) {
            try {
                props.store(new FileOutputStream(CONFIG_FILE_PATH), CONFIG_COMMENT);
            } catch (FileNotFoundException e) {
                XeytanJApplication app = XeytanJApplication.getInstance();
                app.onDisposeError(e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                JOptionPane.showConfirmDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}

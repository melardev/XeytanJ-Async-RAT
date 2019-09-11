package com.melardev.xeytanj.services.config;

import com.melardev.xeytanj.enums.Language;
import com.melardev.xeytanj.errors.UnableToLoadConfigurationException;
import com.melardev.xeytanj.services.data.IStorageService;
import org.springframework.stereotype.Service;

@Service
public class ConfigService implements IConfigService {
    private static final String NAME = "ConfigService";
    private IStorageService storage;

    private ConfigModel configModel;
    private boolean configHasChanged;

    public ConfigService() {
        init();
    }

    @Override
    public void init() throws UnableToLoadConfigurationException {
        configModel = new ConfigModel(); // Default Config Model
    }

    public void setStorage(IStorageService storage) {
        this.storage = storage;
        ConfigModel config = storage.loadConfig();
        if (config != null)
            configModel = config;
    }

    @Override
    public Language getPreferredLanguage() {
        return configModel.preferredLanguage;
    }

    @Override
    public void setHasAcceptedDisclaimer(boolean hasAcceptedDisclaimer) {
        configHasChanged = true;
        configModel.hasAcceptedDisclaimer = hasAcceptedDisclaimer;
    }

    public boolean hasAcceptedDisclaimer() {
        return configModel.hasAcceptedDisclaimer;
    }

    public boolean usingDb() {
        return configModel.usingDb;
    }

    public boolean hasGoneThroughInstallation() {
        return configModel.hasGoneThroughInstallation;
    }

    public String getDefaultHostname() {
        return configModel.defaultHostname;
    }

    public Long getDefaultPort() {
        return configModel.defaultPort;
    }

    public String getLoginKey() {
        return null;
    }

    public String getDefaultClientPath() {
        return null;
    }

    public String getDefaultClientName() {
        return null;
    }

    public void setDefaultClientPath(String defaultPath) {

    }

    public String getListeningKey() {
        return null;
    }

    @Override
    public boolean resolveGeoIpLocally() {
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void dispose() {
        if (configHasChanged) {
            storage.saveConfig(configModel);
        }
    }

    @Override
    public String getGoogleMapsKey() {
        return "AIzaSyDQ4TOfbVbbYtgLJfm4-aztPSVdbP1MjPg";
    }

    @Override
    public void setLanguage(Language lang) {
        configHasChanged = true;
        configModel.preferredLanguage = lang;
    }

    public static class ConfigModel {
        Language preferredLanguage = Language.ENGLISH; // Language.valueOf("esp".toUpperCase());
        boolean hasAcceptedDisclaimer = true;
        boolean resolveGeoIpLocally = false;
        boolean usingDb = true;
        boolean hasGoneThroughInstallation = true;
        String defaultHostname = "";
        Long defaultPort = 3002L;
    }
}

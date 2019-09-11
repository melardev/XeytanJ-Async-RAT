package com.melardev.xeytanj.services.config;

import com.melardev.xeytanj.enums.Language;
import com.melardev.xeytanj.errors.UnableToLoadConfigurationException;
import com.melardev.xeytanj.services.IService;

public interface IConfigService extends IService {
    void init() throws UnableToLoadConfigurationException;

    public boolean hasAcceptedDisclaimer();

    public boolean usingDb();

    public boolean hasGoneThroughInstallation();

    Language getPreferredLanguage();

    public void setHasAcceptedDisclaimer(boolean hasAcceptedDisclaimer);

    public String getDefaultHostname();

    public Long getDefaultPort();

    public String getLoginKey();

    public String getDefaultClientPath();

    public String getDefaultClientName();

    public void setDefaultClientPath(String defaultPath);

    public String getListeningKey();

    public boolean resolveGeoIpLocally();

    String getGoogleMapsKey();

    void setLanguage(Language lang);
}

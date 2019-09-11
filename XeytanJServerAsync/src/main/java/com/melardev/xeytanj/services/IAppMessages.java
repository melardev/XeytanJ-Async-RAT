package com.melardev.xeytanj.services;

import com.melardev.xeytanj.enums.Language;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class IAppMessages implements IService {

    private ResourceBundle messages;

    public IAppMessages() {
        Locale locale = Locale.getDefault();
        //locale = new Locale("es", "ES");
        messages = ResourceBundle.getBundle("messages/messages", locale);
    }

    void init() {

    }

    public String getText(String key) {
        return messages.getString(key);
    }


    @Override
    public void dispose() {

    }

    public void setLocaleForLanguage(Language lang) {
        Locale locale;
        switch (lang) {
            case SPANISH:
                locale = new Locale("es", "ES");
                break;
            case FRENCH:
                locale = new Locale("fr", "FR");
                break;

            case ENGLISH:
            default:
                locale = new Locale("en", "US");
        }

        messages = ResourceBundle.getBundle("messages/messages", locale);
    }
}

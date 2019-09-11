package com.melardev.xeytanj.gui.language;

import com.melardev.xeytanj.enums.Language;
import com.melardev.xeytanj.gui.IUiListener;

public interface LanguageUiListener extends IUiListener {
    void onLanguageSelected(Language lang);
    void onLanguageUiClosed();
}

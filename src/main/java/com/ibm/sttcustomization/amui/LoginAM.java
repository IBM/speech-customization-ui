package com.ibm.sttcustomization.amui;

import com.ibm.sttcustomization.Login;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@HtmlImport("styles/shared-styles.html")
@Route("loginam")
@Theme(Lumo.class)
public class LoginAM extends Login {
    @Override
    public String getDialogTitle() {
        return "Speech To Text Customizations";
    }
    @Override
    public String getNavigateOnLoginTo() {
        return "mainam";
    }
    @Override
    public String getLegacyLogin() {
        return "loginamlegacy";
    }
}

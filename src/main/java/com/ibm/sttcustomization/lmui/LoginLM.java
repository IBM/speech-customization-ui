package com.ibm.sttcustomization.lmui;

import com.ibm.sttcustomization.Login;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@HtmlImport("styles/shared-styles.html")
@Route("loginlm")
@Theme(Lumo.class)
public class LoginLM extends Login {
    @Override
    public String getDialogTitle() {
        return "Speech To Text Customizations";
    }
    @Override
    public String getNavigateOnLoginTo() {
        return "mainlm";
    }
    @Override
    public String getLegacyLogin() {
        return "loginlmlegacy";
    }
}

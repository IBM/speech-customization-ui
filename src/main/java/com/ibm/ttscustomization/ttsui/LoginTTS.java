package com.ibm.ttscustomization.ttsui;

import com.ibm.sttcustomization.Login;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.STTModel.STTModelsReply;
import com.ibm.sttcustomization.utils.Utils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.servlet.http.HttpServletRequest;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The main view contains a simple label element and a template element.
 */
@HtmlImport("styles/shared-styles.html")
@Route("logintts")
@Theme(Lumo.class)
public class LoginTTS extends Login {
    @Override
    public String getDialogTitle() {
        return "Text To Speech Customizations";
    }
    @Override
    public String getNavigateOnLoginTo() {
        return "maintts";
    }
    @Override
    public String getAttributePostfix() {
        return "tts";
    }
    @Override
    public String getLegacyLogin() {
        return "loginttslegacy";
    }
    @Override
    public String checkLogin() throws GenericException {
        return "";
    }


}

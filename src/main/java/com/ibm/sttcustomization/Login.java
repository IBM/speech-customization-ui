package com.ibm.sttcustomization;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.STTModel.STTModelsReply;
import com.ibm.sttcustomization.utils.Utils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.servlet.http.HttpServletRequest;

/**
 * The main view contains a simple label element and a template element.
 */
public class Login extends VerticalLayout {
    public String getDialogTitle() {
        return "";
    }
    public String getNavigateOnLoginTo() {
        return "";
    }
    public String getAttributePostfix() {
        return "";
    }
    public String getLegacyLogin() {
        return "";
    }
    public String checkLogin() throws GenericException {
        RestClient restClient = new RestClient();
        restClient.setUser("apikey");
        restClient.setPassword(apikeyFromLoginForm);
        restClient.setHost(urlFromLoginForm);
        STTModelsReply reply = restClient.querySTTModels();
        if (reply != null)
            return  reply.toString();
        else
            return "";
    }

    private final TextField textFieldAPIKEY;
    private final TextField textFieldURL;
    String apikeyFromLoginForm;
    String urlFromLoginForm;

    public Login() {
        Image image = new Image("img/screenshot1.jpg", "credentials screen");
        Div divRight = new Div(image);
        divRight.getStyle().set("position", "fixed");
        divRight.getStyle().set("right", "130px");

        Div divMain = new Div();
        divMain.getStyle().set("position", "fixed");
        divMain.getStyle().set("left", "100px");
        divMain.getStyle().set("top", "50px");

        textFieldAPIKEY = new TextField("API Key");
        textFieldURL = new TextField("URL");
        FormLayout formLayoutLogin = new FormLayout(textFieldAPIKEY, textFieldURL);

        Div divHeader = new Div();
        divMain.getStyle().set("font-size", "18px");
        divMain.getStyle().set("font-weight", "bold");
        divHeader.add(new Span(getDialogTitle()));
        divMain.add(divHeader);

        divMain.add(formLayoutLogin);

        HttpServletRequest currentRequest = ((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest();
        String requestUrl = currentRequest.getRequestURL().toString();

        if (requestUrl.contains("localhost")) {
        }


        // This is just a simple label created via Elements API
        Button btnLogin = new Button("Login", event -> {
            apikeyFromLoginForm = textFieldAPIKEY.getValue();
            urlFromLoginForm = textFieldURL.getValue() + "/v1";

            // here you would validate the credentials against a backend
            boolean bValidated = false;
            try {

                String sReply = checkLogin();
                // after a successfull login store username to session
                VaadinSession.getCurrent().setAttribute("username" + getAttributePostfix(), "apikey");
                VaadinSession.getCurrent().setAttribute("password" + getAttributePostfix(), apikeyFromLoginForm);
                VaadinSession.getCurrent().setAttribute("url" + getAttributePostfix(), urlFromLoginForm);

                getUI().ifPresent(ui -> ui.navigate(getNavigateOnLoginTo()));
                bValidated = true;
            } catch (GenericException e) {
                Utils.displayErrorMessage(e);
                bValidated = false;
            }
        });

        divMain.add(new H4());
        divMain.add(new H4());
        divMain.add(btnLogin);
        divMain.add(new H4());

        add(divMain, divRight);

        setClassName("main-layout");
    }

}

package com.ibm.sttcustomization.lmui;

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
@Route("loginlmlegacy")
@Theme(Lumo.class)
//@BodySize(height = "100%", width = "100%")
//@BodySize(width="1000px", height="1000px")
public class LoginLMLegacy extends VerticalLayout {

    private final TextField textFieldUser;
    private final PasswordField passwordField;
    private final ComboBox<String> cmbEnvironment;

    public LoginLMLegacy() {
        Div divMain = new Div();
        divMain.getStyle().set("position", "fixed");
        divMain.getStyle().set("left", "100px");
        divMain.getStyle().set("top", "50px");

        textFieldUser = new TextField("Login");
        passwordField = new PasswordField("Password");
        FormLayout formLayoutLogin = new FormLayout(textFieldUser, passwordField);

        Div divHeader = new Div();
        divMain.getStyle().set("font-size", "18px");
        divMain.getStyle().set("font-weight", "bold");
        divHeader.add(new Span("Speech To Text Customizations"));
        divMain.add(divHeader);

        divMain.add(formLayoutLogin);

        HttpServletRequest currentRequest = ((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest();
        String requestUrl = currentRequest.getRequestURL().toString();

        if (requestUrl.contains("localhost")) {
            //textFieldUser.setValue("6ab3b8d3-2d62-4243-b4a0-5b976ec0aa57");
            //passwordField.setValue("RxbAIKqEHUQV");
        }


        // This is just a simple label created via Elements API
        Button btnLogin = new Button("Login", event -> {
            String usernameFromLoginForm = textFieldUser.getValue();
            String passwordFromLoginForm = passwordField.getValue();
            setHostByCmbEnvironment();

            String url = (String) VaadinSession.getCurrent().getAttribute("url");

            // here you would validate the credentials against a backend
            boolean bValidated = false;
            try {
                RestClient restClient = new RestClient();
                restClient.setUser(usernameFromLoginForm);
                restClient.setPassword(passwordFromLoginForm);
                restClient.setHost(url);

                STTModelsReply sttModelsReply = restClient.querySTTModels();
                // after a successfull login store username to session
                VaadinSession.getCurrent().setAttribute("username", usernameFromLoginForm);
                VaadinSession.getCurrent().setAttribute("password", passwordFromLoginForm);

                getUI().ifPresent(ui -> ui.navigate("mainlm"));
                bValidated = true;
            } catch (GenericException e) {
                Utils.displayErrorMessage(e);
                bValidated = false;
            }
        });


        // This is a simple template example
        //add(btnLogin, template);


        cmbEnvironment = new ComboBox<>();
        SortedSet<String> ssFilters = new TreeSet<>();
        ssFilters.add("Prod");
        ssFilters.add("Stg");
        ssFilters.add("Dev");
        // cmbEnvironment.setTextInputAllowed(false);
        cmbEnvironment.setItems(ssFilters); //configure the possible options (usually, this is not a manually defined list...)
        cmbEnvironment.setValue("Stg"); //load the value of given data object
        cmbEnvironment.addValueChangeListener(e -> {
            setHostByCmbEnvironment();
        });

        divMain.add(new H4());
        divMain.add(cmbEnvironment);
        divMain.add(new H4());
        divMain.add(btnLogin);
        divMain.add(new H4());

        if (requestUrl.contains("localhost")) {
            Button btn1 = new Button("Keerthana's", e -> {
                VaadinSession.getCurrent().setAttribute("username", "6ab3b8d3-2d62-4243-b4a0-5b976ec0aa57");
                VaadinSession.getCurrent().setAttribute("password", "RxbAIKqEHUQV");
                VaadinSession.getCurrent().setAttribute("url", "https://gateway-s.watsonplatform.net/speech-to-text/api/v1");
                getUI().ifPresent(ui -> ui.navigate("mainlm"));
            });
            Button btn2 = new Button("Alex's regular", e -> {
                VaadinSession.getCurrent().setAttribute("username", "1ea95ff0-7ade-41ff-97d5-04a6db89bf99");
                VaadinSession.getCurrent().setAttribute("password", "Yvi4yiUJXrqa");
                VaadinSession.getCurrent().setAttribute("url", "https://gateway.watsonplatform.net/speech-to-text/api/v1");
                getUI().ifPresent(ui -> ui.navigate("mainlm"));
            });
            Button btn3 = new Button("Dev", e -> {
                VaadinSession.getCurrent().setAttribute("username", "1caa1631-0ff4-44d9-ae8c-f1a8731947f0");
                VaadinSession.getCurrent().setAttribute("password", "MJlT5vRDL6Xw");
                VaadinSession.getCurrent().setAttribute("url", "https://gateway-d.watsonplatform.net/speech-to-text/api/v1");
                getUI().ifPresent(ui -> ui.navigate("mainlm"));
            });
            HorizontalLayout hl = new HorizontalLayout(btn1, btn2, btn3);
            hl.setWidth("100%");
            divMain.add(hl);
        }

        add(divMain);

        setClassName("main-layout");
    }

    void setHostByCmbEnvironment() {
        String val = cmbEnvironment.getValue();
        if (val == null)
            return;
        if (val.equalsIgnoreCase("Prod")) {
            VaadinSession.getCurrent().setAttribute("url", "https://gateway.watsonplatform.net/speech-to-text/api/v1");
        }
        if (val.equalsIgnoreCase("Stg")) {
            VaadinSession.getCurrent().setAttribute("url", "https://gateway-s.watsonplatform.net/speech-to-text/api/v1");
        }
        if (val.equalsIgnoreCase("Dev")) {
            VaadinSession.getCurrent().setAttribute("url", "https://gateway-d.watsonplatform.net/speech-to-text/api/v1");
        }
    }


}

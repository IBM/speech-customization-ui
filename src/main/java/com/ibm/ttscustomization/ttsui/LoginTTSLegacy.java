package com.ibm.ttscustomization.ttsui;

import com.ibm.sttcustomization.model.RestClient;
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
@Route("loginttslegacy")
@Theme(Lumo.class)
//@BodySize(height = "100%", width = "100%")
//@BodySize(width="1000px", height="1000px")
public class LoginTTSLegacy extends VerticalLayout {

    private final TextField textFieldUser;
    private final PasswordField passwordField;
    private final ComboBox<String> cmbEnvironment;

    public LoginTTSLegacy() {
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
        divHeader.add(new Span("TTS Customizations"));
        divMain.add(divHeader);

        divMain.add(formLayoutLogin);



        HttpServletRequest currentRequest = ((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest();
        String requestUrl = currentRequest.getRequestURL().toString();

        if (requestUrl.contains("localhost")) {
            //textFieldUser.setValue("e9d232fd-7d1f-4c2d-beae-2eda3acf8c00");
            //passwordField.setValue("gSaIQFb0lX3c");
        }


        // This is just a simple label created via Elements API
        Button btnLogin = new Button("Login", event -> {
            String usernameFromLoginForm = textFieldUser.getValue();
            String passwordFromLoginForm = passwordField.getValue();
            setHostByCmbEnvironment();

            String url = (String) VaadinSession.getCurrent().getAttribute("urltts");

            // here you would validate the credentials against a backend
            boolean bValidated = false;
            //try
            {
                RestClient restClient = new RestClient();
                restClient.setUser(usernameFromLoginForm);
                restClient.setPassword(passwordFromLoginForm);
                restClient.setHost(url);

                //STTModelsReply sttModelsReply = restClient.querySTTModels();
                // after a successfull login store username to session
                VaadinSession.getCurrent().setAttribute("usernametts", usernameFromLoginForm);
                VaadinSession.getCurrent().setAttribute("passwordtts", passwordFromLoginForm);

                getUI().ifPresent(ui -> ui.navigate("maintts"));
                bValidated = true;
            }
//            catch (GenericException e) {
//                Utils.displayErrorMessage(e);
//                bValidated = false;
//            }


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
                VaadinSession.getCurrent().setAttribute("usernametts", "bdb86865-60a4-4e42-bfa8-4c91dfd583d2");
                VaadinSession.getCurrent().setAttribute("passwordtts", "L3MIsuh4AGpz");
                VaadinSession.getCurrent().setAttribute("urltts", "https://gateway-s.watsonplatform.net/text-to-speech/api/v1");
                getUI().ifPresent(ui -> ui.navigate("maintts"));
            });
            Button btn2 = new Button("Alex's regular", e -> {
                VaadinSession.getCurrent().setAttribute("usernametts", "e9d232fd-7d1f-4c2d-beae-2eda3acf8c00");
                VaadinSession.getCurrent().setAttribute("passwordtts", "gSaIQFb0lX3c");
                VaadinSession.getCurrent().setAttribute("urltts", "https://gateway-s.watsonplatform.net/text-to-speech/api/v1");
                getUI().ifPresent(ui -> ui.navigate("maintts"));
            });
            Button btn3 = new Button("Alex's lite", e -> {
                VaadinSession.getCurrent().setAttribute("usernametts", "74d51a8a-c224-4276-8d18-50b621707382");
                VaadinSession.getCurrent().setAttribute("passwordtts", "NOJyVJKozxIM");
                VaadinSession.getCurrent().setAttribute("urltts", "https://gateway-s.watsonplatform.net/text-to-speech/api/v1");
                getUI().ifPresent(ui -> ui.navigate("maintts"));
            });
            Button btn4 = new Button("Alex's prod", e -> {
                VaadinSession.getCurrent().setAttribute("usernametts", "08a2150c-2450-4a70-8278-54c8cb024343");
                VaadinSession.getCurrent().setAttribute("passwordtts", "vVn0t2tn4Mq0");
                VaadinSession.getCurrent().setAttribute("urltts", "https://gateway.watsonplatform.net/text-to-speech/api/v1");
                getUI().ifPresent(ui -> ui.navigate("maintts"));
            });
            HorizontalLayout hl = new HorizontalLayout(btn1, btn2, btn3, btn4);
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
            VaadinSession.getCurrent().setAttribute("urltts", "https://gateway.watsonplatform.net/text-to-speech/api/v1");
        }
        if (val.equalsIgnoreCase("Stg")) {
            VaadinSession.getCurrent().setAttribute("urltts", "https://gateway-s.watsonplatform.net/text-to-speech/api/v1");
        }
        if (val.equalsIgnoreCase("Dev")) {
            VaadinSession.getCurrent().setAttribute("urltts", "https://gateway-d.watsonplatform.net/text-to-speech/api/v1");
        }
    }


}

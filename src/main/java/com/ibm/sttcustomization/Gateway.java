package com.ibm.sttcustomization;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Autowired;

@HtmlImport("styles/shared-styles.html")
@Route("")
@Theme(Lumo.class)
//@Theme(value = Lumo.class, variant = Lumo.DARK)
public class Gateway extends VerticalLayout {

    private HorizontalLayout buildHeader() {
        Span leftCaption = new Span("/Speech Tools");
        Div divLeft = new Div(new Anchor("https://www.ibm.com/cloud/watson-studio", "Watson Studio"), leftCaption);
        divLeft.getStyle().set("position", "fixed");
        divLeft.getStyle().set("left", "30px");
        divLeft.getStyle().set("left", "30px");

        String route = "https://console.bluemix.net/docs/services/speech-to-text/getting-started.html#gettingStarted";
        Anchor anchor = new Anchor(route, "Docs");
        anchor.setTarget( "_blank" ) ;
        Div divRight = new Div(anchor);
        divRight.getStyle().set("position", "fixed");
        divRight.getStyle().set("right", "130px");
        //divRight.getStyle().set("text-align", "right");

        HorizontalLayout header = new HorizontalLayout();
        header.getStyle().set("font-size", "25px");
        header.setWidth("100%");
        header.setHeight("30px");
        header.add(divLeft, divRight);

        return header;
    }


    public Gateway() {
            HorizontalLayout hlHeader = buildHeader();
            hlHeader.setHeight("190px");
            add(hlHeader);

            HorizontalLayout hl = new HorizontalLayout();
            hl.setWidth("100%");

            Div divMain = new Div();
            divMain.getStyle().set("font-size", "35px");
            //divMain.getStyle().set("border-left", "10px solid blue`");
            //divMain.getStyle().set("top", "550px");

            Anchor anchorLM = new Anchor("loginlm", "Speech To Text Customizations");
            anchorLM.setWidth("100%");
//            Anchor anchorAM = new Anchor("loginam", "Speech To Text Acoustic Model Customizations");
//            anchorAM.setWidth("100%");
            Anchor anchorTTS = new Anchor("logintts", "Text to Speech Voice Customizations");
            anchorTTS.setWidth("100%");

//            divMain.add(anchorLM, new Div(), anchorAM, new Div(), anchorTTS);
            divMain.add(anchorLM, new Div(), new Div(), anchorTTS);

            divMain.setWidth("45%");

            Div divLeft = new Div();
            divLeft.setWidth("30%");
            //divLeft.getStyle().set("border-right-color", "blue`");
            divLeft.setClassName("border_right");
            Div divRight = new Div();divRight.setWidth("25%");

            hl.add(divLeft, divMain, divRight);

            add(hl);


            setSizeFull();
        }



}

package com.ibm.sttcustomization;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route("health")
public class Health extends Div {
    public Health(){
        setText("status OK");
    }
}

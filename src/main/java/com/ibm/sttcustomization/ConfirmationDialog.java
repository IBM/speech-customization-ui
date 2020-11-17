/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.sttcustomization;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;

import static com.ibm.sttcustomization.utils.Constants.COLOR_BE_CAREFULL_BACKGROUND;

/**
 * Simple confirmation dialog.
 */
public class ConfirmationDialog extends Dialog {

    private final H2 titleField = new H2();
    private final Div messageLabel = new Div();
    private final Button confirmButton = new Button();
    private final Button cancelButton = new Button();
    private Registration registrationForConfirm;

    /**
     * Default constructor.
     */
    public ConfirmationDialog(ComponentEventListener yeslistener, ComponentEventListener nolistener) {
        init(yeslistener, nolistener);
    }

    private void init(ComponentEventListener yeslistener, ComponentEventListener nolistener) {
        confirmButton.getStyle().set("background-color", COLOR_BE_CAREFULL_BACKGROUND);
        confirmButton.setText("OK");

        cancelButton.setText("Cancel");

        getElement().getClassList().add("confirm-dialog");
        confirmButton.getElement().setAttribute("dialog-confirm", true);
        confirmButton.setAutofocus(true);
        cancelButton.getElement().setAttribute("dialog-dismiss", true);

        if (yeslistener != null)
            confirmButton.addClickListener(yeslistener);
        if (nolistener != null)
            cancelButton.addClickListener(nolistener);

        HorizontalLayout buttonBar = new HorizontalLayout(confirmButton, cancelButton);
        buttonBar.setClassName("buttons");

        Div labels = new Div(messageLabel);
        labels.setClassName("text");

        registrationForConfirm = confirmButton.addClickListener(e -> close());
        cancelButton.addClickListener(e -> close());

        add(titleField, labels, buttonBar);
    }

    /**
     * Opens the confirmation dialog.
     *
     * The dialog will display the given title and message, then call
     * <code>confirmHandler</code> if the Confirm button is clicked, or
     * <code>cancelHandler</code> if the Cancel button is clicked.
     *
     * @param title
     *            The title text
     * @param message
     *            Detail message (optional, may be empty)
     */
    public void open(String title, String message) {
        if (title != null)
            titleField.setText(title);
        messageLabel.setText(message);

        if (registrationForConfirm != null) {
            registrationForConfirm.remove();
        }
        registrationForConfirm = confirmButton.addClickListener(e -> {
            //postpone.proceed();
            close();
        });

        open();
    }
}

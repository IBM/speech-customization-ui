package com.ibm.sttcustomization.lmui;

import com.ibm.sttcustomization.lmui.GrammarsTab;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.grammars.Grammar;
import com.ibm.sttcustomization.utils.Utils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

public class NewGrammar extends Dialog {
    private final RestClient restClient;
    private final Grammar grammar;
    private final GrammarsTab grammarsTab;
    private final LMCustomization lmcustomization;
    private final Grammar grammarBeingEdited;
    private final TextField txtFieldGrammarName;
    private byte[] byteArrayGrammarContent = null;
    final ComboBox<String> cmbGrammarContentTypes;

    public NewGrammar(Grammar grammar, boolean bTheCorporaIsNew, GrammarsTab grammarsTab, LMCustomization lmCustomization, RestClient restClient) {
        this.grammar = grammar;
        this.grammarsTab = grammarsTab;
        this.lmcustomization = lmCustomization;
        this.restClient = restClient;

        grammarBeingEdited = new Grammar(grammar);

        String sConfirmButtonName = "Create";

        if (bTheCorporaIsNew)
            add(new H2("Upload Grammar"));
        else {
            add(new H2("Grammar"));
            sConfirmButtonName = "Save";
        }


        //setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setWidth("600px");
        Binder<Grammar> binder = new Binder<>();

        txtFieldGrammarName = new TextField();
        txtFieldGrammarName.setValueChangeMode(ValueChangeMode.EAGER);

        this.cmbGrammarContentTypes = new ComboBox<>();
        SortedSet<String> ssGrammarContentTypes = new TreeSet<>();
        ssGrammarContentTypes.add("application/srgs");
        ssGrammarContentTypes.add("application/bnf");
        ssGrammarContentTypes.add("application/fsm");
        ssGrammarContentTypes.add("application/jsgf");
        ssGrammarContentTypes.add("application/srgs+xml");

        //cmbFilter.setTextInputAllowed(false);
        cmbGrammarContentTypes.setItems(ssGrammarContentTypes); //configure the possible options (usually, this is not a manually defined list...)
        cmbGrammarContentTypes.setValue("application/srgs"); //load the value of given data object
        cmbGrammarContentTypes.addValueChangeListener(e -> {});


        Label infoLabel = new Label();


        Button btnUpload = new Button("Upload");

        btnUpload.addClickListener(event -> {
            if (binder.writeBeanIfValid(grammarBeingEdited)) {
                String sRet = null;
                try {
                    sRet = restClient.uploadGrammar(byteArrayGrammarContent, lmcustomization.getCustomization_id(),
                            txtFieldGrammarName.getValue(), cmbGrammarContentTypes.getValue());
                    grammarsTab.getCustomizationTabs().reloadCurrentCustomization();
                    close();
                } catch (GenericException e) {
                    Utils.displayErrorMessage(e);
                }
                System.out.println(sRet);
            }
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.addClickListener(event -> {
            close();
        });


        layoutWithBinder.addFormItem(txtFieldGrammarName, "Grammar Name");
        layoutWithBinder.addFormItem(cmbGrammarContentTypes, "Grammar Type");

        HorizontalLayout actions = new HorizontalLayout(btnUpload, btnCancel);

        txtFieldGrammarName.setRequiredIndicatorVisible(true);

        binder.forField(txtFieldGrammarName)
                .withValidator(new StringLengthValidator("Please enter the Name of the Corpora", 1, null))
                .bind(Grammar::getName, Grammar::setName);


        Div divUploadGrammar = new Div();
        divUploadGrammar.setWidth("100%");
        divUploadGrammar.setHeight("400px");

        Label labelUploadGrammar = new Label("Upload Grammar");
        TextArea txtGrammarContent = new TextArea("Content:");
        txtGrammarContent.setWidth("100%");
        txtGrammarContent.setHeight("250px");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
                System.out.println(event.getFileName());
                try {
                    byteArrayGrammarContent = IOUtils.toByteArray(buffer.getInputStream());
                    txtGrammarContent.setValue(new String(byteArrayGrammarContent));
                } catch (IOException e) {
                    Utils.displayErrorMessage(e);
                }

        });

        HorizontalLayout hl = new HorizontalLayout(labelUploadGrammar, upload);
        hl.setWidth("100%");
        divUploadGrammar.add(hl, txtGrammarContent);
        //divUploadGrammar.add(hl);

        add(layoutWithBinder);
        add(divUploadGrammar);
        add(actions);
    }

}

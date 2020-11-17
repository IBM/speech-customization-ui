package com.ibm.sttcustomization.lmui;

import com.ibm.sttcustomization.lmui.CorporaTab;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.corpora.Corpus;
import com.ibm.sttcustomization.utils.Utils;
import com.vaadin.flow.component.button.Button;
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

public class NewCorpus extends Dialog {
    private final RestClient restClient;
    private final Corpus corpus;
    private final CorporaTab corporaTab;
    private final LMCustomization lmcustomization;
    private final Corpus corpusBeingEdited;
    private final TextField txtFieldCorporaName;
    private byte[] byteArrayCorpusContent = null;

    public NewCorpus(Corpus corpus, boolean bTheCorporaIsNew, CorporaTab corporaTab, LMCustomization lmCustomization, RestClient restClient) {
        this.corpus = corpus;
        this.corporaTab = corporaTab;
        this.lmcustomization = lmCustomization;
        this.restClient = restClient;

        corpusBeingEdited = new Corpus(corpus);

        String sConfirmButtonName = "Create";

        if (bTheCorporaIsNew)
            add(new H2("Upload Corpus"));
        else {
            add(new H2("Corpus"));
            sConfirmButtonName = "Save";
        }


        //setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setWidth("600px");
        Binder<Corpus> binder = new Binder<>();

        txtFieldCorporaName = new TextField();
        txtFieldCorporaName.setValueChangeMode(ValueChangeMode.EAGER);

        Label infoLabel = new Label();


        Button btnUpload = new Button("Upload");

        btnUpload.addClickListener(event -> {
            if (binder.writeBeanIfValid(corpusBeingEdited)) {
                String sRet = null;
                try {
                    sRet = restClient.uploadCorpus(byteArrayCorpusContent, lmcustomization.getCustomization_id(), txtFieldCorporaName.getValue());
                    corporaTab.getCustomizationTabs().reloadCurrentCustomization();
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


        layoutWithBinder.addFormItem(txtFieldCorporaName, "Corpus Name");

        HorizontalLayout actions = new HorizontalLayout(btnUpload, btnCancel);

        txtFieldCorporaName.setRequiredIndicatorVisible(true);

        binder.forField(txtFieldCorporaName)
                .withValidator(new StringLengthValidator("Please enter the Name of the Corpora", 1, null))
                .bind(Corpus::getName, Corpus::setName);


        Div divUploadCorpus = new Div();
        divUploadCorpus.setWidth("100%");
        divUploadCorpus.setHeight("400px");

        Label labelUploadCorpus = new Label("Upload Corpus");
        TextArea txtCorpusContent = new TextArea("Content:");
        txtCorpusContent.setWidth("100%");
        txtCorpusContent.setHeight("250px");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

 //       upload.setAcceptedFileTypes("text/corpus");

        upload.addSucceededListener(event -> {
                System.out.println(event.getFileName());
                try {
                    byteArrayCorpusContent = IOUtils.toByteArray(buffer.getInputStream());
                    txtCorpusContent.setValue(new String(byteArrayCorpusContent));
                } catch (IOException e) {
                    Utils.displayErrorMessage(e);
                }

        });

        HorizontalLayout hl = new HorizontalLayout(labelUploadCorpus, upload);
        hl.setWidth("100%");
        divUploadCorpus.add(hl, txtCorpusContent);
        //divUploadCorpus.add(hl);

        add(layoutWithBinder);
        add(divUploadCorpus);
        add(actions);

    }

}

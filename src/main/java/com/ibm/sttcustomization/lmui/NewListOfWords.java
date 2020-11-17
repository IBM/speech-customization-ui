package com.ibm.sttcustomization.lmui;

import com.ibm.sttcustomization.lmui.WordsTab;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.utils.Utils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class NewListOfWords extends Dialog {
    private final RestClient restClient;
    private final WordsTab wordsTab;
    private final LMCustomization lmcustomization;
    private byte[] byteArrayListOfWordsContent = null;

    public NewListOfWords(WordsTab wordsTab, LMCustomization lmCustomization, RestClient restClient) {
        this.wordsTab = wordsTab;
        this.lmcustomization = lmCustomization;
        this.restClient = restClient;

        String sConfirmButtonName = "Create";

            add(new H2("Upload List of Words"));


       // setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setWidth("600px");


        Label infoLabel = new Label();


        Button btnUpload = new Button("Upload");

        btnUpload.addClickListener(event -> {
                String sRet = null;
                try {
                    sRet = restClient.uploadListOfWords(byteArrayListOfWordsContent, lmcustomization.getCustomization_id());
                    wordsTab.getCustomizationTabs().reloadCurrentCustomization();
                    close();
                } catch (GenericException e) {
                    Utils.displayErrorMessage(e);
                }
                System.out.println(sRet);
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.addClickListener(event -> {
            close();
        });



        HorizontalLayout actions = new HorizontalLayout(btnUpload, btnCancel);



        Div divUploadListOfWords = new Div();
        divUploadListOfWords.setWidth("100%");
        divUploadListOfWords.setHeight("400px");

        Label labelUploadListOfWords = new Label("Upload List Of Words");
        TextArea txtListOfWordsContent = new TextArea("Content:");
        txtListOfWordsContent.setWidth("100%");
        txtListOfWordsContent.setHeight("250px");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
                System.out.println(event.getFileName());
                try {
                    byteArrayListOfWordsContent = IOUtils.toByteArray(buffer.getInputStream());
                    txtListOfWordsContent.setValue(new String(byteArrayListOfWordsContent));
                } catch (IOException e) {
                    Utils.displayErrorMessage(e);
                }

        });

        HorizontalLayout hl = new HorizontalLayout(labelUploadListOfWords, upload);
        hl.setWidth("100%");
        divUploadListOfWords.add(hl, txtListOfWordsContent);
        //divUploadListOfWords.add(hl);

        add(layoutWithBinder);
        add(divUploadListOfWords);
        add(actions);
    }

}

package com.ibm.sttcustomization.amui;

import com.ibm.sttcustomization.model.AMCustomizations.AMCustomization;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.audio.Audio;
import com.ibm.sttcustomization.utils.Utils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.io.IOUtils;
import java.io.IOException;

public class NewAudio extends Dialog {
    private final RestClient restClient;
    private final Audio audio;
    private final AMContentTab amContentTab;
    private final AMCustomization customization;
    private final Audio audioBeingEdited;
    private final TextField txtFieldAudioName;
    private final Audio m_audioContainer;
    private byte[] byteArrayAudioContent = null;
    private String sUploadedFileName;

    public NewAudio(Audio audio, Audio m_audioContainer, boolean bTheCorporaIsNew, AMContentTab amContentTab, AMCustomization customization, RestClient restClient) {
        this.audio = audio;
        this.m_audioContainer = m_audioContainer;
        this.amContentTab = amContentTab;
        this.customization = customization;
        this.restClient = restClient;

        audioBeingEdited = new Audio(audio);

        String sConfirmButtonName = "Create";

        if (bTheCorporaIsNew)
            add(new H2("Upload Audio"));
        else {
            add(new H2("Audio"));
            sConfirmButtonName = "Save";
        }

        //setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setWidth("600px");
        Binder<Audio> binder = new Binder<>();

        txtFieldAudioName = new TextField();
        txtFieldAudioName.setValueChangeMode(ValueChangeMode.EAGER);

        Button btnUpload = new Button("Upload");

        btnUpload.addClickListener(event -> {
            if (binder.writeBeanIfValid(audioBeingEdited)) {
                String sRet = null;
                try {
                    String extention = Utils.getExtention(sUploadedFileName);
                    String sContentType = "";
                    if (extention.equalsIgnoreCase("zip"))
                        sContentType = "application/zip";
                    else if (extention.equalsIgnoreCase("gz"))
                        sContentType = "application/gzip";
                    else if (extention.equalsIgnoreCase("tar"))
                        sContentType = "application/tar";
                    else
                        sContentType = "audio/" + extention;

                    sRet = restClient.uploadAudio(byteArrayAudioContent, this.customization.getCustomization_id(), txtFieldAudioName.getValue(),
                            sContentType);
                    amContentTab.getCustomizationTabs().reloadCurrentCustomization();
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


        layoutWithBinder.addFormItem(txtFieldAudioName, "Audio Name");

        HorizontalLayout actions = new HorizontalLayout(btnUpload, btnCancel);

        txtFieldAudioName.setRequiredIndicatorVisible(true);

        binder.forField(txtFieldAudioName)
                .withValidator(new StringLengthValidator("Please enter the Name of the Corpora", 1, null))
                .bind(Audio::getName, Audio::setName);


        Div divUploadAudio = new Div();
        divUploadAudio.setWidth("100%");
        divUploadAudio.setHeight("400px");

        Label labelUploadAudio = new Label("Upload Audior");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
            sUploadedFileName = event.getFileName();
                try {
                        byteArrayAudioContent = IOUtils.toByteArray(buffer.getInputStream());
                    } catch (IOException e) {
                        Utils.displayErrorMessage(e);
                    }

        });

        HorizontalLayout hl = new HorizontalLayout(labelUploadAudio, upload);
        hl.setWidth("100%");
        divUploadAudio.add(hl);

        add(layoutWithBinder);
        add(divUploadAudio);
        add(actions);
    }

}

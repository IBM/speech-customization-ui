package com.ibm.ttscustomization.ttsui;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.utils.Utils;
import com.ibm.ttscustomization.ttsmodel.RestClientTTS;
import com.ibm.ttscustomization.ttsmodel.customizations.TTSCustomization;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

//@Push
//@Push(transport = Transport.WEBSOCKET_XHR)
public class EditTTSCustomization extends Dialog {


    private final RestClientTTS restClient;
    private final boolean bCustomizationIsNew;
    private final MainTTS mainTTS;
    private final TTSCustomization customization;
    private final TTSCustomization customizationBeingEdited;
    private Div divTest;
    private Div divTextComparison;


    private void addFormField(String label, FormLayout layoutWithBinder, String value, Binder<TTSCustomization> binder,
                              String errorMessageForBinder, ValueProvider<TTSCustomization, String> getter, Setter<TTSCustomization, String> setter) {
        TextField txtField = new TextField();
        txtField.setWidth("90%");
        txtField .setValueChangeMode(ValueChangeMode.EAGER);
        txtField.setValue(value);
        txtField.setRequiredIndicatorVisible(true);

        if (setter == null)
            txtField.setReadOnly(true);

        binder.forField(txtField)
                .withValidator(new StringLengthValidator(errorMessageForBinder, 1, null))
                .bind(getter, setter);

        layoutWithBinder.addFormItem(txtField, label);
    }

    private void addFormField(String label, FormLayout layoutWithBinder, LocalDateTime value, Binder<TTSCustomization> binder,
                              String errorMessageForBinder, ValueProvider<TTSCustomization, LocalDateTime> getter, Setter<TTSCustomization, LocalDateTime> setter) {

        TextField txtField = new TextField();
        txtField.setWidth("90%");
//        txtField.setValueChangeMode(ValueChangeMode.EAGER);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = value.format(formatter);
        txtField.setValue(formatDateTime);
//        txtField.setRequiredIndicatorVisible(true);

        if (setter == null)
            txtField.setReadOnly(true);

//        binder.forField(txtField)
//                .withValidator(new StringLengthValidator(errorMessageForBinder, 1, null))
//                .bind(getter, setter);

        layoutWithBinder.addFormItem(txtField, label);
    }

//

    EditTTSCustomization(boolean bCustomizationIsNew, MainTTS mainTTS, TTSCustomization customization, RestClientTTS restClient) {
        this.bCustomizationIsNew = bCustomizationIsNew;
        this.mainTTS = mainTTS;
        this.customization = customization;
        this.restClient = restClient;

        customizationBeingEdited = new TTSCustomization(customization);

        if (bCustomizationIsNew)
            initNewModelEditing();
        else
            initExistingModelEditing();
    }

    private void initNewModelEditing() {
        String sConfirmButtonName = "Create";
        add(new H2("Create TTS Customization"));

        //setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setWidth("900px");
        Binder<TTSCustomization> binder = new Binder<>();

        addFormField("Name", layoutWithBinder, customizationBeingEdited.getName(), binder,
                "Please enter Name", TTSCustomization::getName, TTSCustomization::setName);

        SortedSet<String> ssLanguages = Utils.getVoiceRepository().getLanguagesAllowingCustomization();
        ComboBox<String> cmbLanguage = new ComboBox<String>();
        cmbLanguage.setItems(ssLanguages);
        String sDefaultModel = ssLanguages.first();
        for (String smodel: ssLanguages) {
            if (smodel.equalsIgnoreCase("en-US"))
                sDefaultModel = smodel;
        }
        cmbLanguage.setValue(sDefaultModel);

        cmbLanguage.addValueChangeListener(e -> {
            String value = e.getValue();
            customizationBeingEdited.setLanguage(value);
        });
        cmbLanguage.setWidth("90%");
        layoutWithBinder.addFormItem(cmbLanguage, "Language");

        TextField txtDescription = new TextField();
        txtDescription.addValueChangeListener(e -> {
            String value = e.getValue();
            customizationBeingEdited.setDescription(value);
        });
        layoutWithBinder.addFormItem(txtDescription, "Description");

        //        int progress = 0;
        Button btnSaveNewModel = new Button(sConfirmButtonName);
        Button btnCancel = new Button("Cancel");

        // Button bar
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidth("50%");
        actions.add(btnSaveNewModel, btnCancel);
        btnSaveNewModel.getStyle().set("marginRight", "10px");

        btnSaveNewModel.addClickListener(event -> {
            if (binder.writeBeanIfValid(customizationBeingEdited)) {
                //lmCustomizationBeingEdited.setDialect(txtDialect.getValue());
                //customizationBeingEdited.setDescription(txtDescription.getValue());
                //customizationBeingEdited.setBase_model_name(cmbBaseModels.getValue());
                createCustomization();
            } else {
//                BinderValidationStatus<TTSCustomization> validate = binder.validate();
//                String errorText = validate.getFieldValidationStatuses()
//                        .stream().filter(BindingValidationStatus::isError)
//                        .map(BindingValidationStatus::getMessage)
//                        .map(Optional::get).distinct()
//                        .collect(Collectors.joining(", "));
//                infoLabel.setText("There are errors: " + errorText);
            }
        });
        btnCancel.addClickListener(event -> {
            close();
        });


        add(layoutWithBinder);
        add(actions);

    }

    String sRet = null;
    String sRetUncustomized = null;

    private void initExistingModelEditing() {
        add(new H2("TTS Customization"));

        //setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setWidth("500px");
        //    layoutWithBinder.setHeight("1200px");
        Binder<TTSCustomization> binder = new Binder<>();

        addFormField("Name", layoutWithBinder, customizationBeingEdited.getName(), binder,
                "Please enter Name", TTSCustomization::getName, TTSCustomization::setName);

        SortedSet<String> ssLanguages = Utils.getVoiceRepository().getLanguagesAllowingCustomization();
        ComboBox<String> cmbLanguage = new ComboBox<String>();
        cmbLanguage.setItems(ssLanguages);
        String sDefaultModel = ssLanguages.first();
        for (String smodel: ssLanguages) {
            if (smodel.equalsIgnoreCase("en-US"))
                sDefaultModel = smodel;
        }
        cmbLanguage.setValue(sDefaultModel);

        cmbLanguage.addValueChangeListener(e -> {
            String value = e.getValue();
            customizationBeingEdited.setLanguage(value);
        });
        cmbLanguage.setWidth("90%");
        cmbLanguage.setEnabled(false);
        layoutWithBinder.addFormItem(cmbLanguage, "Language");

        TextField txtDescription = new TextField();
        txtDescription.addValueChangeListener(e -> {
            String value = e.getValue();
            customizationBeingEdited.setDescription(value);
        });
        txtDescription.setValue(customizationBeingEdited.getDescription());
        layoutWithBinder.addFormItem(txtDescription, "Description");

        Label infoLabel = new Label();

        Div placeHolder = new Div();
        placeHolder.setWidth("230px");

        // Button bar
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidth("50%");

        Button btnSaveNewModel = new Button("Save");
        btnSaveNewModel.getStyle().set("marginRight", "10px");

        Button btnSave = new Button("Save");
        btnSave.addClickListener(event -> {
            if (binder.writeBeanIfValid(customizationBeingEdited)) {
                saveCustomization();
            } else {
                BinderValidationStatus<TTSCustomization> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses()
                        .stream().filter(BindingValidationStatus::isError)
                        .map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct()
                        .collect(Collectors.joining(", "));
                infoLabel.setText("There are errors: " + errorText);
            }

        });
        Button btnCancel = new Button("Cancel");

        btnCancel.addClickListener(event -> {
            close();
        });

        //actions.add(btnReset, btnDelete, btnCancel);
        actions.add(btnSave, btnCancel);

        VerticalLayout vlMainLeft = new VerticalLayout(layoutWithBinder, actions);

        EnableDisableButtons();

        add(vlMainLeft);
    }

    private void saveCustomization() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", customizationBeingEdited.getName().trim());
            jsonObject.put("description", customizationBeingEdited.getDescription().trim());
            String sRet = restClient.updateCustomVoice(jsonObject, customization.getCustomization_id());
            JSONObject jsoRet = new JSONObject(sRet);

            customization.setName(customizationBeingEdited.getName().trim());
            customization.setDescription(customizationBeingEdited.getDescription().trim());

            //Utils.customizationUpdatedSuccessfully(customization);
            mainTTS.refreshView();

            this.close();
        }
        catch (GenericException genericException) {
            Utils.displayErrorMessage(genericException);
        }
    }

    private void createCustomization() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", customizationBeingEdited.getName().trim());
            jsonObject.put("language", customizationBeingEdited.getLanguage().trim());
            jsonObject.put("description", customizationBeingEdited.getDescription().trim());

            String sRet = restClient.createCustomVoice(jsonObject);
            JSONObject jsoRet = new JSONObject(sRet);
            String customization_id = (String) jsoRet.get("customization_id");

            customization.setName(customizationBeingEdited.getName().trim());
            customization.setDescription(customizationBeingEdited.getDescription().trim());
            customization.setLanguage(customizationBeingEdited.getLanguage().trim());
            customization.setCustomization_id(customization_id);

            // add new customization to the repositary
            // update grid
            mainTTS.refreshView();
            this.close();
        }
        catch (GenericException genericException) {
            Utils.displayErrorMessage(genericException);
        }
    }



    void EnableDisableButtons() {
//        if (customizationBeingEdited.getStatus().equalsIgnoreCase("pending")) {
//            btnTrain.setEnabled(false);
//            btnReset.setEnabled(false);
//            checkboxClean.setEnabled(false);
//            checkboxForce.setEnabled(false);
//            checkboxUseCustomLanguageModel.setEnabled(false);
//        }
//        if (customizationBeingEdited.getStatus().equalsIgnoreCase("ready")) {
//            //btnTrain.setEnabled(false);
//            //cmbTrain.setEnabled(false);
//            //btnReset.setEnabled(false);
//            //checkboxClean.setEnabled(false);
//            //checkboxForce.setEnabled(false);
//            //checkboxUseCustomLanguageModel.setEnabled(false);
//        }
//        if (customizationBeingEdited.getStatus().equalsIgnoreCase("training")) {
//            btnTrain.setEnabled(false);
//            btnReset.setEnabled(false);
//            checkboxClean.setEnabled(false);
//            checkboxForce.setEnabled(false);
//            checkboxUseCustomLanguageModel.setEnabled(false);
//        }
//        if (customizationBeingEdited.getStatus().equalsIgnoreCase("upgrading")) {
//            btnTrain.setEnabled(false);
//            btnReset.setEnabled(false);
//            checkboxClean.setEnabled(false);
//            checkboxForce.setEnabled(false);
//            checkboxUseCustomLanguageModel.setEnabled(false);
//        }
//        if (customizationBeingEdited.getStatus().equalsIgnoreCase("failed")) {
//            //btnTrain.setEnabled(false);
//            //cmbTrain.setEnabled(false);
//            //btnReset.setEnabled(false);
//            //checkboxClean.setEnabled(false);
//            //checkboxForce.setEnabled(false);
//            //checkboxUseCustomLanguageModel.setEnabled(false);
//        }
//        if (customizationBeingEdited.getStatus().equalsIgnoreCase("expired")) {
//            btnTrain.setEnabled(false);
//            btnReset.setEnabled(false);
//            checkboxClean.setEnabled(false);
//            checkboxForce.setEnabled(false);
//            checkboxUseCustomLanguageModel.setEnabled(false);
//        }
//        if (customizationBeingEdited.getStatus().equalsIgnoreCase("available")) {
//            //btnTrain.setEnabled(false);
//            //cmbTrain.setEnabled(false);
//            //btnReset.setEnabled(false);
//            //checkboxClean.setEnabled(false);
//            //checkboxForce.setEnabled(false);
//            //checkboxUseCustomLanguageModel.setEnabled(false);
//        }
//


    }
}

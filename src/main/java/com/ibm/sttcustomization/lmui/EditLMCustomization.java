package com.ibm.sttcustomization.lmui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.utils.Utils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.FailedEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.function.ValueProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.ibm.sttcustomization.utils.Constants.COLOR_BE_CAREFULL_BACKGROUND;

//@Push
//@Push(transport = Transport.WEBSOCKET_XHR)
public class EditLMCustomization extends Dialog {

    private Button btnTrain;
    private Button btnReset;
    private Div divTest;
    private Div divTextComparison;

    class Worker implements Runnable {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;
        private final boolean bCustomModel;
        private final String sFileName;
        MemoryBuffer buffer;
        String sRet;

        Worker(CountDownLatch startSignal, CountDownLatch doneSignal, MemoryBuffer buffer, boolean bCustomModel, String sFileName) {
            this.buffer = buffer;
            this.bCustomModel = bCustomModel;
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
            this.sFileName = sFileName;
        }
        public void run() {
            try {
                startSignal.await();
                doWork();
                doneSignal.countDown();
            } catch (InterruptedException ex) {
                doneSignal.countDown();
            } // return;
        }

        void doWork() {
            try {
                sRet = restClient.recognizeLM(buffer.getInputStream(), bCustomModel ? lmcustomization.getCustomization_id() : null,
                        lmcustomization.getBase_model_name(), sFileName);
            } catch (GenericException e) {
                Utils.displayErrorMessage(e);
            }

        }
    }

    private final RestClient restClient;
    private final boolean bCustomizationIsNew;
    private final MainLM mainLM;
    private final LMCustomization lmcustomization;
    private final LMCustomization lmCustomizationBeingEdited;
    private ComboBox<String> cmbTrain;
    private TextArea txtTestResultsWithCustomization;
    private TextArea txtTestResultsWithBaseModel;

    private void addFormField(String label, FormLayout layoutWithBinder, String value, Binder<LMCustomization> binder,
                              String errorMessageForBinder, ValueProvider<LMCustomization, String> getter, Setter<LMCustomization, String> setter) {
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

    private void addFormField(String label, FormLayout layoutWithBinder, LocalDateTime value, Binder<LMCustomization> binder,
                              String errorMessageForBinder, ValueProvider<LMCustomization, LocalDateTime> getter, Setter<LMCustomization, LocalDateTime> setter) {

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

    EditLMCustomization(boolean bCustomizationIsNew, MainLM mainLM, LMCustomization lmCustomization, RestClient restClient) {
        this.bCustomizationIsNew = bCustomizationIsNew;
        this.mainLM = mainLM;
        this.lmcustomization = lmCustomization;
        this.restClient = restClient;

        lmCustomizationBeingEdited = new LMCustomization(lmCustomization);

        if (bCustomizationIsNew)
            initNewModelEditing();
        else
            initExistingModelEditing();
    }

    private void initNewModelEditing() {
        String sConfirmButtonName = "Create";
        add(new H2("Create LM Customization"));

        //setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setWidth("900px");
        Binder<LMCustomization> binder = new Binder<>();

        addFormField("Name", layoutWithBinder, lmCustomizationBeingEdited.getName(), binder,
                "Please enter Name", LMCustomization::getName, LMCustomization::setName);

//        addFormField("Base Model Name", layoutWithBinder, lmCustomizationBeingEdited.getBase_model_name(), binder,
//                "Please enter Base Model Name", LMCustomization::getBase_model_name, LMCustomization::setBase_model_name);
        ComboBox<String> cmbBaseModels = new ComboBox<String>();
        cmbBaseModels.setItems(mainLM.getModelsAllowingCustomization());
        cmbBaseModels.setValue(mainLM.getModelsAllowingCustomization().first());
        String sDefaultModel = mainLM.getModelsAllowingCustomization().first();
        for (String smodel: mainLM.getModelsAllowingCustomization()) {
            if (smodel.equalsIgnoreCase("en-US_BroadbandModel"))
                sDefaultModel = smodel;
        }
        cmbBaseModels.setValue(sDefaultModel);

        cmbBaseModels.addValueChangeListener(e -> {
            String value = e.getValue();
            lmCustomizationBeingEdited.setBase_model_name(value);
        });
        cmbBaseModels.setWidth("90%");
        layoutWithBinder.addFormItem(cmbBaseModels, "Base Model");

        // optional
        //addFormField("Dialect", layoutWithBinder, lmCustomizationBeingEdited.getDialect(), binder,
        //       "Please enter Dialect", LMCustomization::getDialect, LMCustomization::setDialect);

//        ComboBox<String> cmbDialect = new ComboBox<String>();
//        cmbDialect.setItems(mainLM.getLanguagesAllowingCustomization());
//        cmbDialect.setValue(mainLM.getLanguagesAllowingCustomization().first());
//        cmbDialect.addValueChangeListener(e -> {
//            String value = e.getValue();
//            lmCustomizationBeingEdited.setDialect(value);
//        });
//        layoutWithBinder.addFormItem(cmbDialect, "Dialect");

        TextField txtDialect = new TextField();
        txtDialect.addValueChangeListener(e -> {
            String value = e.getValue();
            lmCustomizationBeingEdited.setDialect(value);
        });
        layoutWithBinder.add();
        layoutWithBinder.addFormItem(txtDialect, "Dialect");
//        // optional
//        addFormField("Description", layoutWithBinder, lmCustomizationBeingEdited.getDescription(), binder,
//                "Please enter Description", LMCustomization::getDescription, LMCustomization::setDescription);
        TextField txtDescription = new TextField();
        txtDescription.addValueChangeListener(e -> {
            String value = e.getValue();
            lmCustomizationBeingEdited.setDescription(value);
        });
        layoutWithBinder.addFormItem(txtDescription, "Description");

        //        int progress = 0;

        Label infoLabel = new Label();

        Button btnSaveNewModel = new Button(sConfirmButtonName);
        Button btnCancel = new Button("Cancel");

        // Button bar
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidth("50%");
        actions.add(btnSaveNewModel, btnCancel);
        btnSaveNewModel.getStyle().set("marginRight", "10px");

        btnSaveNewModel.addClickListener(event -> {
            if (binder.writeBeanIfValid(lmCustomizationBeingEdited)) {
                lmCustomizationBeingEdited.setDialect(txtDialect.getValue());
                lmCustomizationBeingEdited.setDescription(txtDescription.getValue());
                lmCustomizationBeingEdited.setBase_model_name(cmbBaseModels.getValue());
                saveNewModel();
            } else {
                BinderValidationStatus<LMCustomization> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses()
                        .stream().filter(BindingValidationStatus::isError)
                        .map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct()
                        .collect(Collectors.joining(", "));
                infoLabel.setText("There are errors: " + errorText);
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
        add(new H2("LM Customization"));

        //setCloseOnEsc(false);
        setCloseOnOutsideClick(false);


        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setWidth("500px");
        //    layoutWithBinder.setHeight("1200px");
        Binder<LMCustomization> binder = new Binder<>();

        // all read-only
        addFormField("Id", layoutWithBinder, lmCustomizationBeingEdited.getCustomization_id(), binder,
                "Please enter Customization Id", LMCustomization::getCustomization_id, null);

        addFormField("Name", layoutWithBinder, lmCustomizationBeingEdited.getName(), binder,
                "Please enter Name", LMCustomization::getName, null);

        addFormField("Owner", layoutWithBinder, lmCustomizationBeingEdited.getOwner(), binder,
                "Please enter Owner", LMCustomization::getOwner, null);

        addFormField("Base Model Name", layoutWithBinder, lmCustomizationBeingEdited.getBase_model_name(), binder,
                "Please enter Base Model Name", LMCustomization::getBase_model_name, null);

        addFormField("Dialect", layoutWithBinder, lmCustomizationBeingEdited.getDialect(), binder,
                "Please enter Dialect", LMCustomization::getDialect, null);

        addFormField("Date Created", layoutWithBinder, lmCustomizationBeingEdited.getCreatedLocalDate(), binder,
                "Please enter Description", LMCustomization::getCreatedLocalDate, null);

        addFormField("Versions", layoutWithBinder, lmCustomizationBeingEdited.getVersionsAsString(), binder,
                "Please enter versions", LMCustomization::getVersionsAsString, null);

        addFormField("Description", layoutWithBinder, lmCustomizationBeingEdited.getDescription(), binder,
                "Please enter Description", LMCustomization::getDescription, null);

        //        int progress = 0;
        addFormField("Language", layoutWithBinder, lmCustomizationBeingEdited.getLanguage(), binder,
                "Please enter Language", LMCustomization::getLanguage, null);

        addFormField("Status", layoutWithBinder, lmCustomizationBeingEdited.getStatus(), binder,
                "Please enter Status", LMCustomization::getStatus, null);

        Label infoLabel = new Label();

        btnTrain = new Button("Train");
        btnTrain.addClickListener(event -> trainModel());

        cmbTrain = new ComboBox<>();
        SortedSet<String> ssTrainOptions = new TreeSet<>();
        ssTrainOptions.add("Using All Words");
        ssTrainOptions.add("Using User Words Only");

        // cmbEnvironment.setTextInputAllowed(false);
        cmbTrain.setItems(ssTrainOptions); //configure the possible options (usually, this is not a manually defined list...)
        cmbTrain.setValue("Using All Words"); //load the value of given data object

        Label labelTestModel = new Label("Test Customization (up to 2MB file)");
        txtTestResultsWithCustomization = new TextArea("Customized:");
        txtTestResultsWithBaseModel =  new TextArea("Uncustomized:");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.setAcceptedFileTypes("audio/wav", "audio/flac");
        upload.setMaxFileSize(2048576);

        upload.addFailedListener(event -> {
            Utils.displayErrorMessage(event.getReason());
        });

        upload.addSucceededListener(event -> {
//            System.out.println(event.getFileName());
//            CountDownLatch startSignal = new CountDownLatch(1);
//            CountDownLatch doneSignal = new CountDownLatch(2);
//
//            Worker workerCustomizaed =  new Worker(startSignal, doneSignal, buffer, true);
//            Worker workerBase =  new Worker(startSignal, doneSignal, buffer, false);
//            new Thread(workerCustomizaed).start();
//            new Thread(workerBase).start();
//
//            //doSomethingElse();            // don't let run yet
//            startSignal.countDown();      // let all threads proceed
//            //doSomethingElse();
//
//            try {
//                doneSignal.await();           // wait for all to finish
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            txtTestResultsWithCustomization.setValue(Utils.extractTranscript(workerCustomizaed.sRet));
//            txtTestResultsWithBaseModel.setValue(Utils.extractTranscript(workerBase.sRet));

            try {
                String sRetCustomized = restClient.recognizeLM(buffer.getInputStream(), lmcustomization.getCustomization_id(), lmcustomization.getBase_model_name(), event.getFileName());
                //String sRetCustomized = restClient.recognizeLMSpeechHub(buffer.getInputStream(), lmcustomization.getCustomization_id(), lmcustomization.getBase_model_name(), event.getFileName());
                sRetCustomized = Utils.extractTranscript(sRetCustomized);
                txtTestResultsWithCustomization.setValue(sRetCustomized);
                String sRetUncustomized = restClient.recognizeLM(buffer.getInputStream(), null, lmcustomization.getBase_model_name(), event.getFileName());
                sRetUncustomized = Utils.extractTranscript(sRetUncustomized);
                txtTestResultsWithBaseModel.setValue(sRetUncustomized);

                divTextComparison.getElement().removeFromParent();
                divTextComparison = new Div();
                divTest.add(divTextComparison);
                Utils.comparison(divTextComparison.getElement(), sRetCustomized, sRetUncustomized);

            } catch (GenericException e) {
                Utils.displayErrorMessage(e);
            }
        });

        Div placeHolder = new Div();
        placeHolder.setWidth("230px");

        btnReset = new Button("Reset");
        btnReset.addClickListener(event -> {
            if (binder.writeBeanIfValid(lmCustomizationBeingEdited)) {
                ComponentEventListener yeslistener = (ComponentEventListener) event13 -> resetModel();
                ComponentEventListener nolistener = (ComponentEventListener) event14 -> { };
                ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
                getUI().ifPresent(ui -> ui.add(dialog));

                dialog.open(null, "Are you sure you want to reset this customization?");
            } else {
                BinderValidationStatus<LMCustomization> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses()
                        .stream().filter(BindingValidationStatus::isError)
                        .map(BindingValidationStatus::getMessage)
                        .map(Optional::get).distinct()
                        .collect(Collectors.joining(", "));
                infoLabel.setText("There are errors: " + errorText);
            }
        });

        // Button bar
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidth("50%");
//        Button btnDelete = new Button("Delete");
//        btnDelete.getStyle().set("background-color", COLOR_BE_CAREFULL_BACKGROUND);
//        btnDelete.addClickListener(event -> {
//                ComponentEventListener yeslistener = (ComponentEventListener) event1 -> deleteModel();
//                ComponentEventListener nolistener = (ComponentEventListener) event12 -> { };
//                ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
//                getUI().ifPresent(ui -> ui.add(dialog));
//                dialog.open(null, "Are you sure you want to delete this customization?");
//        });
        Button btnCancel = new Button("Cancel");
        btnTrain.getStyle().set("marginRight", "10px");

        btnCancel.addClickListener(event -> {
            close();
        });

        //actions.add(btnReset, btnDelete, btnCancel);
        actions.add(btnReset, btnCancel);

//        VerticalLayout vl = new VerticalLayout(labelTestModel, upload);
        upload.setWidth("90%");
        txtTestResultsWithCustomization.setWidth("370px");
        txtTestResultsWithCustomization.setHeight("400px");

        txtTestResultsWithBaseModel.setWidth("370px");
        txtTestResultsWithBaseModel.setHeight("400px");

        divTest = new Div();
        divTest.setWidth("100%");
//        divTest.setHeight("300px");
        HorizontalLayout hlResults = new HorizontalLayout(txtTestResultsWithCustomization, txtTestResultsWithBaseModel);
        hlResults.setWidth("100%");
        divTest.add(labelTestModel, upload, hlResults);

        divTextComparison = new Div();
        divTest.add(divTextComparison);

        HorizontalLayout hlTrain = new HorizontalLayout(btnTrain, cmbTrain, placeHolder);


        VerticalLayout vlMainLeft = new VerticalLayout(layoutWithBinder, hlTrain, actions);
        VerticalLayout vlMainRight = new VerticalLayout(divTest);
        vlMainRight.setWidth("700px");

        HorizontalLayout hlMain = null;
        if (lmCustomizationBeingEdited.getStatus().equalsIgnoreCase("available")) {
            hlMain = new HorizontalLayout(vlMainLeft, vlMainRight);
        }
        else {
            hlMain = new HorizontalLayout(vlMainLeft);
        }

        EnableDisableButtons();

        add(hlMain);


    }

    private void someMethod() {
        new Thread(() -> {
            UI.getCurrent().access(() -> {
            });
        }).start();
    }

    private void resetModel() {
        try {
            restClient.resetLMCustomization(lmCustomizationBeingEdited);
            mainLM.refreshView();
            close();
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    private void trainModel() {
        try {
            String sWordTypeToAdd = "all";
            String sValue = cmbTrain.getValue();
            if (sValue.equalsIgnoreCase("Using User Words Only")) {
                sWordTypeToAdd =  "user";
            }

            restClient.trainLMCustomization(lmCustomizationBeingEdited, sWordTypeToAdd);
            mainLM.refreshView();
            close();
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    private void deleteModel() {
        try {
            restClient.deleteLMCustomization(lmCustomizationBeingEdited);
            mainLM.refreshView();
            close();
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }


    private void saveNewModel() {
        try {
            restClient.createLMCustomization(lmCustomizationBeingEdited);
            mainLM.refreshView();
            close();
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    void EnableDisableButtons() {
        if (lmCustomizationBeingEdited.getStatus().equalsIgnoreCase("pending")) {
            btnTrain.setEnabled(false);
            cmbTrain.setEnabled(false);
            btnReset.setEnabled(false);
        }
        if (lmCustomizationBeingEdited.getStatus().equalsIgnoreCase("ready")) {
            //btnTrain.setEnabled(false);
            //cmbTrain.setEnabled(false);
            //btnReset.setEnabled(false);
        }
        if (lmCustomizationBeingEdited.getStatus().equalsIgnoreCase("training")) {
            btnTrain.setEnabled(false);
            cmbTrain.setEnabled(false);
            btnReset.setEnabled(false);
        }
        if (lmCustomizationBeingEdited.getStatus().equalsIgnoreCase("upgrading")) {
            btnTrain.setEnabled(false);
            cmbTrain.setEnabled(false);
            btnReset.setEnabled(false);
        }
        if (lmCustomizationBeingEdited.getStatus().equalsIgnoreCase("failed")) {
            //btnTrain.setEnabled(false);
            //cmbTrain.setEnabled(false);
            //btnReset.setEnabled(false);
        }
        if (lmCustomizationBeingEdited.getStatus().equalsIgnoreCase("expired")) {
            btnTrain.setEnabled(false);
            cmbTrain.setEnabled(false);
            btnReset.setEnabled(false);
        }
        if (lmCustomizationBeingEdited.getStatus().equalsIgnoreCase("available")) {
            //btnTrain.setEnabled(false);
            //cmbTrain.setEnabled(false);
            //btnReset.setEnabled(false);
        }



    }
}

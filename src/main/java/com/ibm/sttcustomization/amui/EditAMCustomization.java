package com.ibm.sttcustomization.amui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.model.AMCustomizations.AMCustomization;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomizationsReply;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.utils.Utils;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

//@Push
//@Push(transport = Transport.WEBSOCKET_XHR)
public class EditAMCustomization extends Dialog {

    private Checkbox checkboxForce;
    private Checkbox checkboxClean;
    private Grid<LMCustomization> gridCustomizationsSuitableForTraining;
    private Checkbox checkboxUseCustomLanguageModel;

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
                sRet = restClient.recognizeAM(buffer.getInputStream(), bCustomModel ? customization.getCustomization_id() : null,
                        customization.getBase_model_name(), sFileName);
            } catch (GenericException e) {
                Utils.displayErrorMessage(e);
            }

        }
    }

    private final RestClient restClient;
    private final boolean bCustomizationIsNew;
    private final MainAM mainAM;
    private final AMCustomization customization;
    private final AMCustomization customizationBeingEdited;
    private TextArea txtTestResultsWithCustomization;
    private TextArea txtTestResultsWithBaseModel;
    private Button btnTrain;
    private Button btnReset;
    private Div divTest;
    private Div divTextComparison;


    private void addFormField(String label, FormLayout layoutWithBinder, String value, Binder<AMCustomization> binder,
                              String errorMessageForBinder, ValueProvider<AMCustomization, String> getter, Setter<AMCustomization, String> setter) {
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

    private void addFormField(String label, FormLayout layoutWithBinder, LocalDateTime value, Binder<AMCustomization> binder,
                              String errorMessageForBinder, ValueProvider<AMCustomization, LocalDateTime> getter, Setter<AMCustomization, LocalDateTime> setter) {

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

    List<LMCustomization> queryLMCustomizationsSuitableForTraining()  {
        List<LMCustomization> result = new ArrayList<>();

        LMCustomizationsReply lmCustomizationsReply = null;
        try {
            lmCustomizationsReply = restClient.queryLMCustomizations(null);
            ArrayList<LMCustomization> lmCustomizations = lmCustomizationsReply.getCustomizations();

            for (LMCustomization lmc : lmCustomizations) {
                if (!lmc.getStatus().equalsIgnoreCase("available"))
                    continue;

                ArrayList<String> versions = lmc.getVersions();
                ArrayList<String> thiscustomizationversions = customization.getVersions();

                String lastVersion = versions.get(versions.size() -1);
                String lastVersionThisCustomization = thiscustomizationversions.get(thiscustomizationversions.size() -1);

                if (!lastVersion.equals(lastVersionThisCustomization))
                    continue;

                result.add(lmc);
            }

        } catch (GenericException e) {
            e.printStackTrace();
        }
        return result;
    }

    EditAMCustomization(boolean bCustomizationIsNew, MainAM mainLM, AMCustomization customization, RestClient restClient) {
        this.bCustomizationIsNew = bCustomizationIsNew;
        this.mainAM = mainLM;
        this.customization = customization;
        this.restClient = restClient;

        customizationBeingEdited = new AMCustomization(customization);

        if (bCustomizationIsNew)
            initNewModelEditing();
        else
            initExistingModelEditing();
    }

    private void initNewModelEditing() {
        String sConfirmButtonName = "Create";
        add(new H2("Create AM Customization"));

        //setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setWidth("900px");
        Binder<AMCustomization> binder = new Binder<>();

        addFormField("Name", layoutWithBinder, customizationBeingEdited.getName(), binder,
                "Please enter Name", AMCustomization::getName, AMCustomization::setName);

//        addFormField("Base Model Name", layoutWithBinder, lmCustomizationBeingEdited.getBase_model_name(), binder,
//                "Please enter Base Model Name", LMCustomization::getBase_model_name, LMCustomization::setBase_model_name);
        ComboBox<String> cmbBaseModels = new ComboBox<String>();

        SortedSet<String> modelsAllowingCustomization = mainAM.getModelsAllowingCustomization();
        cmbBaseModels.setItems(modelsAllowingCustomization);
        String sDefaultModel = modelsAllowingCustomization.first();
        for (String smodel: modelsAllowingCustomization) {
            if (smodel.equalsIgnoreCase("en-US_BroadbandModel"))
                sDefaultModel = smodel;
        }
        cmbBaseModels.setValue(sDefaultModel);

        cmbBaseModels.addValueChangeListener(e -> {
            String value = e.getValue();
            customizationBeingEdited.setBase_model_name(value);
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

//        TextField txtDialect = new TextField();
//        txtDialect.addValueChangeListener(e -> {
//            String value = e.getValue();
//            //lmCustomizationBeingEdited.setDialect(value);
//        });
//        layoutWithBinder.add();
//        layoutWithBinder.addFormItem(txtDialect, "Dialect");
//        // optional
//        addFormField("Description", layoutWithBinder, lmCustomizationBeingEdited.getDescription(), binder,
//                "Please enter Description", LMCustomization::getDescription, LMCustomization::setDescription);
        TextField txtDescription = new TextField();
        txtDescription.addValueChangeListener(e -> {
            String value = e.getValue();
            customizationBeingEdited.setDescription(value);
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
            if (binder.writeBeanIfValid(customizationBeingEdited)) {
                //lmCustomizationBeingEdited.setDialect(txtDialect.getValue());
                customizationBeingEdited.setDescription(txtDescription.getValue());
                customizationBeingEdited.setBase_model_name(cmbBaseModels.getValue());
                saveNewModel();
            } else {
                BinderValidationStatus<AMCustomization> validate = binder.validate();
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
        add(new H2("AM Customization"));

        //setCloseOnEsc(false);
        setCloseOnOutsideClick(false);


        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.setWidth("500px");
        //    layoutWithBinder.setHeight("1200px");
        Binder<AMCustomization> binder = new Binder<>();

        // all read-only
        addFormField("Id", layoutWithBinder, customizationBeingEdited.getCustomization_id(), binder,
                "Please enter Customization Id", AMCustomization::getCustomization_id, null);

        addFormField("Name", layoutWithBinder, customizationBeingEdited.getName(), binder,
                "Please enter Name", AMCustomization::getName, null);

        addFormField("Owner", layoutWithBinder, customizationBeingEdited.getOwner(), binder,
                "Please enter Owner", AMCustomization::getOwner, null);

        addFormField("Base Model Name", layoutWithBinder, customizationBeingEdited.getBase_model_name(), binder,
                "Please enter Base Model Name", AMCustomization::getBase_model_name, null);

//        addFormField("Dialect", layoutWithBinder, lmCustomizationBeingEdited.getDialect(), binder,
//                "Please enter Dialect", LMCustomization::getDialect, null);

        addFormField("Date Created", layoutWithBinder, customizationBeingEdited.getCreatedLocalDate(), binder,
                "Please enter Description", AMCustomization::getCreatedLocalDate, null);

        addFormField("Versions", layoutWithBinder, customizationBeingEdited.getVersionsAsString(), binder,
                "Please enter versions", AMCustomization::getVersionsAsString, null);

        addFormField("Description", layoutWithBinder, customizationBeingEdited.getDescription(), binder,
                "Please enter Description", AMCustomization::getDescription, null);

        //        int progress = 0;
        addFormField("Language", layoutWithBinder, customizationBeingEdited.getLanguage(), binder,
                "Please enter Language", AMCustomization::getLanguage, null);

        addFormField("Status", layoutWithBinder, customizationBeingEdited.getStatus(), binder,
                "Please enter Status", AMCustomization::getStatus, null);

        Label infoLabel = new Label();

        btnTrain = new Button("Train");
        btnTrain.addClickListener(event -> {
            ComponentEventListener yeslistener = (ComponentEventListener) event1 -> trainModel();
            ComponentEventListener nolistener = (ComponentEventListener) event12 -> { };
            ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
            getUI().ifPresent(ui -> ui.add(dialog));
            dialog.open(null, "You're about to start training of the model. The training may take a long time, mostly depending on the size of the files used for training");
        });

        checkboxForce = new Checkbox();
        checkboxForce.setLabel("Force");

        checkboxClean = new Checkbox();
        checkboxClean.setLabel("Clean");

        checkboxUseCustomLanguageModel = new Checkbox();
        checkboxUseCustomLanguageModel.setLabel("Use Custom Language Model");
        checkboxUseCustomLanguageModel.addValueChangeListener(event -> {
            gridCustomizationsSuitableForTraining.setVisible(event.getValue());
        });
        List<LMCustomization> lmCustomizationsSuitableForTraining = queryLMCustomizationsSuitableForTraining();
        gridCustomizationsSuitableForTraining = new Grid<>(LMCustomization.class);
        gridCustomizationsSuitableForTraining.setItems(lmCustomizationsSuitableForTraining);
        gridCustomizationsSuitableForTraining.setHeight("150px");
        gridCustomizationsSuitableForTraining.setVisible(false);

        gridCustomizationsSuitableForTraining.setMultiSort(true);

        // show only: guid, created, descroption name, status
        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("owner"));
        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("base_model_name"));
        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("dialect"));
        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("versions"));
        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("progress"));
        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("status"));
        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("language"));
        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("versionsAsString"));
        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("createdLocalDate"));
        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("created"));
//        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("createdLocalDate"));
//        gridCustomizationsSuitableForTraining.removeColumn(gridCustomizationsSuitableForTraining.getColumnByKey("language"));

        gridCustomizationsSuitableForTraining.getColumnByKey("customization_id").setResizable(true).setSortable(true).setWidth("110px").
                setComparator(Comparator.comparing(LMCustomization::getCustomization_id));
        gridCustomizationsSuitableForTraining.getColumnByKey("name").setResizable(true).setSortable(true).setWidth("110px").
                setComparator(Comparator.comparing(LMCustomization::getName));
        gridCustomizationsSuitableForTraining.getColumnByKey("description").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(LMCustomization::getStatus));
        LocalDateTimeRenderer localDateRenderer = new LocalDateTimeRenderer<>(LMCustomization::getCreatedLocalDate);
        Grid.Column<LMCustomization> columnCreated = gridCustomizationsSuitableForTraining.addColumn(localDateRenderer, "dd/MM/yyyy");
        columnCreated.setResizable(true).setSortable(true).setHeader("Created").
                setComparator(Comparator.comparing(LMCustomization::getCreatedLocalDate));


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
                String sRetCustomized = restClient.recognizeAM(buffer.getInputStream(), customization.getCustomization_id(), customization.getBase_model_name(), event.getFileName());
                sRetCustomized = Utils.extractTranscript(sRetCustomized);
                txtTestResultsWithCustomization.setValue(sRetCustomized);
                String sRetUncustomized = restClient.recognizeAM(buffer.getInputStream(), null, customization.getBase_model_name(), event.getFileName());
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
            if (binder.writeBeanIfValid(customizationBeingEdited)) {
                ComponentEventListener yeslistener = (ComponentEventListener) event13 -> resetModel();
                ComponentEventListener nolistener = (ComponentEventListener) event14 -> { };
                ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
                getUI().ifPresent(ui -> ui.add(dialog));

                dialog.open(null, "Are you sure you want to reset this customization?");
            } else {
                BinderValidationStatus<AMCustomization> validate = binder.validate();
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

        HorizontalLayout hlTrain = new HorizontalLayout(btnTrain, checkboxForce, checkboxClean, checkboxUseCustomLanguageModel, placeHolder);


        VerticalLayout vlMainLeft = new VerticalLayout(layoutWithBinder, hlTrain, gridCustomizationsSuitableForTraining, actions);
        VerticalLayout vlMainRight = new VerticalLayout(divTest);
        vlMainRight.setWidth("700px");

        HorizontalLayout hlMain = null;
        if (customizationBeingEdited.getStatus().equalsIgnoreCase("available")) {
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
            restClient.resetAMCustomization(customizationBeingEdited);
            mainAM.refreshView();
            close();
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    private void trainModel() {
        try {
            boolean bForce = checkboxForce.getValue();
            boolean bClean = checkboxClean.getValue();
            String custom_language_model_id = null;
            if (checkboxUseCustomLanguageModel.getValue() && !gridCustomizationsSuitableForTraining.getSelectedItems().isEmpty()) {
                LMCustomization lmCustomizationSelectyed = gridCustomizationsSuitableForTraining.getSelectedItems().iterator().next();
                custom_language_model_id = lmCustomizationSelectyed.getCustomization_id();
            }
            restClient.trainAMCustomization(customizationBeingEdited, bForce, bClean, custom_language_model_id);
            mainAM.refreshView();
            close();
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    private void deleteModel() {
        try {
            restClient.deleteAMCustomization(customizationBeingEdited);
            mainAM.refreshView();
            close();
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }


    private void saveNewModel() {
        try {
            restClient.createAMCustomization(customizationBeingEdited);
            mainAM.refreshView();
            close();
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    void EnableDisableButtons() {
        if (customizationBeingEdited.getStatus().equalsIgnoreCase("pending")) {
            btnTrain.setEnabled(false);
            btnReset.setEnabled(false);
            checkboxClean.setEnabled(false);
            checkboxForce.setEnabled(false);
            checkboxUseCustomLanguageModel.setEnabled(false);
        }
        if (customizationBeingEdited.getStatus().equalsIgnoreCase("ready")) {
            //btnTrain.setEnabled(false);
            //cmbTrain.setEnabled(false);
            //btnReset.setEnabled(false);
            //checkboxClean.setEnabled(false);
            //checkboxForce.setEnabled(false);
            //checkboxUseCustomLanguageModel.setEnabled(false);
        }
        if (customizationBeingEdited.getStatus().equalsIgnoreCase("training")) {
            btnTrain.setEnabled(false);
            btnReset.setEnabled(false);
            checkboxClean.setEnabled(false);
            checkboxForce.setEnabled(false);
            checkboxUseCustomLanguageModel.setEnabled(false);
        }
        if (customizationBeingEdited.getStatus().equalsIgnoreCase("upgrading")) {
            btnTrain.setEnabled(false);
            btnReset.setEnabled(false);
            checkboxClean.setEnabled(false);
            checkboxForce.setEnabled(false);
            checkboxUseCustomLanguageModel.setEnabled(false);
        }
        if (customizationBeingEdited.getStatus().equalsIgnoreCase("failed")) {
            //btnTrain.setEnabled(false);
            //cmbTrain.setEnabled(false);
            //btnReset.setEnabled(false);
            //checkboxClean.setEnabled(false);
            //checkboxForce.setEnabled(false);
            //checkboxUseCustomLanguageModel.setEnabled(false);
        }
        if (customizationBeingEdited.getStatus().equalsIgnoreCase("expired")) {
            btnTrain.setEnabled(false);
            btnReset.setEnabled(false);
            checkboxClean.setEnabled(false);
            checkboxForce.setEnabled(false);
            checkboxUseCustomLanguageModel.setEnabled(false);
        }
        if (customizationBeingEdited.getStatus().equalsIgnoreCase("available")) {
            //btnTrain.setEnabled(false);
            //cmbTrain.setEnabled(false);
            //btnReset.setEnabled(false);
            //checkboxClean.setEnabled(false);
            //checkboxForce.setEnabled(false);
            //checkboxUseCustomLanguageModel.setEnabled(false);
        }



    }
}

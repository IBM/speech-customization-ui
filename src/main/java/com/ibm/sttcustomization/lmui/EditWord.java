package com.ibm.sttcustomization.lmui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.lmui.WordsTab;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.words.Word;
import com.ibm.sttcustomization.utils.Utils;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ibm.sttcustomization.utils.Constants.COLOR_BE_CAREFULL_BACKGROUND;

public class EditWord  extends Dialog {

    private final RestClient restClient;

    public static class SoundsLikeItem {
        private static int COUNT;
        private int runtimeId;
        private String soundsLike;

        public SoundsLikeItem(String soundsLike) {
            runtimeId = COUNT++;
            this.soundsLike = soundsLike;
        }

        public String getSoundsLike() {
            return soundsLike;
        }

        public void setSoundsLike(String soundsLike) {
            this.soundsLike = soundsLike;
        }
    }

    private final Word word;
    private final WordsTab wordsTab;
    private final LMCustomization lmcustomization;
    private final Word wordBeingEdited;
    private final ArrayList<SoundsLikeItem> alSoundLikesEdited ;

    public EditWord(Word word, boolean bTheWordIsNew, WordsTab wordsTab, LMCustomization lmCustomization, RestClient restClient) {
        this.word = word;
        this.wordsTab = wordsTab;
        this.lmcustomization = lmCustomization;
        this.restClient = restClient;

        wordBeingEdited = new Word(word);

        String sConfirmButtonName = "Create";
        //setId("EditWord");
        if (bTheWordIsNew)
            add(new H2("Create Word"));
        else {
            add(new H2("Edit Word"));
            sConfirmButtonName = "Save";
        }

       // setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        alSoundLikesEdited = new ArrayList<>();
        for (String s: wordBeingEdited.getSounds_like()) {
            alSoundLikesEdited.add(new SoundsLikeItem(s));
        }

        Grid<SoundsLikeItem> grid = new Grid<>();
        grid.setItems(alSoundLikesEdited);

        grid.addColumn(SoundsLikeItem::getSoundsLike).setHeader("Sounds Like");

        grid.addColumn(new NativeButtonRenderer<>("Remove", clickedItem -> {
            alSoundLikesEdited.remove(clickedItem);
            grid.setItems(alSoundLikesEdited);
        }));

        FormLayout layoutWithBinder = new FormLayout();
        Binder<Word> binder = new Binder<>();

        TextField txtFieldWord = new TextField();
        txtFieldWord.setValueChangeMode(ValueChangeMode.EAGER);
        txtFieldWord.setValue(wordBeingEdited.getWord());

        TextField txtFieldDisplayAs = new TextField();
        txtFieldDisplayAs.setValueChangeMode(ValueChangeMode.EAGER);
        txtFieldDisplayAs.setValue(wordBeingEdited.getDisplay_as());


        Label infoLabel = new Label();

        Button btnSave = new Button(sConfirmButtonName);

//        Button btnDelete = new Button("Delete");
//        btnDelete.getStyle().set("background-color", COLOR_BE_CAREFULL_BACKGROUND);
//        btnDelete.addClickListener(event -> {
//                ComponentEventListener yeslistener = (ComponentEventListener) event1 -> deleteWord();
//                ComponentEventListener nolistener = (ComponentEventListener) event12 -> { };
//                ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
//                getUI().ifPresent(ui -> ui.add(dialog));
//
//                dialog.open(null, "Are you sure you want to delete this word?");
//            }
//        );

        Button btnCancel = new Button("Cancel");

        layoutWithBinder.addFormItem(txtFieldWord, "Word");
        layoutWithBinder.addFormItem(txtFieldDisplayAs, "Display As");
//        layoutWithBinder.addFormItem(grid, "Sounds Like");

        // Button bar
        HorizontalLayout actions = new HorizontalLayout();
        //actions.add(btnDelete, btnSave, btnCancel);
        actions.add(btnSave, btnCancel);
        btnSave.getStyle().set("marginRight", "10px");

        // Word and Display As are required fields
        txtFieldWord.setRequiredIndicatorVisible(true);
        txtFieldDisplayAs.setRequiredIndicatorVisible(true);

        binder.forField(txtFieldWord)
                .withValidator(new StringLengthValidator("Please enter Word", 1, null))
                .bind(Word::getWord, Word::setWord);

        binder.forField(txtFieldDisplayAs)
                .withValidator(new StringLengthValidator("Please enter Display As", 1, null))
                .bind(Word::getDisplay_as, Word::setDisplay_as);

        btnSave.addClickListener(event -> {
            if (binder.writeBeanIfValid(wordBeingEdited)) {
                saveWord();
            } else {
                BinderValidationStatus<Word> validate = binder.validate();
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

        TextField txtSoundLike = new TextField();
        Button addSoundLike = new Button("Add Sounds Like");
        addSoundLike.addClickListener(event -> {
            alSoundLikesEdited.add(new SoundsLikeItem(txtSoundLike.getValue()));
            grid.setItems(alSoundLikesEdited);
        });
        HorizontalLayout hlAddSoundsLike = new HorizontalLayout(txtSoundLike, addSoundLike );

        add(layoutWithBinder);
        add(grid, hlAddSoundsLike);
        add(actions);

    }

    private void deleteWord() {
        try {
            restClient.deleteWord(lmcustomization.getCustomization_id(), wordBeingEdited.getWord());
            wordsTab.getCustomizationTabs().reloadCurrentCustomization();
            close();
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    private void saveWord() {
        try {
            ArrayList<String> arrSoundsLike = new ArrayList<>();
            for (SoundsLikeItem soundslikeitem: alSoundLikesEdited) {
                arrSoundsLike.add(soundslikeitem.getSoundsLike());
            }
            wordBeingEdited.setSounds_like(arrSoundsLike);

            String sRet = restClient.createOrupdateWord(wordBeingEdited, lmcustomization.getCustomization_id());
            wordsTab.getCustomizationTabs().reloadCurrentCustomization();
            close();
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }
}

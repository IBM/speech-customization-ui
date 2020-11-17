package com.ibm.ttscustomization.ttsui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.utils.Utils;
import com.ibm.ttscustomization.TTSUtils;
import com.ibm.ttscustomization.ttsmodel.RestClientTTS;
import com.ibm.ttscustomization.ttsmodel.customizations.TTSCustomization;
import com.ibm.ttscustomization.ttsmodel.words.Word;
import com.ibm.ttscustomization.ttsui.words.ipa.IPAKeyboard;
import com.ibm.ttscustomization.ttsui.words.ipa.IPARow;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.ArrayList;

public class EditWord extends Dialog {
    private Word word;
    private TextField txtFieldPartOfSpeech;
    private final TextField txtFieldWord;
    private final TextField txtFieldTranslation;
    private RestClientTTS restClient;
    TTSContentTab customizationTab;
    TTSCustomization customization;
    private final boolean bBrandNewWord;
    private ArrayList<IPARow> alIPAItems;
    private final VerticalLayout layoutMain;
    private TextField textFieldIPA;

    public void addHL(VerticalLayout layoutMain, TextField textField, String sLabel, Button btn) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("1000px");
        textField.setWidth("700px");
        Label lblWord = new Label(sLabel);
        lblWord.setWidth("150px");
        horizontalLayout.add(lblWord, textField);
        if (btn != null)
            horizontalLayout.add(btn);
        layoutMain.add(horizontalLayout);
    }

    public EditWord(Word word, RestClientTTS restClient, TTSContentTab customizationTab, TTSCustomization customization, boolean bBrandNewWord) {
        this.word = word;
        this.restClient = restClient;
        this.customizationTab = customizationTab;
        this.customization = customization;
        this.bBrandNewWord = bBrandNewWord;

        add(new H2("Edit Word"));
        layoutMain = new VerticalLayout();
        layoutMain.setMargin(true);
        layoutMain.setSpacing(false);

        FormLayout layoutWordProperties = new FormLayout();
        layoutWordProperties.setWidth("1100px");
        layoutMain.add(layoutWordProperties);

        txtFieldWord = new TextField();
        txtFieldWord.setValue(word.getWord());
        if (!bBrandNewWord)
            txtFieldWord.setReadOnly(true);
        addHL(layoutMain, txtFieldWord, "Word", null);

        if (customization.getLanguage().equalsIgnoreCase("jp-JP")) {
            txtFieldPartOfSpeech = new TextField();
            if (word.getPart_of_speech() != null)
                txtFieldPartOfSpeech.setValue(word.getPart_of_speech());
            addHL(layoutMain, txtFieldPartOfSpeech, "Part of Speech", null);
        }

        txtFieldTranslation = new TextField();
        txtFieldTranslation.setValue(word.getTranslation());
        Button btnPlayTranslation = new Button();
        btnPlayTranslation.setIcon(VaadinIcon.PLAY.create());
        btnPlayTranslation.setWidth("50px");
        btnPlayTranslation.addClickListener(e -> {
            String textValue = txtFieldTranslation.getValue();
            TTSUtils.playWord(textValue, restClient, customization);
        });
        addHL(layoutMain, txtFieldTranslation, "Translation", btnPlayTranslation);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button save = new Button("save");
        save.addClickListener(event -> this.saveWord());
        buttonLayout.add(save);

        Button cancel = new Button("cancel");
        cancel.addClickListener(event -> this.close());

        buttonLayout.add(cancel);

        if (!bBrandNewWord) {
            Button deleteButton = new Button("Delete", event -> {
                ComponentEventListener yeslistener = (ComponentEventListener) event1 -> deleteWord();
                ComponentEventListener nolistener = (ComponentEventListener) event12 -> { };
                ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
                getUI().ifPresent(ui -> ui.add(dialog));
                dialog.open(null, "Are you sure you want to delete this word?");
            });

            buttonLayout.add(deleteButton);
        }

        layoutMain.add(buttonLayout);

        if (isIPAExcelAvailable()) {

            layoutMain.add(new Div());
            layoutMain.add(new H3("IPA Keyboard:"));
            textFieldIPA = new TextField();

            try {
                Document document = TTSUtils.convertStringToDocument(word.getTranslation());
                if (document != null) {
                    String alphabet = document.getDocumentElement().getAttribute("alphabet");
                    if (alphabet.equalsIgnoreCase("ipa")) {
                        String ipa = document.getDocumentElement().getAttribute("ph");
                        textFieldIPA.setValue(ipa);
                    }
                }
            }
            catch (Exception e) {
            }

            IPAKeyboard ipaKeyboard = new IPAKeyboard(customization, textFieldIPA, txtFieldTranslation);
            ipaKeyboard.setWidth("100%");
            layoutMain.add(ipaKeyboard);

            Div divIPATextAndButtons = new Div();
            divIPATextAndButtons.setSizeFull();
            divIPATextAndButtons.setWidth("100%");

            textFieldIPA.setWidth("300px");
            divIPATextAndButtons.add(textFieldIPA);

            Button btnPlayIPA = new Button();
            btnPlayIPA.setIcon(VaadinIcon.PLAY.create());
            btnPlayIPA.addClickListener(e -> {
                String textValue = textFieldIPA.getValue();
                String translation = "<phoneme alphabet=\"ipa\" ph=\"" + textValue + "\"></phoneme>";
                //playAudio(translation);
                TTSUtils.playWord(translation, restClient, customization);
            });
            divIPATextAndButtons.add(btnPlayIPA);

            Button btnUseIPAInTranslation = new Button("Use in Translation");
            btnUseIPAInTranslation.setIcon(VaadinIcon.ANGLE_DOUBLE_UP.create());
            btnUseIPAInTranslation.addClickListener(e -> {
                    String textValue = textFieldIPA.getValue();
                    String translation = "<phoneme alphabet=\"ipa\" ph=\"" + textValue + "\"></phoneme>";
                    txtFieldTranslation.setValue(translation);
            });
            divIPATextAndButtons.add(btnUseIPAInTranslation);


            layoutMain.add(divIPATextAndButtons);
        }

        add(layoutMain);

        setWidth("90%");
        setHeight("90%");
    }


    boolean isIPAExcelAvailable() {
        String language = customization.getLanguage();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/ipa_" + language + ".xls");
        return (inputStream != null);
    }

    private void saveWord() {
        try {
            JSONObject jsoBody = new JSONObject();
            JSONArray jsaWords = new JSONArray();
            JSONObject jsoWord = new JSONObject();

            jsoWord.put("word", txtFieldWord.getValue().trim());
            jsoWord.put("translation", txtFieldTranslation.getValue().trim());
            if (txtFieldPartOfSpeech != null)
                jsoWord.put("part_of_speech", txtFieldPartOfSpeech.getValue().trim());

            jsaWords.put(jsoWord);
            jsoBody.put("words", jsaWords);

            String sRet = restClient.updateWord(jsoBody, customization.getCustomization_id());
            JSONObject jsoRet = new JSONObject(sRet);

            word.setWord(txtFieldWord.getValue().trim());
            word.setTranslation(txtFieldTranslation.getValue().trim());
            if (txtFieldPartOfSpeech != null)
                word.setPart_of_speech(txtFieldPartOfSpeech.getValue().trim());

            customizationTab.reloadGrid("");
            customizationTab.getCustomizationTabs().reloadCurrentCustomization();

            this.close();
        }
        catch (GenericException genericException) {
            Utils.displayErrorMessage(genericException);
        }
    }

    private void deleteWord() {
        try {
            String sRet = restClient.deleteWord(customization.getCustomization_id(), word.getWord());
            customizationTab.reloadGrid("");
            customizationTab.getCustomizationTabs().reloadCurrentCustomization();
            this.close();
        }
        catch (GenericException genericException) {
            Utils.displayErrorMessage(genericException);
        }
    }



}
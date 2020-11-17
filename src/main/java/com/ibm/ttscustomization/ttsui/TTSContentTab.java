package com.ibm.ttscustomization.ttsui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.utils.Utils;
import com.ibm.ttscustomization.TTSUtils;
import com.ibm.ttscustomization.ttsmodel.RestClientTTS;
import com.ibm.ttscustomization.ttsmodel.customizations.TTSCustomization;
import com.ibm.ttscustomization.ttsmodel.words.Word;
import com.ibm.ttscustomization.ttsmodel.words.WordRepository;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.ibm.sttcustomization.utils.Constants.COLOR_BE_CAREFULL_FOREGROUND;


// put for single word add/edit

public class TTSContentTab extends VerticalLayout {
    private final TTSCustomizationTabs customizationTabs;
    private TTSCustomization m_customization;
    private final WordRepository wordRepository;
    private final Grid<Word> grid = new Grid<>(Word.class);
    private final ComboBox<String> cmbFilter;
    private final TextField textFieldFilter;
    private final Button btnNewWord;
    private RestClientTTS restClient;
    private byte[] byteArrayCorpusContent = null;
//    private Audio m_audioContainer;

    public TTSCustomizationTabs getCustomizationTabs() {
        return customizationTabs;
    }

//    public InputStream getAudioStream(Word w) {
//        try {
//            String sVoice = Utils.getVoiceRepository().getAnyVoiceForLanguage(m_customization.getLanguage()).getName();
//            JSONObject jso = new JSONObject();
//            jso.put("text", w.getTranslation());
//            byte[] ab = restClient.queryAudio(jso, m_customization.getCustomization_id(), sVoice);
//            if (true)
//                return new ByteArrayInputStream(ab);
//            return null;
//        } catch (GenericException genericException) {
//            Utils.displayErrorMessage(genericException);
//        }
//        return null;
//    }

    public TTSContentTab(TTSCustomizationTabs customizationTabs) {
        this.customizationTabs = customizationTabs;
        this.wordRepository = new WordRepository();

        this.textFieldFilter = new TextField();
        this.btnNewWord = new Button("New Word");
        btnNewWord.addClickListener(e -> {
            Word w = new Word();
            EditWord editWord = new EditWord(w, restClient, this, m_customization, true);
            getUI().get().add(editWord);
            editWord.open();
        });

        this.cmbFilter = new ComboBox<>();
        SortedSet<String> ssFilters = new TreeSet<>();
        ssFilters.add("Filter by Word");
        //cmbFilter.setTextInputAllowed(false);
        cmbFilter.setItems(ssFilters); //configure the possible options (usually, this is not a manually defined list...)
        cmbFilter.setValue("Filter by Word"); //load the value of given data object
        cmbFilter.addValueChangeListener(e -> textFieldFilter.setValue(""));

        textFieldFilter.addValueChangeListener(e -> reloadGrid(e.getValue()));

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setHeight("100%");
        grid.setWidth("100%");

        grid.removeColumn(grid.getColumnByKey("runtimeid"));
        grid.removeColumn(grid.getColumnByKey("customization_id"));
        grid.removeColumn(grid.getColumnByKey("word"));
        grid.removeColumn(grid.getColumnByKey("part_of_speech"));
        grid.removeColumn(grid.getColumnByKey("translation"));
        grid.removeColumn(grid.getColumnByKey("markedForDelection"));
        grid.removeColumn(grid.getColumnByKey("edited"));

        grid.addColumn(Word::getWord).setHeader("Word").setWidth("70px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Word::getWord));
        grid.addColumn(Word::getTranslation).setHeader("Translation").setWidth("210px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Word::getTranslation));
        grid.addColumn(Word::getPart_of_speech).setHeader("Part of Speech").setWidth("70px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Word::getPart_of_speech));

        grid.addColumn(new ComponentRenderer<>(word -> {
            Icon iconPlay = VaadinIcon.PLAY.create();
            iconPlay.setColor("darkgray");
            Button btnPlay = new Button("", event -> {
//                StreamResource resource = new StreamResource("audio.wav",
//                        () -> TTSUtils.getAudioStream(word, restClient, m_customization));
//                Element element = new Element("audio");
//                element.setAttribute("type", "audio/wav");
//                element.setAttribute("autoplay", true);
//                element.getStyle().set("display", "block");
//                element.setAttribute("src", resource);
//                UI.getCurrent().getElement().appendChild(element);
                TTSUtils.playWord(word.getTranslation(), restClient, m_customization);
            });

            btnPlay.setWidth("20px");
            btnPlay.setHeight("25px");
            btnPlay.setIcon(iconPlay);

            Icon iconDetails = VaadinIcon.BULLETS.create();
            iconDetails.setColor("darkgray");
            Button btnDetails = new Button("", event -> {
                EditWord editWord = new EditWord(word, restClient, this, m_customization, false);
                getUI().get().add(editWord);
                editWord.open();
            });
            btnDetails.setWidth("20px");btnDetails.setHeight("25px");
            btnDetails.setIcon(iconDetails);

            Icon iconDelete = VaadinIcon.FILE_REMOVE.create();
            iconDelete.setColor(COLOR_BE_CAREFULL_FOREGROUND);
            Button btnDelete = new Button("", event -> {
                ComponentEventListener yeslistener = (ComponentEventListener) event1 -> deleteWord(word);
                ComponentEventListener nolistener = (ComponentEventListener) event12 -> { };
                ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
                getUI().ifPresent(ui -> ui.add(dialog));
                dialog.open(null, "Are you sure you want to delete this customization?");
            });
            btnDelete.setWidth("20px");btnDelete.setHeight("25px");
            btnDelete.setIcon(iconDelete);

            //Div divButtons = new Div(btnDetails, btnRefresh, btnDelete);
            Div divButtons = new Div(btnPlay, btnDetails, btnDelete);
            divButtons.setHeight("35px");
            //buttons.setSpacing(false);
            return divButtons;
        })).setHeader("");


        add(grid);
        HorizontalLayout hl = new HorizontalLayout(cmbFilter, textFieldFilter, btnNewWord);
        add(hl);
    }

    private void deleteWord(Word word) {
        try {
            String sRet = restClient.deleteWord(m_customization.getCustomization_id(), word.getWord());
            //customizationTab.wordDeletedSuccessfully(word);
            //this.close();
            // !!!!!!!!!!!!!!! refresh grid
        }
        catch (GenericException genericException) {
            Utils.displayErrorMessage(genericException);
        }
    }

    public void editCustomization(TTSCustomization customization, RestClientTTS restClient) {
        this.m_customization = customization;
        //this.m_audioContainer = null;
        btnNewWord.setVisible(true);

        this.restClient = restClient;
        try {
            customization.loadWords(restClient);
            wordRepository.setWords(new ArrayList<>(customization.getWordsList()));
            grid.setItems(customization.getWordsList());
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    public void reloadGrid(String filterText) {
        String filterBy = cmbFilter.getValue();
        if (filterBy == null)
            filterBy = "Filter by Name";

        if (filterBy.equalsIgnoreCase("Filter by Name")) {
            listByName(filterText);
        }
//        if (filterBy.equalsIgnoreCase("Filter by Display As")) {
//            listByDisplayAs(filterText);
//        }
//        if (filterBy.equalsIgnoreCase("Filter by Source")) {
//            listBySource(filterText);
//        }

    }

    void listByName(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid.setItems(Utils.toList(wordRepository.findAll()));
        else
            grid.setItems(wordRepository.findByNameContains(filterText));
    }


}

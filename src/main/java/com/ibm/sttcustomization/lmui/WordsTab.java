package com.ibm.sttcustomization.lmui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.words.Word;
import com.ibm.sttcustomization.model.words.WordRepository;
import com.ibm.sttcustomization.utils.Utils;
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

import java.util.*;

import static com.ibm.sttcustomization.utils.Constants.COLOR_BE_CAREFULL_FOREGROUND;


// put for single word add/edit

public class WordsTab extends VerticalLayout {
    private final Button btnNewListOfWords;
    private final CustomizationTabs customizationTabs;
    private LMCustomization lmCustomization;
    private final WordRepository wordRepository;
    private final Grid<Word> grid = new Grid<>(Word.class);
    private final ComboBox<String> cmbFilter;
    private final TextField textFieldFilter;
    private final Button btnNewWord;
    private RestClient restClient;

    public CustomizationTabs getCustomizationTabs() {
        return customizationTabs;
    }

    public WordsTab(CustomizationTabs customizationTabs) {
        this.customizationTabs = customizationTabs;
        this.wordRepository = new WordRepository();

        this.textFieldFilter = new TextField();
        this.btnNewWord = new Button("New Word");
        btnNewWord.addClickListener(e -> {
            Word w = new Word();
            EditWord editWord = new EditWord(w, true, this, lmCustomization, restClient);
            getUI().get().add(editWord);
            editWord.open();
        });

        this.btnNewListOfWords = new Button("New List of Words");
        btnNewListOfWords.addClickListener(e -> {
            NewListOfWords editWord = new NewListOfWords(this, lmCustomization, restClient);
            getUI().get().add(editWord);
            editWord.open();
        });

        this.cmbFilter = new ComboBox<>();
        SortedSet<String> ssFilters = new TreeSet<>();
        ssFilters.add("Filter by Word");
        ssFilters.add("Filter by Display As");
        ssFilters.add("Filter by Source");
        //cmbFilter.setTextInputAllowed(false);
        cmbFilter.setItems(ssFilters); //configure the possible options (usually, this is not a manually defined list...)
        cmbFilter.setValue("Filter by Word"); //load the value of given data object
        cmbFilter.addValueChangeListener(e -> textFieldFilter.setValue(""));

        textFieldFilter.addValueChangeListener(e -> reloadGrid(e.getValue()));

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setHeight("100%");
        grid.setWidth("100%");

        grid.removeColumn(grid.getColumnByKey("word"));
        grid.removeColumn(grid.getColumnByKey("source"));
        grid.removeColumn(grid.getColumnByKey("sounds_like"));
        grid.removeColumn(grid.getColumnByKey("display_as"));
        grid.removeColumn(grid.getColumnByKey("count"));

        grid.addColumn(Word::getWord).setHeader("Word").setWidth("100px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Word::getWord));
        grid.addColumn(Word::getDisplay_as).setHeader("Display As").setWidth("130px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Word::getDisplay_as));
        grid.addColumn(Word::getCount).setHeader("Count").setWidth("100px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Word::getCount));
        grid.addColumn(Word::getSounds_like).setHeader("Sounds Like").setWidth("130px").setResizable(true);
        grid.addColumn(Word::getSource).setHeader("Source").setWidth("150px").setResizable(true);


        grid.asSingleSelect().addValueChangeListener(e -> {
            //customizationTabs.editCustomization(e.getValue(), restClient);
        });

//        grid.addColumn(new NativeButtonRenderer<>("...", clickedItem -> {
//            EditWord editWord = new EditWord(clickedItem, false, this, lmCustomization, restClient);
//            getUI().get().add(editWord);
//            editWord.open();
//        }));
        grid.addColumn(new ComponentRenderer<>(word-> {
            Icon iconDetails = VaadinIcon.BULLETS.create();
            iconDetails.setColor("darkgray");
            Button btnDetails = new Button("", event -> {
                EditWord editWord = new EditWord(word, false, this, lmCustomization, restClient);
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
                dialog.open(null, "Are you sure you want to delete this word?");
            });
            btnDelete.setWidth("20px");btnDelete.setHeight("25px");
            btnDelete.setIcon(iconDelete);

            Div divButtons = new Div(btnDetails, btnDelete);
            divButtons.setHeight("35px");
            //buttons.setSpacing(false);
            return divButtons;
        })).setHeader("");


        add(grid);
        HorizontalLayout hl = new HorizontalLayout(cmbFilter, textFieldFilter, btnNewWord, btnNewListOfWords);
        add(hl);
    }

    public void editCustomization(LMCustomization lmCustomization, RestClient restClient) throws GenericException {
        this.lmCustomization = lmCustomization;
        this.restClient = restClient;
        wordRepository.loadWords(restClient, lmCustomization.getCustomization_id());

        if (wordRepository.getWords() != null)
           grid.setItems(wordRepository.getWords());
        else
            grid.setItems(new ArrayList<>());
    }

    void reloadGrid(String filterText) {
        String filterBy = cmbFilter.getValue();
        if (filterBy == null)
            filterBy = "Filter by Word";

        if (filterBy.equalsIgnoreCase("Filter by Word")) {
            listByName(filterText);
        }
        if (filterBy.equalsIgnoreCase("Filter by Display As")) {
            listByDisplayAs(filterText);
        }
        if (filterBy.equalsIgnoreCase("Filter by Source")) {
            listBySource(filterText);
        }

    }

    private void listBySource(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid.setItems(Utils.toList(wordRepository.findAll()));
        else
            grid.setItems(wordRepository.findBySourceContains(filterText));
    }

    private void listByDisplayAs(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid.setItems(Utils.toList(wordRepository.findAll()));
        else
            grid.setItems(wordRepository.findByDisplayAsContains(filterText));
    }

    void listByName(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid.setItems(Utils.toList(wordRepository.findAll()));
        else
            grid.setItems(wordRepository.findByNameContains(filterText));
    }

    private void deleteWord(Word word) {
        try {
            restClient.deleteWord(lmCustomization.getCustomization_id(), word.getWord());
            customizationTabs.reloadCurrentCustomization();
            reloadGrid(textFieldFilter.getValue());
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

}

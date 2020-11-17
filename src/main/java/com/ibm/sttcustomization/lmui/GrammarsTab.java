package com.ibm.sttcustomization.lmui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.grammars.Grammar;
import com.ibm.sttcustomization.model.grammars.GrammarRepository;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.ibm.sttcustomization.utils.Constants.COLOR_BE_CAREFULL_FOREGROUND;

public class GrammarsTab extends VerticalLayout {
    private final CustomizationTabs customizationTabs;
    private LMCustomization lmCustomization;
    private final GrammarRepository grammarRepository;
    private final Grid<Grammar> grid = new Grid<>(Grammar.class);
    private final ComboBox<String> cmbFilter;
    private final TextField textFieldFilter;
    private RestClient restClient;

    public CustomizationTabs getCustomizationTabs() {
        return customizationTabs;
    }

    public GrammarsTab(CustomizationTabs customizationTabs) {
        this.customizationTabs = customizationTabs;
        this.grammarRepository = new GrammarRepository();

        this.textFieldFilter = new TextField();
        Button btnCreateNewGrammar = new Button("New Grammar");
        btnCreateNewGrammar.addClickListener(e -> {
            Grammar g = new Grammar();
            NewGrammar newCorpus = new NewGrammar(g, true, this, lmCustomization, restClient);
            getUI().get().add(newCorpus);
            newCorpus.open();
        });
        this.cmbFilter = new ComboBox<>();
        SortedSet<String> ssFilters = new TreeSet<>();
        ssFilters.add("Filter by Name");
        ssFilters.add("Filter by Status");
        //cmbFilter.setTextInputAllowed(false);
        cmbFilter.setItems(ssFilters); //configure the possible options (usually, this is not a manually defined list...)
        cmbFilter.setValue("Filter by Name"); //load the value of given data object
        cmbFilter.addValueChangeListener(e -> textFieldFilter.setValue(""));

        textFieldFilter.addValueChangeListener(e -> reloadGrid(e.getValue()));

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setHeight("100%");
        grid.setWidth("100%");

        grid.removeColumn(grid.getColumnByKey("name"));
        grid.removeColumn(grid.getColumnByKey("OOV_words"));
        grid.removeColumn(grid.getColumnByKey("status"));

        grid.addColumn(Grammar::getName).setHeader("Name").setWidth("100px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Grammar::getName));
        grid.addColumn(Grammar::getOOV_words).setHeader("Out Of Vocabulary Words").setWidth("100px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Grammar::getOOV_words));
        grid.addColumn(Grammar::getStatus).setHeader("Status").setWidth("100px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Grammar::getStatus));

        grid.asSingleSelect().addValueChangeListener(e -> {
            //customizationTabs.editCustomization(e.getValue(), restClient);
        });

        grid.addColumn(new ComponentRenderer<>(grammar-> {
            Icon iconDelete = VaadinIcon.FILE_REMOVE.create();
            iconDelete.setColor(COLOR_BE_CAREFULL_FOREGROUND);
            Button btnDelete = new Button("", event -> {
                ComponentEventListener yeslistener = (ComponentEventListener) event1 -> deleteGrammar(grammar);
                ComponentEventListener nolistener = (ComponentEventListener) event12 -> { };
                ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
                getUI().ifPresent(ui -> ui.add(dialog));
                dialog.open(null, "Are you sure you want to delete this grammar?");
            });
            btnDelete.setWidth("20px");btnDelete.setHeight("25px");
            btnDelete.setIcon(iconDelete);

            Div divButtons = new Div(btnDelete);
            divButtons.setHeight("35px");
            //buttons.setSpacing(false);
            return divButtons;
        })).setHeader("");

        add(grid);
        HorizontalLayout hl = new HorizontalLayout(cmbFilter, textFieldFilter, btnCreateNewGrammar);
        add(hl);
    }

    public void editCustomization(LMCustomization lmCustomization, RestClient restClient) throws GenericException {
        this.restClient = restClient;
        this.lmCustomization = lmCustomization;
        grammarRepository.loadGrammars(restClient, lmCustomization.getCustomization_id());

        if (grammarRepository.getGrammars() != null)
            grid.setItems(grammarRepository.getGrammars());
        else
            grid.setItems(new ArrayList<>());

    }

    void reloadGrid(String filterText) {
        String filterBy = cmbFilter.getValue();
        if (filterBy == null)
            filterBy = "Filter by Name";

        if (filterBy.equalsIgnoreCase("Filter by Name")) {
            listByName(filterText);
        }
        if (filterBy.equalsIgnoreCase("Filter by Status")) {
            listByStatus(filterText);
        }
    }

    private void listByStatus(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid.setItems(Utils.toList(grammarRepository.findAll()));
        else
            grid.setItems(grammarRepository.findByStatusContains(filterText));
    }

    void listByName(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid.setItems(Utils.toList(grammarRepository.findAll()));
        else
            grid.setItems(grammarRepository.findByNameContains(filterText));
    }

    private void deleteGrammar(Grammar grammar) {
        try {
            restClient.deleteGrammar(lmCustomization.getCustomization_id(), grammar.getName());
            customizationTabs.reloadCurrentCustomization();
            reloadGrid(textFieldFilter.getValue());
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

}

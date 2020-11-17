package com.ibm.sttcustomization.lmui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.corpora.Corpus;
import com.ibm.sttcustomization.model.corpora.CorporaRepository;
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
import java.util.SortedSet;
import java.util.TreeSet;

import static com.ibm.sttcustomization.utils.Constants.COLOR_BE_CAREFULL_FOREGROUND;

public class CorporaTab extends VerticalLayout {
    private final CustomizationTabs customizationTabs;
    private LMCustomization lmCustomization;
    private final CorporaRepository corporaRepository;
    private final Grid<Corpus> grid = new Grid<>(Corpus.class);
    private final ComboBox<String> cmbFilter;
    private final TextField textFieldFilter;
    private final Button btnCreateNewCorpora;
    private RestClient restClient;

    public CustomizationTabs getCustomizationTabs() {
        return customizationTabs;
    }

    public CorporaTab(CustomizationTabs customizationTabs) {
        this.customizationTabs = customizationTabs;
        this.corporaRepository = new CorporaRepository();

        this.textFieldFilter = new TextField();
        this.btnCreateNewCorpora = new Button("New Corpus");
        btnCreateNewCorpora.addClickListener(e -> {
            Corpus c = new Corpus();
            NewCorpus newCorpus = new NewCorpus(c, true, this, lmCustomization, restClient);
            getUI().get().add(newCorpus);
            newCorpus.open();
        });

        this.cmbFilter = new ComboBox<>();
        SortedSet<String> ssFilters = new TreeSet<>();
        ssFilters.add("Filter by Corpus Name");
        ssFilters.add("Filter by Corpus Status");
        //cmbFilter.setTextInputAllowed(false);
        cmbFilter.setItems(ssFilters); //configure the possible options (usually, this is not a manually defined list...)
        cmbFilter.setValue("Filter by Corpus Name"); //load the value of given data object
        cmbFilter.addValueChangeListener(e -> textFieldFilter.setValue(""));

        textFieldFilter.addValueChangeListener(e -> reloadGrid(e.getValue()));

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setHeight("100%");
        grid.setWidth("100%");

        grid.removeColumn(grid.getColumnByKey("name"));
        grid.removeColumn(grid.getColumnByKey("total_words"));
        grid.removeColumn(grid.getColumnByKey("out_of_vocabulary_words"));
        grid.removeColumn(grid.getColumnByKey("status"));

        grid.addColumn(Corpus::getName).setHeader("Name").setWidth("100px").setResizable(true).setSortable(true);
        grid.addColumn(Corpus::getTotal_words).setHeader("Total Words").setWidth("100px").setResizable(true).setSortable(true);
        grid.addColumn(Corpus::getOut_of_vocabulary_words).setHeader("Out of Vocabulary Words").setWidth("100px").setResizable(true).setSortable(true);
        grid.addColumn(Corpus::getStatus).setHeader("Status").setWidth("100px").setResizable(true).setSortable(true);

        grid.asSingleSelect().addValueChangeListener(e -> {
            //customizationTabs.editCustomization(e.getValue(), restClient);
        });

//        grid.addColumn(new NativeButtonRenderer<>("Delete", clickedItem -> {
//            try {
//                restClient.deleteCorpus(lmCustomization.getCustomization_id(), clickedItem.getName());
//            } catch (GenericException e) {
//                Utils.displayErrorMessage(e);
//            }
//        }));
        grid.addColumn(new ComponentRenderer<>(corpus-> {
            Icon iconDelete = VaadinIcon.FILE_REMOVE.create();
            iconDelete.setColor("magenta");
            iconDelete.setColor(COLOR_BE_CAREFULL_FOREGROUND);
            Button btnDelete = new Button("", event -> {
                ComponentEventListener yeslistener = (ComponentEventListener) event1 -> deleteCorpus(corpus);
                ComponentEventListener nolistener = (ComponentEventListener) event12 -> { };
                ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
                getUI().ifPresent(ui -> ui.add(dialog));
                dialog.open(null, "Are you sure you want to delete this corpus?");
            });
            btnDelete.setWidth("20px");btnDelete.setHeight("25px");
            btnDelete.setIcon(iconDelete);

            Div divButtons = new Div(btnDelete);
            divButtons.setHeight("35px");
            //buttons.setSpacing(false);
            return divButtons;
        })).setHeader("");


        add(grid);
        HorizontalLayout hl = new HorizontalLayout(cmbFilter, textFieldFilter, btnCreateNewCorpora);
        add(hl);
    }

    public void editCustomization(LMCustomization lmCustomization, RestClient restClient) throws GenericException {
        this.lmCustomization = lmCustomization;
        this.restClient = restClient;
        corporaRepository.loadCorpora(restClient, lmCustomization.getCustomization_id());

        if (corporaRepository.getCorpora() != null)
            grid.setItems(corporaRepository.getCorpora());
        else
            grid.setItems(new ArrayList<>());

    }

    void reloadGrid(String filterText) {
        String filterBy = cmbFilter.getValue();
        if (filterBy == null)
            filterBy = "Filter by Corpus Name";

        if (filterBy.equalsIgnoreCase("Filter by Corpus Name")) {
            listByName(filterText);
        }
        if (filterBy.equalsIgnoreCase("Filter by Corpus Status")) {
            listByStatus(filterText);
        }

    }

    private void listByStatus(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid.setItems(Utils.toList(corporaRepository.findAll()));
        else
            grid.setItems(corporaRepository.findByStatusContains(filterText));
    }

    void listByName(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid.setItems(Utils.toList(corporaRepository.findAll()));
        else
            grid.setItems(corporaRepository.findByNameContains(filterText));
    }

    private void deleteCorpus(Corpus corpus) {
        try {
            restClient.deleteCorpus(lmCustomization.getCustomization_id(), corpus.getName());
            customizationTabs.reloadCurrentCustomization();
            reloadGrid(textFieldFilter.getValue());
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

}

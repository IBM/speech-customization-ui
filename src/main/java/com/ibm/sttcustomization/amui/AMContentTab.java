package com.ibm.sttcustomization.amui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.model.AMCustomizations.AMCustomization;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.audio.Audio;
import com.ibm.sttcustomization.model.audio.AudioRepository;
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


// put for single word add/edit

public class AMContentTab extends VerticalLayout {
    private final AMCustomizationTabs customizationTabs;
    private AMCustomization m_customization;
    private final AudioRepository audioRepository;
    private final Grid<Audio> grid = new Grid<>(Audio.class);
    private final ComboBox<String> cmbFilter;
    private final TextField textFieldFilter;
    private final Button btnNewAudio;
    private RestClient restClient;
    private final Button btnNavigateback;
    private byte[] byteArrayCorpusContent = null;
    private Audio m_audioContainer;

    public AMCustomizationTabs getCustomizationTabs() {
        return customizationTabs;
    }

    public AMContentTab(AMCustomizationTabs customizationTabs) {
        this.customizationTabs = customizationTabs;
        this.audioRepository = new AudioRepository();

        this.textFieldFilter = new TextField();
        this.btnNewAudio = new Button("New Audio");
        btnNewAudio.addClickListener(e -> {
            Audio audio = new Audio();
            NewAudio newAudio = new NewAudio(audio, m_audioContainer,true, this, m_customization, restClient);
            getUI().get().add(newAudio);
            newAudio.open();
        });

        this.cmbFilter = new ComboBox<>();
        SortedSet<String> ssFilters = new TreeSet<>();
        ssFilters.add("Filter by Name");
        //cmbFilter.setTextInputAllowed(false);
        cmbFilter.setItems(ssFilters); //configure the possible options (usually, this is not a manually defined list...)
        cmbFilter.setValue("Filter by Name"); //load the value of given data object
        cmbFilter.addValueChangeListener(e -> textFieldFilter.setValue(""));

        textFieldFilter.addValueChangeListener(e -> reloadGrid(e.getValue()));

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setHeight("100%");
        grid.setWidth("100%");

        grid.removeColumn(grid.getColumnByKey("details"));
        grid.removeColumn(grid.getColumnByKey("duration"));
        grid.removeColumn(grid.getColumnByKey("name"));
        grid.removeColumn(grid.getColumnByKey("detailsAsString"));
        grid.removeColumn(grid.getColumnByKey("status"));

        grid.addColumn(Audio::getName).setHeader("Name").setWidth("70px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Audio::getName));
        grid.addColumn(Audio::getDuration).setHeader("Duration").setWidth("30px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Audio::getDuration));
        grid.addColumn(Audio::getStatus).setHeader("Status").setWidth("70px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Audio::getStatus));
        grid.addColumn(Audio::getDetailsAsString).setHeader("Details").setWidth("250px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(Audio::getDetailsAsString));

//        grid.addColumn(TemplateRenderer.<Audio> of("<b>[[item.name]]</b>")
//                .withProperty("name", Audio::getName)).setHeader("Name");

        grid.asSingleSelect().addValueChangeListener(e -> {
            //customizationTabs.editCustomization(e.getValue(), restClient);
        });

//        grid.addColumn(new NativeButtonRenderer<>("...", clickedItem -> {
//            EditWord editWord = new EditWord(clickedItem, false, this, lmCustomization, restClient);
//            getUI().get().add(editWord);
//            editWord.open();
//        }));

        Grid.Column<Audio> columnAdditional =  grid.addColumn(new ComponentRenderer<>(audio-> {
           // Icon iconDetails = VaadinIcon.BULLETS.create();
           // iconDetails.setColor("darkgray");
//            Button btnDetails = new Button("", event -> {
////                EditWord editWord = new EditWord(word, false, this, lmCustomization, restClient);
////                getUI().get().add(editWord);
////                editWord.open();
//            });
//            btnDetails.setWidth("20px");btnDetails.setHeight("25px");
//            btnDetails.setIcon(iconDetails);

            Icon iconDelete = VaadinIcon.FILE_REMOVE.create();
            iconDelete.setColor(COLOR_BE_CAREFULL_FOREGROUND);
            Button btnDelete = new Button("", event -> {
                ComponentEventListener yeslistener = (ComponentEventListener) event1 -> deleteAudio(audio);
                ComponentEventListener nolistener = (ComponentEventListener) event12 -> { };
                ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
                getUI().ifPresent(ui -> ui.add(dialog));
                dialog.open(null, "Are you sure you want to delete this audio?");
            });
            btnDelete.setWidth("20px");btnDelete.setHeight("25px");
            btnDelete.setIcon(iconDelete);
            if (m_audioContainer != null) {
                btnDelete.setVisible(false);
            }

            //Div divButtons = new Div(btnDetails, btnDelete);
            Div divButtons = new Div(btnDelete);
            divButtons.setHeight("35px");
            //buttons.setSpacing(false);
            return divButtons;
        })).setHeader("");

        grid.addColumn(new ComponentRenderer<>(audio-> {
            Icon iconEnter = VaadinIcon.ARROW_FORWARD.create();
            Button btnEnter = new Button("", event -> {
                navigateIntoArchive(audio);
            });
            btnEnter.setWidth("20px");
            btnEnter.setHeight("25px");
            btnEnter.setIcon(iconEnter);
            if (audio.IsArchive()) {
                iconEnter.setColor("darkblue");
                btnEnter.setEnabled(true);
                btnEnter.setIcon(iconEnter);
            }
            else {
                iconEnter.setColor("darkgray");
                btnEnter.setEnabled(false);
                btnEnter.setIcon(null);
                btnEnter.setVisible(false);
            }

            Div divButtons = new Div(btnEnter);
            divButtons.setHeight("35px");
            //buttons.setSpacing(false);
            return divButtons;
        })).setHeader("");

        Icon iconnavigateBack =VaadinIcon.ARROW_BACKWARD.create();
        iconnavigateBack.setColor("darkblue");
        btnNavigateback = new Button("Back", event -> {
            editCustomization(m_customization, restClient);
        });
        btnNavigateback.setIcon(iconnavigateBack);
        btnNavigateback.setVisible(false);
        add(btnNavigateback);

        add(grid);
        HorizontalLayout hl = new HorizontalLayout(cmbFilter, textFieldFilter, btnNewAudio);
        add(hl);
    }

    private void navigateIntoArchive(Audio audio) {
        m_audioContainer = audio;
        btnNavigateback.setVisible(true);
        btnNewAudio.setVisible(false);
        try {
            audioRepository.loadAudio(restClient, m_customization.getCustomization_id(), audio.getName());
            if (audioRepository.getAudio() != null)
                grid.setItems(audioRepository.getAudio());
            else
                grid.setItems(new ArrayList<>());
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }

    }

    public void editCustomization(AMCustomization customization, RestClient restClient) {
        this.m_customization = customization;
        this.m_audioContainer = null;
        btnNavigateback.setVisible(false);
        btnNewAudio.setVisible(true);

        this.restClient = restClient;
        try {
            audioRepository.loadAudio(restClient, customization.getCustomization_id(), null);
            if (audioRepository.getAudio() != null)
                grid.setItems(audioRepository.getAudio());
            else
                grid.setItems(new ArrayList<>());
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }


    }

    void reloadGrid(String filterText) {
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
            grid.setItems(Utils.toList(audioRepository.findAll()));
        else
            grid.setItems(audioRepository.findByNameContains(filterText));
    }

    private void deleteAudio(Audio audio) {
        try {
            restClient.deleteAudio(m_customization.getCustomization_id(), audio.getName());
            customizationTabs.reloadCurrentCustomization();
            reloadGrid(textFieldFilter.getValue());
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

}

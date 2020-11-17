package com.ibm.ttscustomization.ttsui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.utils.Constants;
import com.ibm.sttcustomization.utils.Utils;
import com.ibm.ttscustomization.ttsmodel.RestClientTTS;
import com.ibm.ttscustomization.ttsmodel.customizations.TTSCustomisationRepository;
import com.ibm.ttscustomization.ttsmodel.customizations.TTSCustomization;
import com.ibm.ttscustomization.ttsmodel.voices.TTSVoiceRepository;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.ibm.sttcustomization.utils.Constants.COLOR_BE_CAREFULL_FOREGROUND;

/**
 * The main view contains a simple label element and a template element.
 */
@HtmlImport("styles/shared-styles.html")
@Route("maintts")
@Theme(Lumo.class)
//@BodySize(height = "100vh", width = "100vw")
//@BodySize(height = "100vh", width = "100%")
//@Push
//@Push(transport = Transport.WEBSOCKET_XHR)
public class MainTTS extends VerticalLayout implements RouterLayout {

    private TTSCustomisationRepository ttsCustomisationRepository;
    private TTSCustomizationTabs customizationTabs;
    Grid<TTSCustomization> grid = new Grid<>(TTSCustomization.class);;
    TextField textFieldFilter;
    private RestClientTTS restClient;
    ComboBox<String> cmbFilter;
    private Button btnCreateNewCustomization;
    private List<TTSCustomization> lmCustomizationsCurrent;
    private SortedSet<String> languagesAllowingCustomization = new TreeSet<String>();
    private SortedSet<String> modelsAllowingCustomization = new TreeSet<String>();
//    private RefresherThread refresherThread;
    private final TTSVoiceRepository voiceRepository;

    public SortedSet<String> getLanguagesAllowingCustomization() {
        return languagesAllowingCustomization;
    }

    public SortedSet<String> getModelsAllowingCustomization() {
        return modelsAllowingCustomization;
    }

    public TTSVoiceRepository getVoiceRepository() { return voiceRepository; }

    private HorizontalLayout buildHeader() {
//        Div divLeft = new Div(new Anchor("mainlm", "LM Customizations"));
//        divLeft.getStyle().set("position", "fixed");
//        divLeft.getStyle().set("left", "30px");


        Span centerCaption = new Span("TTS Customizations");
        Div divCenter = new Div(centerCaption);
        divCenter.getStyle().set("margin-left", "auto");
        divCenter.getStyle().set("margin-right", "auto");
        divCenter.getStyle().set("fontWeight", "bold");


        String route = "https://console.bluemix.net/docs/services/speech-to-text/getting-started.html#gettingStarted";
        Anchor anchor = new Anchor(route, "Docs");
        anchor.setTarget( "_blank" ) ;
        Div divRight = new Div(anchor);
        divRight.getStyle().set("position", "fixed");
        divRight.getStyle().set("right", "130px");
        //divRight.getStyle().set("text-align", "right");

        HorizontalLayout header = new HorizontalLayout();
        header.getStyle().set("font-size", "25px");
        header.setWidth("100%");
        header.setHeight("30px");
        //header.add(divLeft, divCenter, divRight);
        header.add(new Div(), divCenter, divRight);

        return header;
    }


    public MainTTS() {
        VaadinSession.getCurrent().setAttribute("maintts", this);

        String username = (String)VaadinSession.getCurrent().getAttribute("usernametts");
        String password = (String)VaadinSession.getCurrent().getAttribute("passwordtts");
        String url = (String)VaadinSession.getCurrent().getAttribute("urltts");
        this.voiceRepository = new TTSVoiceRepository();

        if ((username == null) || (password == null)) {
            Anchor anchor = new Anchor("logintts", "Please Login to use TTS Customization Tools");
            Div divGoToLogin = new Div(anchor);
            divGoToLogin.getStyle().set("left", "150px");
            divGoToLogin.getStyle().set("top", "100px");
            divGoToLogin.getStyle().set("position", "relative");
//            divGoToLogin.setSizeFull();
//            divGoToLogin.getStyle().set("padding", "150px 0");
//            divGoToLogin.getStyle().set("text-align", "center");
            add(divGoToLogin);
            setSizeFull();
        }
        else {
            this.restClient = new RestClientTTS();
            restClient.setUser(username);
            restClient.setPassword(password);
            restClient.setHost(url);

            this.ttsCustomisationRepository = new TTSCustomisationRepository();
            try {
                ttsCustomisationRepository.loadCustomizations(restClient);
                voiceRepository.loadVoices(restClient);
            } catch (GenericException e) {
                Utils.displayErrorMessage(e);
            }

            grid.setSelectionMode(Grid.SelectionMode.SINGLE);
            grid.setHeight("100%");
            grid.setWidth("100%");

            this.textFieldFilter = new TextField();
            this.btnCreateNewCustomization = new Button("New Customization");
            btnCreateNewCustomization.addClickListener(e -> {
                EditTTSCustomization editTTSCustomization = new EditTTSCustomization(true, this, new TTSCustomization(), restClient);
                getUI().get().add(editTTSCustomization);
                editTTSCustomization.open();
            });

            this.cmbFilter = new ComboBox<>();
            SortedSet<String> ssFilters = new TreeSet<>();
            ssFilters.add("Filter by Name");
            ssFilters.add("Filter by Id");
            ssFilters.add("Filter by Language");
            //ssFilters.add("Filter by Status");
            //cmbFilter.setTextInputAllowed(false);
            cmbFilter.setItems(ssFilters); //configure the possible options (usually, this is not a manually defined list...)
            cmbFilter.setValue("Filter by Name"); //load the value of given data object
            cmbFilter.addValueChangeListener(e -> textFieldFilter.setValue(""));

            textFieldFilter.addValueChangeListener(e -> filterCustomizations(e.getValue()));

            // Actions
            Icon iconRefresh = VaadinIcon.REFRESH.create();

            iconRefresh.setColor("white");
            Button btnRefreshVuew = new Button("Refresh", event -> {
                refreshView();
            });
            //btnRefreshVuew.setWidth("20px");btnRefreshVuew.setHeight("25px");
            btnRefreshVuew.getStyle().set("background-color", Constants.COLOR_REFRESH_BACKGROUND);
            btnRefreshVuew.getStyle().set("color", Constants.COLOR_REFRESH_FOREGROUND);
            btnRefreshVuew.setIcon(iconRefresh);
            HorizontalLayout horizontalLayoutFilter = new HorizontalLayout(cmbFilter, textFieldFilter, btnCreateNewCustomization,
                    new Label(""),
                    new Label(""),
                    new Label(""),
                    new Label(""),
                    btnRefreshVuew);

            VerticalLayout layoutCustomizationView = new VerticalLayout(grid, horizontalLayoutFilter);

            layoutCustomizationView.setWidth("100%");
            layoutCustomizationView.setHeight("95%");

            // layoutCustomizationView.setSizeFull();
            this.customizationTabs = new TTSCustomizationTabs(this, ttsCustomisationRepository);

            reloadCutomizations(null);

            SplitLayout splitLayout = new SplitLayout(layoutCustomizationView, customizationTabs);
            splitLayout.setHeight("100%");
            splitLayout.setWidth("100%");

            add(buildHeader());

            grid.asSingleSelect().addValueChangeListener(e -> {
                customizationTabs.loadCustomization(e.getValue(), restClient);
            });

            filterCustomizations(null);

            grid.setMultiSort(true);

//            this.last_modified = t.last_modified;

            // show only: guid, created, descroption name, status
            grid.removeColumn(grid.getColumnByKey("owner"));
            grid.removeColumn(grid.getColumnByKey("description"));
            grid.removeColumn(grid.getColumnByKey("edited"));
            grid.removeColumn(grid.getColumnByKey("runtimeid"));
            grid.removeColumn(grid.getColumnByKey("customization_id"));
            grid.removeColumn(grid.getColumnByKey("language"));
            grid.removeColumn(grid.getColumnByKey("name"));
            grid.removeColumn(grid.getColumnByKey("wordswereloaded"));
//            grid.removeColumn(grid.getColumnByKey("words"));
            grid.removeColumn(grid.getColumnByKey("markedForDelection"));
            grid.removeColumn(grid.getColumnByKey("created"));
            grid.removeColumn(grid.getColumnByKey("wordsList"));
            grid.removeColumn(grid.getColumnByKey("last_modified"));
            grid.removeColumn(grid.getColumnByKey("lastModifiedLocalDate"));


            grid.addColumn(TTSCustomization::getCustomization_id).setHeader("id").setWidth("70px").setResizable(true).setSortable(true).
                    setComparator(Comparator.comparing(TTSCustomization::getCustomization_id));
            grid.addColumn(TTSCustomization::getName).setHeader("Name").setWidth("70px").setResizable(true).setSortable(true).
                setComparator(Comparator.comparing(TTSCustomization::getName));
            grid.addColumn(TTSCustomization::getLanguage).setHeader("Language").setWidth("70px").setResizable(true).setSortable(true).
                    setComparator(Comparator.comparing(TTSCustomization::getLanguage));

            LocalDateTimeRenderer localDateRenderer = new LocalDateTimeRenderer<>(TTSCustomization::getlastModifiedLocalDate);
            Grid.Column<TTSCustomization> column = grid.addColumn(localDateRenderer, "dd/MM/yyyy");
            column.setResizable(true).setSortable(true).setHeader("last Modified").
                    setComparator(Comparator.comparing(TTSCustomization::getlastModifiedLocalDate));

            grid.addColumn(new ComponentRenderer<>(customization -> {
                Icon iconDetails = VaadinIcon.BULLETS.create();
                iconDetails.setColor("darkgray");
                Button btnDetails = new Button("", event -> {
                    EditTTSCustomization editeditTTSCustomization = new EditTTSCustomization(false, this, customization, restClient);
                    getUI().get().add(editeditTTSCustomization);
                    editeditTTSCustomization.open();
                    //grid.getDataProvider().refreshItem(person);
                });
                btnDetails.setWidth("20px");btnDetails.setHeight("25px");
                btnDetails.setIcon(iconDetails);

                Icon iconDelete = VaadinIcon.FILE_REMOVE.create();
                iconDelete.setColor(COLOR_BE_CAREFULL_FOREGROUND);
                Button btnDelete = new Button("", event -> {
                    ComponentEventListener yeslistener = (ComponentEventListener) event1 -> deleteCustomization(customization);
                    ComponentEventListener nolistener = (ComponentEventListener) event12 -> { };
                    ConfirmationDialog dialog = new ConfirmationDialog(yeslistener, nolistener);
                    getUI().ifPresent(ui -> ui.add(dialog));
                    dialog.open(null, "Are you sure you want to delete this customization?");
                });
                btnDelete.setWidth("20px");btnDelete.setHeight("25px");
                btnDelete.setIcon(iconDelete);

                //Div divButtons = new Div(btnDetails, btnRefresh, btnDelete);
                Div divButtons = new Div(btnDetails, btnDelete);
                divButtons.setHeight("35px");
                //buttons.setSpacing(false);
                return divButtons;
            })).setHeader("");


            add(splitLayout);
            setSizeFull();
        }

        setClassName("main-layout");
    }

    private void reloadCutomizations(TTSCustomization ttsCustomization) {
        try {
            ttsCustomisationRepository.loadCustomizations(restClient);
            filterCustomizations(textFieldFilter.getValue());
            if (ttsCustomization != null) {
                grid.select(ttsCustomization);
            }
            if (!lmCustomizationsCurrent.isEmpty()) {
                ttsCustomization = lmCustomizationsCurrent.get(0);
                if(ttsCustomization != null)
                    grid.select(ttsCustomization);
            }
            customizationTabs.loadCustomization(ttsCustomization, restClient);
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    private void deleteCustomization(TTSCustomization customization) {
        try {
            String sRet = restClient.deleteCustomVoice(customization.getCustomization_id());
        }
        catch (GenericException genericException) {
            Utils.displayErrorMessage(genericException);
        }
        reloadCutomizations(null);
    }

    void filterCustomizations(String filterText) {
        String filterBy = cmbFilter.getValue();
        if (filterBy == null)
            filterBy = "Filter by Name";

        if (filterBy.equalsIgnoreCase("Filter by Id")) {
            listCustomizationsById(filterText);
        }
        else if (filterBy.equalsIgnoreCase("Filter by Name")) {
            listCustomizationsByName(filterText);
        }
        else if (filterBy.equalsIgnoreCase("Filter by Language")) {
            listCustomizationsByLanguage(filterText);
        }
//        else if (filterBy.equalsIgnoreCase("Filter by Status")) {
//            listCustomizationsByStatus(filterText);
//        }
    }

    private void grid_setItems(List<TTSCustomization> lmCustomizations) {
        this.lmCustomizationsCurrent = lmCustomizations;
        grid.setItems(lmCustomizations);
    }


    void listCustomizationsByName(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid_setItems(Utils.toList(ttsCustomisationRepository.findAll()));
        else
            grid_setItems(ttsCustomisationRepository.findByCustomizationNameContains(filterText));
    }

    void listCustomizationsById(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid_setItems(Utils.toList(ttsCustomisationRepository.findAll()));
        else
            grid_setItems(ttsCustomisationRepository.findByCustomizationIdContains(filterText));

        grid.getDataProvider().refreshAll();
    }

    private void listCustomizationsByLanguage(String filterText) {
        if (StringUtils.isEmpty(filterText))
            grid_setItems(Utils.toList(ttsCustomisationRepository.findAll()));
        else
            grid_setItems(ttsCustomisationRepository.findByCustomizationLanguageContains(filterText));
    }

//    private void listCustomizationsByStatus(String filterText) {
//        if (StringUtils.isEmpty(filterText))
//            grid_setItems(Utils.toList(customisationLMRepository.findAll()));
//        else
//            grid_setItems(customisationLMRepository.findByStatusContains(filterText));
//    }
//

    public void refreshView() {
        Set<TTSCustomization> setSelectedItems = grid.getSelectedItems();
        TTSCustomization currentlySelected = null;
        if (!setSelectedItems.isEmpty())
            currentlySelected = grid.getSelectedItems().iterator().next();
        reloadCutomizations(currentlySelected);
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        //add(new Span("Waiting for updates"));

        // Start the data feed thread
//        refresherThread = new MainLM.RefresherThread(attachEvent.getUI(), this);
//        refresherThread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
//        refresherThread.interrupt();
//        refresherThread = null;
    }



}

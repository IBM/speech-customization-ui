package com.ibm.sttcustomization.amui;

import com.ibm.sttcustomization.ConfirmationDialog;
import com.ibm.sttcustomization.ExampleTemplate;
import com.ibm.sttcustomization.lmui.CustomizationTabs;
import com.ibm.sttcustomization.model.AMCustomizations.CustomisationAMRepository;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.AMCustomizations.CustomisationAMRepository;
import com.ibm.sttcustomization.model.AMCustomizations.AMCustomization;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.STTModel.STTModelsReply;
import com.ibm.sttcustomization.utils.Constants;
import com.ibm.sttcustomization.utils.Utils;
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
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.ibm.sttcustomization.utils.Constants.COLOR_BE_CAREFULL_FOREGROUND;

/**
 * The main view contains a simple label element and a template element.
 */
@HtmlImport("styles/shared-styles.html")
@Route("mainam")
@Theme(Lumo.class)
//@BodySize(height = "100vh", width = "100vw")
//@BodySize(height = "100vh", width = "100%")
//@Push
//@Push(transport = Transport.WEBSOCKET_XHR)
public class MainAM extends VerticalLayout implements RouterLayout {

    private CustomisationAMRepository customisationLMRepository;
    private AMCustomizationTabs customizationTabs;
    Grid<AMCustomization> gridCustomizations = new Grid<>(AMCustomization.class);;
    TextField textFieldFilter;
    private RestClient restClient;
    ComboBox<String> cmbFilter;
    private Button btnCreateNewCustomization;
    private List<AMCustomization> lmCustomizationsCurrent;
    private SortedSet<String> languagesAllowingCustomization = new TreeSet<String>();
    private SortedSet<String> modelsAllowingCustomization = new TreeSet<String>();
//    private RefresherThread refresherThread;

    public SortedSet<String> getLanguagesAllowingCustomization() {
        return languagesAllowingCustomization;
    }

    public SortedSet<String> getModelsAllowingCustomization() {
        return modelsAllowingCustomization;
    }

    private HorizontalLayout buildHeader() {
        Div divLeft = new Div(new Anchor("mainlm", "LM Customizations"));
        divLeft.getStyle().set("position", "fixed");
        divLeft.getStyle().set("left", "30px");


        Span centerCaption = new Span("AM Customizations");
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
        header.add(divLeft, divCenter, divRight);

        return header;
    }


    public MainAM() {
        VaadinSession.getCurrent().setAttribute("mainam", this);

        String username = (String)VaadinSession.getCurrent().getAttribute("username");
        String password = (String)VaadinSession.getCurrent().getAttribute("password");
        String url = (String)VaadinSession.getCurrent().getAttribute("url");

        if ((username == null) || (password == null)) {
            Anchor anchor = new Anchor("loginam", "Please Login to use STT Customization Tools");
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
            this.restClient = new RestClient();
            restClient.setUser(username);
            restClient.setPassword(password);
            restClient.setHost(url);

            try {
                STTModelsReply sttModelsReply = restClient.querySTTModels();
                languagesAllowingCustomization = sttModelsReply.getLanguagesAllowingCustomization();
                modelsAllowingCustomization = sttModelsReply.getModelsAllowingCustomization();
            } catch (GenericException e) {
                Utils.displayErrorMessage(e);
            }

            this.customisationLMRepository = new CustomisationAMRepository();

            gridCustomizations.setSelectionMode(Grid.SelectionMode.SINGLE);
            gridCustomizations.setHeight("100%");
            gridCustomizations.setWidth("100%");

            this.textFieldFilter = new TextField();
            this.btnCreateNewCustomization = new Button("New Customization");
            btnCreateNewCustomization.addClickListener(e -> {
                EditAMCustomization editAMCustomization = new EditAMCustomization(true, this, new AMCustomization(), restClient);
                getUI().get().add(editAMCustomization);
                editAMCustomization.open();
            });

            this.cmbFilter = new ComboBox<>();
            SortedSet<String> ssFilters = new TreeSet<>();
            ssFilters.add("Filter by Name");
            ssFilters.add("Filter by Id");
            ssFilters.add("Filter by Language");
            ssFilters.add("Filter by Status");
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

            VerticalLayout layoutCustomizationView = new VerticalLayout(gridCustomizations, horizontalLayoutFilter);

            layoutCustomizationView.setWidth("100%");
            layoutCustomizationView.setHeight("95%");

            // layoutCustomizationView.setSizeFull();
            this.customizationTabs = new AMCustomizationTabs(this, customisationLMRepository);

            reloadCutomizations(null);

            SplitLayout splitLayout = new SplitLayout(layoutCustomizationView, customizationTabs);
            splitLayout.setHeight("100%");
            splitLayout.setWidth("100%");

            add(buildHeader());

            gridCustomizations.asSingleSelect().addValueChangeListener(e -> {
                customizationTabs.loadCustomization(e.getValue(), restClient);
            });

            filterCustomizations(null);

            gridCustomizations.setMultiSort(true);

            // show only: guid, created, descroption name, status
            gridCustomizations.removeColumn(gridCustomizations.getColumnByKey("owner"));
            gridCustomizations.removeColumn(gridCustomizations.getColumnByKey("base_model_name"));
            gridCustomizations.removeColumn(gridCustomizations.getColumnByKey("versions"));
            gridCustomizations.removeColumn(gridCustomizations.getColumnByKey("progress"));
            gridCustomizations.removeColumn(gridCustomizations.getColumnByKey("createdLocalDate"));

            gridCustomizations.removeColumn(gridCustomizations.getColumnByKey("created"));
//        gridCustomizations.removeColumn(gridCustomizations.getColumnByKey("language"));

            gridCustomizations.getColumnByKey("customization_id").setResizable(true).setSortable(true).setWidth("110px").
                    setComparator(Comparator.comparing(AMCustomization::getCustomization_id));
            gridCustomizations.getColumnByKey("name").setResizable(true).setSortable(true).setWidth("110px").
                    setComparator(Comparator.comparing(AMCustomization::getName));
            gridCustomizations.getColumnByKey("description").setResizable(true).setSortable(true).
                    setComparator(Comparator.comparing(AMCustomization::getDescription));
            gridCustomizations.getColumnByKey("status").setResizable(true).setSortable(true).setWidth("70px").
                    setComparator(Comparator.comparing(AMCustomization::getStatus));
            gridCustomizations.getColumnByKey("language").setResizable(true).setSortable(true).setWidth("50px").
                    setComparator(Comparator.comparing(AMCustomization::getLanguage));
            gridCustomizations.getColumnByKey("versionsAsString").setResizable(true).setSortable(true).setHeader("Versions").
                    setComparator(Comparator.comparing(AMCustomization::getVersionsAsString));

            LocalDateTimeRenderer localDateRenderer = new LocalDateTimeRenderer<>(AMCustomization::getCreatedLocalDate);
            Grid.Column<AMCustomization> column = gridCustomizations.addColumn(localDateRenderer, "dd/MM/yyyy");
            column.setResizable(true).setSortable(true).setHeader("Created").
                    setComparator(Comparator.comparing(AMCustomization::getCreatedLocalDate));

//            gridCustomizations.addColumn(new NativeButtonRenderer<>("...", clickedItem -> {
//                EditAMCustomization editAMCustomization = new EditAMCustomization(false, this, clickedItem, restClient);
//                getUI().get().add(editAMCustomization);
//                editAMCustomization.open();
//            })).setWidth("50px");

            gridCustomizations.addColumn(new ComponentRenderer<>(customization -> {
                Icon iconDetails = VaadinIcon.BULLETS.create();
                iconDetails.setColor("darkgray");
                Button btnDetails = new Button("", event -> {
                    EditAMCustomization editAMCustomization = new EditAMCustomization(false, this, customization, restClient);
                    getUI().get().add(editAMCustomization);
                    editAMCustomization.open();
                    //grid.getDataProvider().refreshItem(person);
                });
                btnDetails.setWidth("20px");btnDetails.setHeight("25px");
                btnDetails.setIcon(iconDetails);

//                Icon iconRefresh = VaadinIcon.REFRESH.create();
//                iconRefresh.setColor("darkgray");
//                Button btnRefresh = new Button("", event -> {
//                    refreshView();
//                });
//                btnRefresh.setWidth("20px");btnRefresh.setHeight("25px");
//                btnRefresh.setIcon(iconRefresh);

                Icon iconDelete = VaadinIcon.FILE_REMOVE.create();
                iconDelete.setColor(COLOR_BE_CAREFULL_FOREGROUND);
                Button btnDelete = new Button("", event -> {
                    ComponentEventListener yeslistener = (ComponentEventListener) event1 -> deleteModel(customization);
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

    private void reloadCutomizations(AMCustomization amCustomization) {
        try {
            customisationLMRepository.loadCustomizations(restClient);
            filterCustomizations(textFieldFilter.getValue());
            if (amCustomization != null) {
                gridCustomizations.select(amCustomization);
            }
            if (!lmCustomizationsCurrent.isEmpty()) {
                amCustomization = lmCustomizationsCurrent.get(0);
                if(amCustomization != null)
                    gridCustomizations.select(amCustomization);
            }
            customizationTabs.loadCustomization(amCustomization, restClient);
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    private void deleteModel(AMCustomization customization) {
        try {
            restClient.deleteAMCustomization(customization);
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
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
        else if (filterBy.equalsIgnoreCase("Filter by Status")) {
            listCustomizationsByStatus(filterText);
        }
    }

    private void gridCustomizations_setItems(List<AMCustomization> lmCustomizations) {
        this.lmCustomizationsCurrent = lmCustomizations;
        gridCustomizations.setItems(lmCustomizations);
    }


    void listCustomizationsByName(String filterText) {
        if (StringUtils.isEmpty(filterText))
            gridCustomizations_setItems(Utils.toList(customisationLMRepository.findAll()));
        else
            gridCustomizations_setItems(customisationLMRepository.findByCustomizationNameContains(filterText));
    }

    void listCustomizationsById(String filterText) {
        if (StringUtils.isEmpty(filterText))
            gridCustomizations_setItems(Utils.toList(customisationLMRepository.findAll()));
        else
            gridCustomizations_setItems(customisationLMRepository.findByCustomizationIdContains(filterText));

        gridCustomizations.getDataProvider().refreshAll();
    }

    private void listCustomizationsByLanguage(String filterText) {
        if (StringUtils.isEmpty(filterText))
            gridCustomizations_setItems(Utils.toList(customisationLMRepository.findAll()));
        else
            gridCustomizations_setItems(customisationLMRepository.findByCustomizationLanguageContains(filterText));
    }

    private void listCustomizationsByStatus(String filterText) {
        if (StringUtils.isEmpty(filterText))
            gridCustomizations_setItems(Utils.toList(customisationLMRepository.findAll()));
        else
            gridCustomizations_setItems(customisationLMRepository.findByStatusContains(filterText));
    }


    public void refreshView() {
        Set<AMCustomization> setSelectedItems = gridCustomizations.getSelectedItems();
        AMCustomization currentlySelected = null;
        if (!setSelectedItems.isEmpty())
            currentlySelected = gridCustomizations.getSelectedItems().iterator().next();
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

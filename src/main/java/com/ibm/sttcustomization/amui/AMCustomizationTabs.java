package com.ibm.sttcustomization.amui;

import com.ibm.sttcustomization.model.AMCustomizations.CustomisationAMRepository;
import com.ibm.sttcustomization.model.AMCustomizations.AMCustomization;
import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.utils.Utils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//@UIScope
public class AMCustomizationTabs extends VerticalLayout {
    private final CustomisationAMRepository customisationAMRepository;
    private AMCustomization customization;
    private final AMContentTab amContentTab;
    private final MainAM mainAM;
    private RestClient restClient;

    @Autowired
    public AMCustomizationTabs(MainAM mainLM, CustomisationAMRepository customisationLMRepository) {
        this.mainAM = mainLM;
        this.customisationAMRepository = customisationLMRepository;

        Tab tab = new Tab("Audio");
        amContentTab = new AMContentTab(this);
        amContentTab.setSizeFull();
       // pageWords.setText("Page#1");

        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(tab, amContentTab);
        Tabs tabs = new Tabs(tab);//, tabCorpora, tabGrammars);
        Div pages = new Div(amContentTab);//, pageCorpora, pageGrammars);
        Set<Component> pagesShown = Stream.of(amContentTab).collect(Collectors.toSet());

        tabs.addSelectedChangeListener(event -> {
            pagesShown.forEach(page -> page.setVisible(false));
            pagesShown.clear();
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
            pagesShown.add(selectedPage);
        });

        add(tabs);
        add(amContentTab);//, pageCorpora, pageGrammars);
        setSizeFull();
        loadCustomization(null, null);
    }

    public void loadCustomization(AMCustomization customization, RestClient restClient) {
        if (customization == null) {
            //this.setEnabled(false);
            return;
        }
        else {
            //setEnabled(true);
        }

        this.restClient = restClient;
        if (customization == null) {
            return; // TODo: clear tabs
        }
        this.customization = customization;
        amContentTab.editCustomization(customization, restClient);
    }

    public void reloadCurrentCustomization() {
        loadCustomization(customization, restClient);
    }

}

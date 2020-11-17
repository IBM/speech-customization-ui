package com.ibm.ttscustomization.ttsui;

import com.ibm.ttscustomization.ttsmodel.RestClientTTS;
import com.ibm.ttscustomization.ttsmodel.customizations.TTSCustomisationRepository;
import com.ibm.ttscustomization.ttsmodel.customizations.TTSCustomization;
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
public class TTSCustomizationTabs extends VerticalLayout {
    private final TTSCustomisationRepository customisationAMRepository;
    private TTSCustomization customization;
    private final TTSContentTab ttsContentTab;
    private final MainTTS mainAM;
    private RestClientTTS restClient;

    @Autowired
    public TTSCustomizationTabs(MainTTS mainLM, TTSCustomisationRepository customisationLMRepository) {
        this.mainAM = mainLM;
        this.customisationAMRepository = customisationLMRepository;

        Tab tab = new Tab("Words");
        ttsContentTab = new TTSContentTab(this);
        ttsContentTab.setSizeFull();
       // pageWords.setText("Page#1");

        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(tab, ttsContentTab);
        Tabs tabs = new Tabs(tab);//, tabCorpora, tabGrammars);
        Div pages = new Div(ttsContentTab);//, pageCorpora, pageGrammars);
        Set<Component> pagesShown = Stream.of(ttsContentTab).collect(Collectors.toSet());

        tabs.addSelectedChangeListener(event -> {
            pagesShown.forEach(page -> page.setVisible(false));
            pagesShown.clear();
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
            pagesShown.add(selectedPage);
        });

        add(tabs);
        add(ttsContentTab);//, pageCorpora, pageGrammars);
        setSizeFull();
        loadCustomization(null, null);
    }

    public void loadCustomization(TTSCustomization customization, RestClientTTS restClient) {
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
        ttsContentTab.editCustomization(customization, restClient);
    }

    public void reloadCurrentCustomization() {
        loadCustomization(customization, restClient);
    }

}

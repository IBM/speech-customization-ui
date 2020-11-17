package com.ibm.sttcustomization.lmui;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.LMCustomizations.CustomisationLMRepository;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
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
public class CustomizationTabs extends VerticalLayout {
    private final CustomisationLMRepository customisationLMRepository;
    private LMCustomization lmCustomization;
    private final WordsTab pageWords;
    private final CorporaTab pageCorpora;
    private final GrammarsTab pageGrammars;
    private final MainLM mainLM;
    private RestClient restClient;

    @Autowired
    public CustomizationTabs(MainLM mainLM, CustomisationLMRepository customisationLMRepository) {
        this.mainLM = mainLM;
        this.customisationLMRepository = customisationLMRepository;

        Tab tabWords = new Tab("Words");
        pageWords = new WordsTab(this);
        pageWords.setSizeFull();
       // pageWords.setText("Page#1");

        Tab tabCorpora = new Tab("Corpora");
        pageCorpora = new CorporaTab(this);
        pageCorpora.setSizeFull();
        pageCorpora.setVisible(false);

        Tab tabGrammars = new Tab("Grammars");
        pageGrammars = new GrammarsTab(this);
        pageGrammars.setSizeFull();
        pageGrammars.setVisible(false);

        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(tabWords, pageWords);
        tabsToPages.put(tabCorpora, pageCorpora);
        tabsToPages.put(tabGrammars, pageGrammars);
        Tabs tabs = new Tabs(tabWords, tabCorpora, tabGrammars);
        Div pages = new Div(pageWords, pageCorpora, pageGrammars);
        Set<Component> pagesShown = Stream.of(pageWords).collect(Collectors.toSet());

        tabs.addSelectedChangeListener(event -> {
            pagesShown.forEach(page -> page.setVisible(false));
            pagesShown.clear();
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
            pagesShown.add(selectedPage);
        });

        add(tabs);
        add(pageWords, pageCorpora, pageGrammars);
        setSizeFull();
        loadCustomization(null, null);
    }

    public void loadCustomization(LMCustomization lmCustomization, RestClient restClient) {
        if (lmCustomization == null) {
            //this.setEnabled(false);
            return;
        }
        else {
            //setEnabled(true);
        }

        this.restClient = restClient;
        if (lmCustomization == null) {
            return; // TODo: clear tabs
        }
        this.lmCustomization = lmCustomization;
        try {
            pageWords.editCustomization(lmCustomization, restClient);
            pageCorpora.editCustomization(lmCustomization, restClient);
            pageGrammars.editCustomization(lmCustomization, restClient);
        } catch (GenericException e) {
            Utils.displayErrorMessage(e);
        }
    }

    public void reloadCurrentCustomization() {
        loadCustomization(lmCustomization, restClient);
    }

}

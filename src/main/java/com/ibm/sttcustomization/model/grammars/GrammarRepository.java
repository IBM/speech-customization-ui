package com.ibm.sttcustomization.model.grammars;


import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.model.words.QueryWordsReply;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class GrammarRepository {
    ArrayList<Grammar> grammars = new ArrayList<>();

    public void addWord(Grammar grammar) {
        grammars.add(grammar);
    }

    public void clear() {
        grammars.clear();
    }

    public List<Grammar> findByNameContains(String filterText) {
        ArrayList<Grammar> arrRet = new ArrayList<Grammar>();
        for (Grammar grammar : grammars) {
            if (grammar.getName().contains(filterText))
                arrRet.add(grammar);
        }
        return arrRet;
    }

    public List<Grammar> findByStatusContains(String filterText) {
        ArrayList<Grammar> arrRet = new ArrayList<Grammar>();
        for (Grammar grammar : grammars) {
            if (grammar.getStatus().contains(filterText))
                arrRet.add(grammar);
        }
        return arrRet;
    }

    public List<Grammar> findAll() {
        return grammars;
    }

    public void loadGrammars(RestClient restClient, String customization_id) throws GenericException {
        QueryGrammarsReply queryGrammarsReply = null;
        queryGrammarsReply = restClient.queryGrammarsByLMCustomization(customization_id);
        grammars = queryGrammarsReply.getGrammars();
    }


    public ArrayList<Grammar> getGrammars() {
        return grammars;
    }

    public void setGrammars(ArrayList<Grammar> grammars) {
        this.grammars = grammars;
    }

}

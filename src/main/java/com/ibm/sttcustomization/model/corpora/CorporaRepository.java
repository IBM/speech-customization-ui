package com.ibm.sttcustomization.model.corpora;


import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class CorporaRepository {
    ArrayList<Corpus> corpora = new ArrayList<>();

    public void addCorpora(Corpus corpus) {
        this.corpora.add(corpus);
    }

    public ArrayList<Corpus> getCorpora() {
        return corpora;
    }

    public void setCorpora(ArrayList<Corpus> corpora) {
        this.corpora = corpora;
    }

    public void clear() {
        corpora.clear();
    }

    public List<Corpus> findByNameStartsWithIgnoreCase(String prefix) {
        ArrayList<Corpus> arrRet = new ArrayList<Corpus>();
        for (Corpus corpus : this.corpora) {
            if (corpus.getName().startsWith(prefix))
                arrRet.add(corpus);
        }
        return arrRet;
    }

    public List<Corpus> findAll() {
        return corpora;
    }

    public void loadCorpora(RestClient restClient, String customization_id) throws GenericException {
        QueryCorporaReply queryCorporaReply = null;
        queryCorporaReply = restClient.queryCorporaByLMCustomization(customization_id);
        corpora = queryCorporaReply.getCorpora();
    }

    public List<Corpus> findByNameContains(String filterText) {
        ArrayList<Corpus> arrRet = new ArrayList<Corpus>();
        for (Corpus corpus : this.corpora) {
            if (corpus.getName().contains(filterText))
                arrRet.add(corpus);
        }
        return arrRet;
    }


    public List<Corpus> findByStatusContains(String filterText) {
        ArrayList<Corpus> arrRet = new ArrayList<Corpus>();
        for (Corpus corpus : this.corpora) {
            if (corpus.getStatus().contains(filterText))
                arrRet.add(corpus);
        }
        return arrRet;
    }
}

package com.ibm.sttcustomization.model.corpora;

import java.util.ArrayList;

public class QueryCorporaReply {

    ArrayList<Corpus> corpora;
    public QueryCorporaReply() {
    }

    public ArrayList<Corpus> getCorpora() {
        return corpora;
    }

    public void setCorpora(ArrayList<Corpus> corpora) {
        this.corpora = corpora;
    }
}

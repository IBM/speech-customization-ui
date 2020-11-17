package com.ibm.sttcustomization.model.words;

import java.sql.Timestamp;
import java.util.ArrayList;

public class QueryWordsReply {

    ArrayList<Word> words;
    public QueryWordsReply() {
    }

    public ArrayList<Word> getWords() { return words; }
    public void setWords(ArrayList<Word> words) { this.words = words; }
}

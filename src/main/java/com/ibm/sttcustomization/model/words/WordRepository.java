package com.ibm.sttcustomization.model.words;


import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import com.ibm.sttcustomization.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class WordRepository {
    ArrayList<Word> words = new ArrayList<>();

    public ArrayList<Word> getWords() {
        return words;
    }

    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }

    public void addWord(Word word) {
        words.add(word);
    }

    public void clear() {
        words.clear();
    }

    public List<Word> findAll() {
        return words;
    }

    public void loadWords(RestClient restClient, String customization_id) throws GenericException {
        QueryWordsReply queryWordsReply = null;
        queryWordsReply = restClient.queryWordsByLMCustomization(customization_id);
        words = queryWordsReply.getWords();
    }

    public List<Word> findByNameContains(String s) {
        ArrayList<Word> arrRet = new ArrayList<Word>();
        for (Word word : words) {
            if (word.getWord().contains(s))
                arrRet.add(word);
        }
        return arrRet;
    }

    public List<Word> findBySourceContains(String s) {
        ArrayList<Word> arrRet = new ArrayList<Word>();

        for (Word word : words) {
            String str = String.join(",", word.getSource());
            if (str.contains(s))
                arrRet.add(word);
        }
        return arrRet;
    }

    public List<Word> findByDisplayAsContains(String s) {
        ArrayList<Word> arrRet = new ArrayList<Word>();
        for (Word word : words) {
            if (word.getDisplay_as().contains(s))
                arrRet.add(word);
        }
        return arrRet;
    }
}

package com.ibm.ttscustomization.ttsmodel.words;


import java.util.ArrayList;
import java.util.List;


public class WordRepository {
    ArrayList<Word> words = new ArrayList<>();

    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }

    public void addWord(Word word) {
        words.add(word);
    }

    public void clear() {
        words.clear();
    }

    public List<Word> findByNameStartsWithIgnoreCase(String prefix) {
        ArrayList<Word> arrRet = new ArrayList<Word>();
        for (Word word : words) {
            if (word.getWord().startsWith(prefix))
                arrRet.add(word);
        }
        return arrRet;
    }

    public List<Word> findByNameContains(String s) {
        ArrayList<Word> arrRet = new ArrayList<Word>();
        for (Word word : words) {
            if (word.getWord().contains(s))
                arrRet.add(word);
        }
        return arrRet;
    }

        public List<Word> findAll() {
        return words;
    }

    public Word findOne(Long id) {
        for (Word word : words) {
            if (word.getRuntimeid() == id)
                return word;
        }
        return null;
    }



}

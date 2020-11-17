package com.ibm.sttcustomization.model.words;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;

@Component
public class Word {
    private String word = "";
    ArrayList<String> sounds_like = new ArrayList<>();
    private String display_as = "";
    ArrayList<String> source = new ArrayList<>();
    private Long count = Long.valueOf(0);

	public Word() {
	}

    public Word(Word w) {
        this.word = w.word;
        this.sounds_like = new ArrayList<>(w.sounds_like);
        this.display_as = w.display_as;
        this.source = new ArrayList<>(source);
        this.count = w.count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ArrayList<String> getSounds_like() {
        return sounds_like;
    }

    public void setSounds_like(ArrayList<String> sounds_like) {
        this.sounds_like = sounds_like;
    }

    public String getDisplay_as() {
        return display_as;
    }

    public void setDisplay_as(String display_as) {
        this.display_as = display_as;
    }

    public ArrayList<String> getSource() {
        return source;
    }

    public void setSource(ArrayList<String> source) {
        this.source = source;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

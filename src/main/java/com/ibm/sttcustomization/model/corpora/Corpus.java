package com.ibm.sttcustomization.model.corpora;

import org.springframework.stereotype.Component;

@Component
public class Corpus {
    private String name;
    private long total_words;
    private long out_of_vocabulary_words;
    private String status;

    public Corpus() {
    }

    public Corpus(Corpus c) {
        this.name = c.name;
        this.total_words = c.total_words;
        this.out_of_vocabulary_words = c.out_of_vocabulary_words;
        this.status = c.status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotal_words() {
        return total_words;
    }

    public void setTotal_words(long total_words) {
        this.total_words = total_words;
    }

    public long getOut_of_vocabulary_words() {
        return out_of_vocabulary_words;
    }

    public void setOut_of_vocabulary_words(long out_of_vocabulary_words) {
        this.out_of_vocabulary_words = out_of_vocabulary_words;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.ibm.sttcustomization.model.grammars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

@Component
public class Grammar {
    private String name;
    @JsonProperty("OOV_words")
    private long OOV_words;
    private String status;

    public Grammar() {
    }

    public Grammar(Grammar g) {
        this.name = g.name;
        this.OOV_words = g.OOV_words;
        this.status = g.status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOOV_words() {
        return OOV_words;
    }

    public void setOOV_words(long OOV_words) {
        this.OOV_words = OOV_words;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

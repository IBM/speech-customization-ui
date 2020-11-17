package com.ibm.sttcustomization.model.STTModel;

import java.util.ArrayList;

public class STTModel {
    private String name;
    private String language;
    private String url;
    private Integer rate;
    Supported_Features supported_features;
    String description;

    public STTModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Supported_Features getSupported_features() {
        return supported_features;
    }

    public void setSupported_features(Supported_Features supported_features) {
        this.supported_features = supported_features;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

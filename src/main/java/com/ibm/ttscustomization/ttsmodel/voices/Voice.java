package com.ibm.ttscustomization.ttsmodel.voices;

public class Voice {
    private String name;
    private String language;
    private boolean customizable;
    private String  gender;
    private String url;
    private String description;
    QueryVoiceSupportedFeaturesReply supported_features;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public boolean isCustomizable() { return customizable; }
    public void setCustomizable(boolean customizable) { this.customizable = customizable; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public QueryVoiceSupportedFeaturesReply getSupported_features() { return supported_features; }
    public void setSupported_features(QueryVoiceSupportedFeaturesReply supported_features) { this.supported_features = supported_features; }

    public Voice() {
    }

}

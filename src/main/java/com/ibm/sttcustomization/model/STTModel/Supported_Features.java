package com.ibm.sttcustomization.model.STTModel;

public class Supported_Features {
    boolean custom_language_model;
    boolean speaker_labels;

    public boolean isCustom_language_model() {
        return custom_language_model;
    }

    public void setCustom_language_model(boolean custom_language_model) {
        this.custom_language_model = custom_language_model;
    }

    public boolean isSpeaker_labels() {
        return speaker_labels;
    }

    public void setSpeaker_labels(boolean speaker_labels) {
        this.speaker_labels = speaker_labels;
    }
}

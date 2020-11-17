package com.ibm.sttcustomization.model.voices;

public class QueryVoiceSupportedFeaturesReply {
    boolean voice_transformation;
    boolean custom_pronunciation;

    public boolean isVoice_transformation() { return voice_transformation; }
    public void setVoice_transformation(boolean voice_transformation) { this.voice_transformation = voice_transformation; }
    public boolean isCustom_pronunciation() { return custom_pronunciation; }
    public void setCustom_pronunciation(boolean custom_pronunciation) { this.custom_pronunciation = custom_pronunciation; }
}

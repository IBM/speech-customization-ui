package com.ibm.sttcustomization.model.STTModel;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class STTModelsReply {
    ArrayList<STTModel> models;

    public STTModelsReply() {
    }

    public ArrayList<STTModel> getModels() {
        return models;
    }

    public void setModels(ArrayList<STTModel> models) {
        this.models = models;
    }

    public SortedSet<String> getLanguagesAllowingCustomization()  {
        SortedSet<String> ssRet = new TreeSet<String>();
        for (STTModel v : models) {
            if (v.supported_features.isCustom_language_model())
                ssRet.add(v.getLanguage());
        }
        return ssRet;
    }

    public SortedSet<String> getModelsAllowingCustomization()  {
        SortedSet<String> ssRet = new TreeSet<String>();
        for (STTModel v : models) {
            if (v.supported_features.isCustom_language_model())
                ssRet.add(v.getName());
        }
        return ssRet;
    }

}

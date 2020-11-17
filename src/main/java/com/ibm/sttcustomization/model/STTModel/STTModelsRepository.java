package com.ibm.sttcustomization.model.STTModel;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class STTModelsRepository {
    ArrayList<STTModel> models = new ArrayList<>();

    public void  loadModels(RestClient restClient) throws GenericException {
        STTModelsReply queryModelsReply = restClient.querySTTModels();
        models = queryModelsReply.getModels();
    }

    public SortedSet<String> getLanguagesAllowingCustomization()  {
        SortedSet<String> ssRet = new TreeSet<String>();
        for (STTModel v : models) {
            if (v.supported_features.isCustom_language_model())
                ssRet.add(v.getLanguage());
        }
        return ssRet;
    }

    public STTModel getAnyVoiceForLanguage(String sLanguage) {
        for (STTModel v : models) {
            if (v.getLanguage().equalsIgnoreCase(sLanguage))
                return v;
        }
        return null;
    }


}

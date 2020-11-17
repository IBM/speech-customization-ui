package com.ibm.ttscustomization.ttsmodel.voices;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.ttscustomization.ttsmodel.RestClientTTS;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TTSVoiceRepository {
    ArrayList<Voice> voices = new ArrayList<>();

    public void  loadVoices(RestClientTTS restClient) throws GenericException {
        QueryVoicesReply queryVoicesReply = restClient.queryVoices();
        voices = queryVoicesReply.getVoices();
    }

    public SortedSet<String> getLanguagesAllowingCustomization()  {
        SortedSet<String> ssRet = new TreeSet<String>();
        for (Voice v : voices) {
            if (v.isCustomizable())
                ssRet.add(v.getLanguage());
        }
        return ssRet;
    }

    public Voice getAnyVoiceForLanguage(String sLanguage) {
        for (Voice v : voices) {
            if (v.getLanguage().equalsIgnoreCase(sLanguage))
                return v;
        }
        return null;
    }


}

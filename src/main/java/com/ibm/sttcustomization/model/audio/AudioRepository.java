package com.ibm.sttcustomization.model.audio;


import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class AudioRepository {
    ArrayList<Audio> audio = new ArrayList<>();

    public ArrayList<Audio> getAudio() {
        return audio;
    }

    public void setAudio(ArrayList<Audio> audio) {
        this.audio = audio;
    }

    public void addAudio(Audio audio) {
        this.audio.add(audio);
    }

    public void clear() {
        audio.clear();
    }

    public List<Audio> findAll() {
        return audio;
    }

    public void loadAudio(RestClient restClient, String customization_id, String sContainer) throws GenericException {
        QueryAudioReply queryAudioReply = null;
        queryAudioReply = restClient.queryAudioByAMCustomization(customization_id, sContainer);
        audio = queryAudioReply.getAudio();
    }

    public List<Audio> findByNameContains(String s) {
        ArrayList<Audio> arrRet = new ArrayList<Audio>();
        for (Audio word : audio) {
            if (word.getName().contains(s))
                arrRet.add(word);
        }
        return arrRet;
    }

}

package com.ibm.sttcustomization.model.audio;

import java.util.ArrayList;

public class QueryAudioReply {
    double total_minutes_of_audio;
    ArrayList<Audio> audio;

    public QueryAudioReply() {
    }

    public ArrayList<Audio> getAudio() {
        return audio;
    }

    public void setAudio(ArrayList<Audio> audio) {
        this.audio = audio;
    }

    public double getTotal_minutes_of_audio() {
        return total_minutes_of_audio;
    }

    public void setTotal_minutes_of_audio(double total_minutes_of_audio) {
        this.total_minutes_of_audio = total_minutes_of_audio;
    }
}

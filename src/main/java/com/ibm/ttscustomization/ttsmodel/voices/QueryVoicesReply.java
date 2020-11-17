package com.ibm.ttscustomization.ttsmodel.voices;

import java.util.ArrayList;

public class QueryVoicesReply {
    ArrayList<Voice> voices;
    public QueryVoicesReply() {
    }

    public ArrayList<Voice> getVoices() { return voices; }
    public void setVoices(ArrayList<Voice> voices) { this.voices = voices; }
}

package com.ibm.ttscustomization.ttsmodel.customizations;

import java.util.ArrayList;

public class QueryCustomVoicesReply {
    ArrayList<TTSCustomization> customizations;
    public QueryCustomVoicesReply() {
    }

    public ArrayList<TTSCustomization> getCustomizations() { return customizations; }
    public void setCustomizations(ArrayList<TTSCustomization> customizations) { this.customizations = customizations; }
}

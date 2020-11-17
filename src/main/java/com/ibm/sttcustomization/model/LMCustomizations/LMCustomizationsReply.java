package com.ibm.sttcustomization.model.LMCustomizations;

import com.ibm.sttcustomization.model.words.Word;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class LMCustomizationsReply {
    ArrayList<LMCustomization> customizations;

    public LMCustomizationsReply() {
    }

    public ArrayList<LMCustomization> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(ArrayList<LMCustomization> customizations) {
        this.customizations = customizations;
    }
}

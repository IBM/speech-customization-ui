package com.ibm.sttcustomization.model.AMCustomizations;


import java.util.ArrayList;

public class AMCustomizationsReply {
    ArrayList<AMCustomization> customizations;

    public AMCustomizationsReply() {
    }

    public ArrayList<AMCustomization> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(ArrayList<AMCustomization> customizations) {
        this.customizations = customizations;
    }
}

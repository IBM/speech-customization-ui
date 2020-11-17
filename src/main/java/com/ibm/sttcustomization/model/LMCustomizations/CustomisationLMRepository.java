package com.ibm.sttcustomization.model.LMCustomizations;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomisationLMRepository {
    ArrayList<LMCustomization> customizations = new ArrayList<>();

    public void addCustomization(LMCustomization lmcustomization) {
        customizations.add(lmcustomization);
    }

    public void clear() {
        customizations.clear();
    }

    public List<LMCustomization> findByCustomizationNameContains(String prefix) {
        ArrayList<LMCustomization> arrRet = new ArrayList<LMCustomization>();
        for (LMCustomization lmcustomization : customizations) {
            if (lmcustomization.getName().contains(prefix))
                arrRet.add(lmcustomization);
        }
        return arrRet;
    }

    public List<LMCustomization> findByCustomizationIdContains(String prefix) {
        ArrayList<LMCustomization> arrRet = new ArrayList<LMCustomization>();
        for (LMCustomization customization : customizations) {
            if (customization.getCustomization_id().contains(prefix))
                arrRet.add(customization);
        }
        return arrRet;
    }

    public List<LMCustomization>  findByCustomizationLanguageContains(String prefix) {
        ArrayList<LMCustomization> arrRet = new ArrayList<LMCustomization>();
        for (LMCustomization lmcustomization : customizations) {
            if (lmcustomization.getLanguage().contains(prefix))
                arrRet.add(lmcustomization);
        }
        return arrRet;
    }

    public List<LMCustomization>  findByStatusContains(String filterText) {
        ArrayList<LMCustomization> arrRet = new ArrayList<LMCustomization>();
        for (LMCustomization lmcustomization : customizations) {
            if (lmcustomization.getStatus().contains(filterText))
                arrRet.add(lmcustomization);
        }
        return arrRet;
    }

    public List<LMCustomization> findAll() {
        return customizations;
    }


    public LMCustomization findOne(String customization_id) {
        for (LMCustomization lmcustomization : customizations) {
            if (customization_id.equals(lmcustomization.getCustomization_id()))
                return lmcustomization;
        }
        return null;
    }


    public void delete(LMCustomization c) {
        customizations.remove(c);
    }


    public void loadCustomizations(RestClient restClient) throws GenericException {
        LMCustomizationsReply queryCustomVoicesReply = restClient.queryLMCustomizations(null);
        //report on e441067d-b2c6-495c-8d37-7734fdcd677a
        customizations = queryCustomVoicesReply.getCustomizations();
//        LMCustomization  lmCustomization = findOne("e441067d-b2c6-495c-8d37-7734fdcd677a");
//        if (lmCustomization != null) {
//            System.out.println("################ " + lmCustomization.getStatus());
//        }
    }

}

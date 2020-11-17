package com.ibm.sttcustomization.model.AMCustomizations;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.model.RestClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomisationAMRepository {
    ArrayList<AMCustomization> customizations = new ArrayList<>();

    public void addCustomization(AMCustomization amcustomization) {
        customizations.add(amcustomization);
    }

    public void clear() {
        customizations.clear();
    }

    public List<AMCustomization> findByCustomizationNameContains(String prefix) {
        ArrayList<AMCustomization> arrRet = new ArrayList<AMCustomization>();
        for (AMCustomization lmcustomization : customizations) {
            if (lmcustomization.getName().contains(prefix))
                arrRet.add(lmcustomization);
        }
        return arrRet;
    }

    public List<AMCustomization> findByCustomizationIdContains(String prefix) {
        ArrayList<AMCustomization> arrRet = new ArrayList<AMCustomization>();
        for (AMCustomization customization : customizations) {
            if (customization.getCustomization_id().contains(prefix))
                arrRet.add(customization);
        }
        return arrRet;
    }

    public List<AMCustomization>  findByCustomizationLanguageContains(String prefix) {
        ArrayList<AMCustomization> arrRet = new ArrayList<AMCustomization>();
        for (AMCustomization lmcustomization : customizations) {
            if (lmcustomization.getLanguage().contains(prefix))
                arrRet.add(lmcustomization);
        }
        return arrRet;
    }

    public List<AMCustomization>  findByStatusContains(String filterText) {
        ArrayList<AMCustomization> arrRet = new ArrayList<AMCustomization>();
        for (AMCustomization lmcustomization : customizations) {
            if (lmcustomization.getStatus().contains(filterText))
                arrRet.add(lmcustomization);
        }
        return arrRet;
    }

    public List<AMCustomization> findAll() {
        return customizations;
    }


    public AMCustomization findOne(String customization_id) {
        for (AMCustomization lmcustomization : customizations) {
            if (customization_id.equals(lmcustomization.getCustomization_id()))
                return lmcustomization;
        }
        return null;
    }


    public void delete(AMCustomization c) {
        customizations.remove(c);
    }


    public void loadCustomizations(RestClient restClient) throws GenericException {
        AMCustomizationsReply queryCustomVoicesReply = restClient.queryAMCustomizations(null);
        //report on e441067d-b2c6-495c-8d37-7734fdcd677a
        customizations = queryCustomVoicesReply.getCustomizations();
//        LMCustomization  lmCustomization = findOne("e441067d-b2c6-495c-8d37-7734fdcd677a");
//        if (lmCustomization != null) {
//            System.out.println("################ " + lmCustomization.getStatus());
//        }
    }

}

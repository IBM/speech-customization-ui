package com.ibm.ttscustomization.ttsmodel.customizations;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.ttscustomization.ttsmodel.RestClientTTS;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TTSCustomisationRepository {
    ArrayList<TTSCustomization> customizations = new ArrayList<>();

    public void addCustomization(TTSCustomization customization) {
        customizations.add(customization);
    }

    public void clear() {
        customizations.clear();
    }

    public List<TTSCustomization> findByCustomizationNameContains(String prefix) {
        ArrayList<TTSCustomization> arrRet = new ArrayList<TTSCustomization>();
        for (TTSCustomization customization : customizations) {
            if (customization.getName().contains(prefix))
                arrRet.add(customization);
        }
        return arrRet;
    }

    public List<TTSCustomization> findByCustomizationIdContains(String prefix) {
        ArrayList<TTSCustomization> arrRet = new ArrayList<TTSCustomization>();
        for (TTSCustomization customization : customizations) {
            if (customization.getCustomization_id().contains(prefix))
                arrRet.add(customization);
        }
        return arrRet;
    }

    public List<TTSCustomization>  findByCustomizationLanguageContains(String prefix) {
        ArrayList<TTSCustomization> arrRet = new ArrayList<TTSCustomization>();
        for (TTSCustomization customization : customizations) {
            if (customization.getLanguage().contains(prefix))
                arrRet.add(customization);
        }
        return arrRet;
    }

    public List<TTSCustomization> findAll() {
        return customizations;
    }

    public TTSCustomization findOne(Long id) {
        for (TTSCustomization customization : customizations) {
            if (customization.getRuntimeid() == id)
                return customization;
        }
        return null;
    }

    public TTSCustomization findOne(String customization_id) {
        for (TTSCustomization customization : customizations) {
            if (customization_id.equals(customization.getCustomization_id()))
                return customization;
        }
        return null;
    }


    public void delete(TTSCustomization c) {
        customizations.remove(c);
    }


    public void loadCustomizations(RestClientTTS restClient) throws GenericException {
        QueryCustomVoicesReply queryCustomVoicesReply = restClient.queryCustomVoices();
        customizations = queryCustomVoicesReply.getCustomizations();
    }

}

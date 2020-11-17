package com.ibm.ttscustomization;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.sttcustomization.utils.Utils;
import com.ibm.ttscustomization.ttsmodel.RestClientTTS;
import com.ibm.ttscustomization.ttsmodel.customizations.TTSCustomization;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.StreamResource;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;

public class TTSUtils {
    public static InputStream getAudioStream(String sWordOrTranslation, RestClientTTS restClient, TTSCustomization ttsCustomization) {
        try {
            String sVoice = Utils.getVoiceRepository().getAnyVoiceForLanguage(ttsCustomization.getLanguage()).getName();
            JSONObject jso = new JSONObject();
            jso.put("text", sWordOrTranslation);
            byte[] ab = restClient.queryAudio(jso, ttsCustomization.getCustomization_id(), sVoice);
            if (true)
                return new ByteArrayInputStream(ab);
            return null;
        } catch (GenericException genericException) {
            Utils.displayErrorMessage(genericException);
        }
        return null;
    }

    public static void playWord(String sWordOrTranslation, RestClientTTS restClient, TTSCustomization ttsCustomization) {
        StreamResource resource = new StreamResource("audio.wav",
                () -> TTSUtils.getAudioStream(sWordOrTranslation, restClient, ttsCustomization));
        Element element = new Element("audio");
        element.setAttribute("type", "audio/wav");
        element.setAttribute("autoplay", true);
        element.getStyle().set("display", "block");
        element.setAttribute("src", resource);
        UI.getCurrent().getElement().appendChild(element);
    }

    public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            return doc;
        } catch (Exception e) {
        }
        return null;
    }

}

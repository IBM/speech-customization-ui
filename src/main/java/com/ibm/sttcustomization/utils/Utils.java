package com.ibm.sttcustomization.utils;

import com.ibm.ttscustomization.ttsmodel.voices.TTSVoiceRepository;
import com.ibm.ttscustomization.ttsui.MainTTS;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.text.diff.EditScript;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import org.apache.commons.text.diff.*;
import org.apache.commons.text.diff.CommandVisitor;

import static com.ibm.sttcustomization.utils.Constants.COLOR_DELETED_IN_COMPARED_TEXT;
import static com.ibm.sttcustomization.utils.Constants.COLOR_INSERTED_IN_COMPARED_TEXT;

public class Utils {

//    public static class LongToStringConverter implements ModelEncoder<Long, String> {
//
//        @Override
//        public String encode(Long modelValue) {
//            return Optional.ofNullable(modelValue).map(Object::toString)
//                    .orElse(null);
//        }
//
//        @Override
//        public Long decode(String presentationValue) {
//            return Optional.ofNullable(presentationValue).map(Long::parseLong)
//                    .orElse(null);
//
//        }
//
//    }

    public static <E> List<E> toList(Iterable<E> iterable) {
        if(iterable instanceof List) {
            return (List<E>) iterable;
        }
        ArrayList<E> list = new ArrayList<E>();
        if(iterable != null) {
            for(E e: iterable) {
                list.add(e);
            }
        }
        return list;
    }

//    public static RestClient getRestClient() {
//        RestClient restClient = ((GatewayUI) UI.getCurrent()).getMainView().getRestClient();
//        return restClient;
//    }
//
    public static TTSVoiceRepository getVoiceRepository() {
        MainTTS maintts = (MainTTS)VaadinSession.getCurrent().getAttribute("maintts");
        TTSVoiceRepository voiceRepository = maintts.getVoiceRepository();
        return voiceRepository;
    }

//    public static void customizationUpdatedSuccessfully(Customization customization) {
//        ((GatewayUI) UI.getCurrent()).getMainView().customizationUpdatedSuccessfully(customization);
//    }
//
//    public static void customizationDeletedSuccessfully(Customization customization) {
//        ((GatewayUI) UI.getCurrent()).getMainView().customizationDeletedSuccessfully(customization);
//    }
//
//    public static void customizationCreatedSuccessfully(Customization customization) {
//        ((GatewayUI) UI.getCurrent()).getMainView().customizationCreatedSuccessfully(customization);
//    }

    public static void displayErrorMessage(Throwable e) {
//       Notification.show(e.getMessage(), 5000, Notification.Position.MIDDLE);
        Label content = new Label(e.getMessage());
        Button buttonInside = new Button("Close");
        Notification notification = new Notification(content, buttonInside);
        buttonInside.addClickListener(event -> notification.close());
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();

        e.printStackTrace();
    }

    public static String fixedLengthString(String string, int length) {
        String sRet = String.format("%1$"+length+ "s", string);
        sRet = sRet.replace(" ", "&nbsp;");
        return sRet;
    }


    public static String extractTranscript(String sSpeechRecoResults) {
        JSONObject jsoTranscript = new JSONObject(sSpeechRecoResults);
        JSONArray jsaResults = jsoTranscript.getJSONArray("results");

        String sTranscript = "";
        for (int i = 0; i < jsaResults.length(); i++) {
            JSONObject jsoResult = jsaResults.getJSONObject(i);
            if (jsoResult.getBoolean("final")) {
                JSONArray jsaAlt = jsoResult.getJSONArray("alternatives");
                String transcript = jsaAlt.getJSONObject(0).getString("transcript");
                if (sTranscript.equalsIgnoreCase(""))
                    sTranscript = transcript;
                else
                    sTranscript = sTranscript + "\n" + transcript;
            }
        }
        return sTranscript;
    }


    static String t1 = "careers are LEGKO to neglect if the paychecks keep coming and the bosses tolerable most people get into a routine and direct their attention elsewhere \n" +
            "while that's again adequate weighed to put bread on the table it probably won't win you any big raises or promotions show whether you're hoping to stay at your current employer or thinking you'll move to another firm at some point here are eight job resolutions to help you jump start your career in two thousand seven create a board of advisors fighting two or three people you admire and take each one to launch a few times this year says Dale Winston chief executive of recruiting \n" +
            "careers are LEGKO to neglect if the paychecks keep coming and the bosses tolerable most people get into a routine and direct their attention elsewhere \n" +
            "while that's again adequate weighed to put bread on the table it probably won't win you any big raises or promotions show whether you're hoping to stay at your current employer or thinking you'll move to another firm at some point here are eight job resolutions to help you jump start your career in two thousand seven create a board of advisors fighting two or three people you admire and take each one to launch a few times this year says Dale Winston chief executive of recruiting ";

    static String t2 = "careers are easy to neglect if the paychecks keep coming and the boss is tolerable most people get into a routine and direct their attention elsewhere \n" +
            "while that's again adequate Wade to put bread on the table it probably won't win you any big raises or promotions so whether you're hoping to stay at your current employer or thinking you'll move to another firm at some point here are eight job resolutions to help you jump start your career in two thousand seven create a board of advisors fighting two or three people you admire and take each one to launch a few times this year says Dale Winston chief executive of recruiting \n" +
            "careers are easy to neglect if the paychecks keep coming and the boss is tolerable most people get into a routine and direct their attention elsewhere \n" +
            "while that's again adequate Wade to put bread on the table it probably won't win you any big raises or promotions so whether you're hoping to stay at your current employer or thinking you'll move to another firm at some point here are eight job resolutions to help you jump start your career in two thousand seven create a board of advisors fighting two or three people you admire and take each one to launch a few times this year says Dale Winston chief executive of recruiting ";

    public static String extractError(JSONObject jso) {
        String message = jso.toString();

        try {
            if (jso.has("error"))
                message = jso.getString("error");
            else if (jso.has("description"))
                message = jso.getString("description");
        }
        catch (Exception e) {

        }
        return message;
    }

    public static class ShowVisitor implements CommandVisitor {
        private final Element element;
        private boolean bInserting = false;
        private boolean bDeleting = false;

        public String sret = "";

        public ShowVisitor(Element element) {
            index = 0;
            this.element = element;
        }

        public void inserting(boolean b) {
            if ((b == true) && (bInserting == false)) { // if opening
                //collect("<b>");
            }
            else if ((b == false) && (bInserting == true)) { // if closure
                //collect("</b>");
            }

            bInserting = b;
        }

        public void deleting(boolean b) {
            if ((b == true) && (bDeleting == false)) { // if opening
               // collect("<strike>");
            }
            else if ((b == false) && (bDeleting == true)) { // if closure
               // collect("</strike>");
            }

            bDeleting = b;
        }

        public void visitInsertCommand(Object object) {
            index++;

            deleting(false);
            inserting(true);
            collect(object);
        }

        public void visitKeepCommand(Object object) {
            ++index;

            deleting(false);
            inserting(false);
            collect(object);
        }

        public void visitDeleteCommand(Object object) {
            inserting(false);
            deleting(true);

            collect(object);
        }

        private void collect(Object object) {
            sret = sret + object;
            //System.out.println(commandName + " " + object + " ->" + this);
            Element el;
            if(bDeleting) {
                //el = element.appendChild(ElementFactory.createStrong(object.toString()));
                Span span = new Span(object.toString());
                span.getStyle().set("background-color", COLOR_INSERTED_IN_COMPARED_TEXT);
                el = element.appendChild(span.getElement());
            }
            else if (bInserting) {
                //el = element.appendChild(ElementFactory.createEmphasis(object.toString()));
                Span span = new Span(object.toString());
                span.getStyle().set("background-color", COLOR_DELETED_IN_COMPARED_TEXT);
                el = element.appendChild(span.getElement());
            }
            else {
                el = element.appendChild((new Span(object.toString())).getElement());
            }
        }


        private int index;
    }

    public static String comparison(Element element, String sOriginal, String sModified) {
        String sret;
        StringsComparator cmp = new StringsComparator(sOriginal, sModified);
        EditScript<Character> script = cmp.getScript();
        ShowVisitor showVisitor = new ShowVisitor(element);
        script.visit(showVisitor);
        showVisitor.inserting(false);
        showVisitor.deleting(false);
        sret = showVisitor.sret;
        return sret;
    }

    public static String getExtention(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

    public static void main(String [] args) {
//        String transcript = Utils.extractTranscript(Utils.transcription);
//        String transcriptChanged = transcript.replace("or ", "ILI " );
//        StringsComparator cmp = new StringsComparator(transcript, transcriptChanged);
//        EditScript<Character> script = cmp.getScript();
//        int mod = script.getModifications();
//        ShowVisitor showVisitor = new ShowVisitor(10000);
//        script.visit(showVisitor);
////        for (int i = 0; i < script.comm; i++) {
////
////        }
//        String sCompared = comparison(null, transcript, transcriptChanged);

        //String sTranscript = extractTranscript(transcription);

        int jj = 5;


    }

    /////////////////////////////////////////////////////////////
    // TTS




}

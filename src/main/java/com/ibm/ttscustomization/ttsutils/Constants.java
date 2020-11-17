package com.ibm.ttscustomization.ttsutils;

public class Constants {
    public static class ERRORS {
        public static final String FAILED_TO_QUERY_AUDIO = "Could not query audio customization_id='%s' word='%s' voice='%s'";
        public static final String FAILED_TO_QUERY_VOICES = "Could not query voices";
        public static final String FAILED_TO_CREATE_CUSTOM_VOICE = "Could not create custom voice json='%s'";
        public static final String FAILED_TO_UPDATE_CUSTOM_VOICE = "Could not update custom voice customization_id='%s' json='%s'";
        public static final String FAILED_TO_DELETE_CUSTOM_VOICE = "Could not delete custom voice customization_id='%s' ";
        public static final String FAILED_TO_DELETE_WORd = "Could not delete word customization_id='%s' word='%s'";;
        public static final String FAILED_TO_QUERY_CUSTOM_VOICES = "Failed to query custom voices";
        public static final String FAILED_TO_QUERY_WORDS = "Could not query words customization_id='%s'";
        public static final String FAILED_TO_UPDATE_WORD = "Could not update word customization_id='%s' json='%s' ";
    }
}

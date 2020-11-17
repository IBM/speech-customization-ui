package com.ibm.sttcustomization.utils;

import java.util.Locale;

public class Constants {
    public static final String COLOR_BE_CAREFULL_BACKGROUND = "#ff8e9b";
    public static final String COLOR_BE_CAREFULL_FOREGROUND = "#c24c61";
    public static final String COLOR_REFRESH_BACKGROUND = "#2584FF";
    public static final String COLOR_REFRESH_FOREGROUND = "#ffffff";
    public static final String COLOR_DELETED_IN_COMPARED_TEXT = "#fdc4b9";
    public static final String COLOR_INSERTED_IN_COMPARED_TEXT = "#bbfcb5";


    public static class ERRORS {
        public static final String FAILED_TO_QUERY_AUDIO = "***Failed to query audio customization_id='%s' word='%s' voice='%s'";
        public static final String FAILED_TO_QUERY_VOICES = "***Failed to query voices";
        public static final String FAILED_TO_CREATE_CUSTOM_VOICE = "***Failed to create custom voice json='%s'";
        public static final String FAILED_TO_UPDATE_CUSTOM_VOICE = "***Failed to update custom voice customization_id='%s' json='%s'";
        public static final String FAILED_TO_DELETE_CUSTOM_VOICE = "***Failed to delete custom voice customization_id='%s' ";
        public static final String FAILED_TO_DELETE_WORd = "***Failed to delete word customization_id='%s' word='%s'";
        public static final String FAILED_TO_QUERY_CUSTOM_VOICES = "***Failed to query custom voices";
        public static final String FAILED_TO_QUERY_WORDS = "***Failed to  query words customization_id='%s'";
        public static final String FAILED_TO_CREATEUPDATE_WORD = "***Failed to create or update word customization_id='%s' word='%s' response='%s' ";
        public static final String FAILED_TO_QUERY_LMCUSTOMIZATIONS = "***Failed to query LM customizations";
        public static final String FAILED_TO_QUERY_AMCUSTOMIZATIONS = "***Failed to query AM customizations";
        public static final String FAILED_TO_QUERY_WORDS_BY_LMCUSTOMIZATION = "***Failed to Query Words by LMCutomization customization_id='%s' response='%s'";
        public static final String FAILED_TO_QUERY_GRAMMARS_BY_LMCUSTOMIZATION = "***Failed to Query Grammars Words by LMCutomization customization_id='%s' ";
        public static final String FAILED_TO_QUERY_CORPORA_BY_LMCUSTOMIZATION = "***Failed to Query Corpora Words by LMCutomization customization_id='%s' ";
        public static final String FAILED_TO_CREATE_LMCUSTOMIZATION = "***Failed to create LM Customization json='%s' responce='%s' ";
        public static final String FAILED_TO_RESET_LMCUSTOMIZATION = "***Failed to reset LM Customization id='%s'";
        public static final String FAILED_TO_DELETE_LMCUSTOMIZATION = "***Failed to delete LM Customization id='%s'";
        public static final String FAILED_TO_DECODE_FILE = "***Failed To Decode File customization_id='%s' response='%s' file='%s' ";
        public static final String FAILED_TO_QUERY_STTMODELS = "***Failed to query STT Models";
        public static final String FAILED_TO_UPLOAD_CORPUS = "***Failed To Upload Corpus customization_id='%s' response='%s'";
        public static final String FAILED_TO_UPLOAD_GRAMMAR = "***Failed To Upload Grammar customization_id='%s' response='%s'";
        public static final String FAILED_TO_UPLOAD_LIST_OF_WORDS = "***Failed To Upload List Of Words customization_id='%s' response='%s'";
        public static final String FAILED_TO_TRAIN_LMCUSTOMIZATION = "***Failed to train LM Customization id='%s'";
        public static final String FAILED_TO_DELETE_GRAMMAR = "***Failed To Delete Grammar customization_id='%s' grammar_name='%s' response='%s' ";
        public static final String FAILED_TO_DELETE_CORPUS = "***Failed to delete Corpus customization_id='%s' corpus_name='%s'  ";
    }
}
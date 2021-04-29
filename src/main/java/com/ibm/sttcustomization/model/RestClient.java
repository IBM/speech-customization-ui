package com.ibm.sttcustomization.model;


import com.ibm.sttcustomization.model.AMCustomizations.AMCustomization;
import com.ibm.sttcustomization.model.AMCustomizations.AMCustomizationsReply;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomization;
import com.ibm.sttcustomization.model.LMCustomizations.LMCustomizationsReply;
import com.ibm.sttcustomization.model.STTModel.STTModelsReply;
import com.ibm.sttcustomization.model.audio.QueryAudioReply;
import com.ibm.sttcustomization.model.corpora.QueryCorporaReply;
import com.ibm.sttcustomization.bak.customizations.QueryCustomVoicesReply;
import com.ibm.sttcustomization.model.grammars.QueryGrammarsReply;
import com.ibm.sttcustomization.model.voices.QueryVoicesReply;
import com.ibm.sttcustomization.model.words.QueryWordsReply;
import com.ibm.sttcustomization.model.words.Word;
import com.ibm.sttcustomization.utils.Constants;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;



public class RestClient {
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private String host = "url";
    private String user = "user";
    private String password = "password";

    public void setHost(String host) {
        this.host = host;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private RestTemplateBuilder restTemplateBuilder;

    public RestClient() {
        this.restTemplateBuilder = new RestTemplateBuilder();
    }

    private RestTemplate creatRestTemplate() {
        //restTemplate.setErrorHandler(new RestClientErrorHandler());
        RestTemplate restTemplate = restTemplateBuilder
//                .setConnectTimeout(30000)
//                .setReadTimeout(30000)
                .build();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    private HttpHeaders createHeaders() {
        String plainCreds = user + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }

    public AMCustomizationsReply queryAMCustomizations(String language) throws GenericException {
        ResponseEntity<AMCustomizationsReply> response = null;
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();
            if (language != null)
                response = restTemplate.exchange(host + "/acoustic_customizations?language=" + language, HttpMethod.GET, request, AMCustomizationsReply.class);
            else
                response = restTemplate.exchange(host + "/acoustic_customizations", HttpMethod.GET, request, AMCustomizationsReply.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) { String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_AMCUSTOMIZATIONS));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    // {"code":503,"description":"RibbonCommandSpeechToText failed and no fallback available. null com.netflix.client.ClientException: Load balancer does not have available server for client: STT_CUSTOMIZATION_SVC_20180419-202454-256 Load balancer does not have available server for client: STT_CUSTOMIZATION_SVC_20180419-202454-256 ","message":"Service Unavailable"}
    public LMCustomizationsReply queryLMCustomizations(String language) throws GenericException {
        ResponseEntity<LMCustomizationsReply> response = null;
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();
            if (language != null)
                response = restTemplate.exchange(host + "/customizations?language=" + language, HttpMethod.GET, request, LMCustomizationsReply.class);
            else
                response = restTemplate.exchange(host + "/customizations", HttpMethod.GET, request, LMCustomizationsReply.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) { String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_LMCUSTOMIZATIONS));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public STTModelsReply querySTTModels() throws GenericException {
        ResponseEntity<STTModelsReply> response = null;
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();
            response = restTemplate.exchange(host + "/models", HttpMethod.GET, request, STTModelsReply.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_STTMODELS));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String createAMCustomization(AMCustomization customization) throws GenericException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", customization.getName());
        jsonObject.put("description", customization.getDescription());
//        if (!customization.getDialect().equalsIgnoreCase(""))
//            jsonObject.put("dialect", customization.getDialect());
        jsonObject.put("base_model_name", customization.getBase_model_name());

        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/acoustic_customizations", HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_CREATE_LMCUSTOMIZATION, jsonObject.toString(), responseString));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String createLMCustomization(LMCustomization lmCustomizationBeingEdited) throws GenericException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", lmCustomizationBeingEdited.getName());
        jsonObject.put("description", lmCustomizationBeingEdited.getDescription());
        if (!lmCustomizationBeingEdited.getDialect().equalsIgnoreCase(""))
            jsonObject.put("dialect", lmCustomizationBeingEdited.getDialect());
        jsonObject.put("base_model_name", lmCustomizationBeingEdited.getBase_model_name());

        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations", HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_CREATE_LMCUSTOMIZATION, jsonObject.toString(), responseString));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String uploadAudio(byte[] bytearrayContent, String customization_id, String name, String sContentType) throws GenericException {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.valueOf(sContentType));
            HttpEntity<byte[]> entity = new HttpEntity<>(bytearrayContent, headers);
            RestTemplate restTemplate = creatRestTemplate();
            response = restTemplate.postForEntity(host + "/acoustic_customizations/" + customization_id + "/audio/" + name , entity, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_UPLOAD_CORPUS, customization_id, responseString));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String uploadCorpus( byte[] bytearrayCorpusContent, String customization_id, String corpusName) throws GenericException {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.valueOf("text/plain"));
            HttpEntity<byte[]> entity = new HttpEntity<>(bytearrayCorpusContent, headers);
            RestTemplate restTemplate = creatRestTemplate();
            response = restTemplate.postForEntity(host + "/customizations/" + customization_id + "/corpora/" + corpusName , entity, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_UPLOAD_CORPUS, customization_id, responseString));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String uploadGrammar(byte[] byteArrayGrammarContent, String customization_id, String grammarName, String grammarContentType) throws GenericException {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.valueOf(grammarContentType));
            HttpEntity<byte[]> entity = new HttpEntity<>(byteArrayGrammarContent, headers);
            RestTemplate restTemplate = creatRestTemplate();
            response = restTemplate.postForEntity(host + "/customizations/" + customization_id + "/grammars/" + grammarName , entity, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_UPLOAD_GRAMMAR, customization_id, responseString));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String uploadListOfWords(byte[] byteArrayListOfWordsContent, String customization_id) throws GenericException {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.valueOf("application/json"));
            HttpEntity<byte[]> entity = new HttpEntity<>(byteArrayListOfWordsContent, headers);
            RestTemplate restTemplate = creatRestTemplate();
            response = restTemplate.postForEntity(host + "/customizations/" + customization_id + "/words" , entity, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_UPLOAD_LIST_OF_WORDS, customization_id, responseString));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String resetAMCustomization(AMCustomization customization) throws GenericException {
        JSONObject jsonObject = new JSONObject();
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/acoustic_customizations/"  + customization.getCustomization_id() + "/reset", HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_RESET_LMCUSTOMIZATION, customization.getCustomization_id()));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String resetLMCustomization(LMCustomization lmCustomization) throws GenericException {
        JSONObject jsonObject = new JSONObject();
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations/"  + lmCustomization.getCustomization_id() + "/reset", HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_RESET_LMCUSTOMIZATION, lmCustomization.getCustomization_id()));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String trainAMCustomization(AMCustomization customization, boolean bForce, boolean bClean, String custom_language_model_id) throws GenericException {
        // queryParametersValidator.add("word_type_to_add", "all", "user");
        JSONObject jsonObject = new JSONObject();
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
            RestTemplate restTemplate = creatRestTemplate();
            String url = host + "/acoustic_customizations/"  + customization.getCustomization_id() + "/train?" + "force=" + bForce + "&clean=" + bClean;
            if (custom_language_model_id != null)
                url = url + "&" + "custom_language_model_id=" + custom_language_model_id;
            ResponseEntity<String> response = restTemplate.exchange(url,
                    HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_TRAIN_LMCUSTOMIZATION, customization.getCustomization_id()));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String trainLMCustomization(LMCustomization lmCustomization, String word_type_to_add) throws GenericException {
        // queryParametersValidator.add("word_type_to_add", "all", "user");
        JSONObject jsonObject = new JSONObject();
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations/"  + lmCustomization.getCustomization_id() + "/train?" +
                    "word_type_to_add=" + word_type_to_add,
                    HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_TRAIN_LMCUSTOMIZATION, lmCustomization.getCustomization_id()));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }


    public String deleteAMCustomization(AMCustomization customization) throws GenericException {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/acoustic_customizations/" + customization.getCustomization_id(), HttpMethod.DELETE, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_DELETE_LMCUSTOMIZATION, customization.getCustomization_id()));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String deleteLMCustomization(LMCustomization lmCustomizationBeingEdited) throws GenericException {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations/" + lmCustomizationBeingEdited.getCustomization_id(), HttpMethod.DELETE, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_DELETE_LMCUSTOMIZATION, lmCustomizationBeingEdited.getCustomization_id()));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String deleteCorpus(String customization_id, String corpus_name) throws GenericException {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations/" + customization_id + "/corpora/" + corpus_name, HttpMethod.DELETE, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_DELETE_CORPUS, customization_id, corpus_name));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String deleteGrammar(String customization_id, String grammar_name) throws GenericException {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations/" + customization_id + "/grammars/" + grammar_name, HttpMethod.DELETE, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_DELETE_GRAMMAR, customization_id, grammar_name, responseString));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    private String speechHubHost = "http://localhost:9090/transcribe";

    public String recognizeLMSpeechHub(InputStream inputStream, String customizationId, String sBaseModel, String sFileName) throws GenericException {
        try {
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        ByteArrayResource resource = null;
            //resource1 = new ByteArrayResource(IOUtils.toByteArray(inputStream));
            resource = new ByteArrayResource(IOUtils.toByteArray(inputStream)) {
                @Override
                public String getFilename() {
                    return sFileName;
                }
            };
            data.add("file", resource);
            data.add("stt_host", host);
            HttpHeaders requestHeaders = createHeaders();
            requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(data, requestHeaders);

            String url;
            if (customizationId != null)
                url = speechHubHost + "?customization_id=" + customizationId + "&model=" + sBaseModel;
            else
                url = speechHubHost + "?model=" + sBaseModel;

            RestTemplate restTemplate = creatRestTemplate();
            final ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<String>(){});
            return responseEntity.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }


    public String recognizeAM(InputStream inputStream, String customizationId, String sBaseModel, String sFileName) throws GenericException {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.valueOf("audio/wav"));
            HttpEntity<byte[]> entity = new HttpEntity(IOUtils.toByteArray(inputStream), headers);
            RestTemplate restTemplate = creatRestTemplate();

            //"Content-Type: audio/flac"
            //restTemplate.postForEntity(host + "/customizations/" + customizationId + "/words", entity, String.class);
            //https://stream-s.watsonplatform.net/speech-to-text/api/v1
            if (customizationId != null)
                response = restTemplate.postForEntity(host + "/recognize?timestamps=true&acoustic_customization_id=" + customizationId + "&model=" + sBaseModel, entity, String.class);
            else
                response = restTemplate.postForEntity(host + "/recognize?timestamps=true&model=" + sBaseModel, entity, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_DECODE_FILE, customizationId, responseString, sFileName));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), e, GenericException.ErrorCode.GENERAL);
        }
    }


    public String recognizeLM(InputStream inputStream, String customizationId, String sBaseModel, String sFileName) throws GenericException {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.valueOf("audio/wav"));
            HttpEntity<byte[]> entity = new HttpEntity(IOUtils.toByteArray(inputStream), headers);
            RestTemplate restTemplate = creatRestTemplate();

            //"Content-Type: audio/flac"
            //restTemplate.postForEntity(host + "/customizations/" + customizationId + "/words", entity, String.class);
            //https://stream-s.watsonplatform.net/speech-to-text/api/v1
            if (customizationId != null)
                response = restTemplate.postForEntity(host + "/recognize?timestamps=true&customization_id=" + customizationId + "&model=" + sBaseModel, entity, String.class);
            else
                response = restTemplate.postForEntity(host + "/recognize?timestamps=true&model=" + sBaseModel, entity, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_DECODE_FILE, customizationId, responseString, sFileName));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), e, GenericException.ErrorCode.GENERAL);
        }
    }

    public QueryAudioReply queryAudioByAMCustomization(String customizationId, String sContainer) throws GenericException {
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();
            String url = host + "/acoustic_customizations/" + customizationId + "/audio";
            if (sContainer != null)
                url = url + "/" + sContainer;
            ResponseEntity<QueryAudioReply> response = restTemplate.exchange(url, HttpMethod.GET, request, QueryAudioReply.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_WORDS_BY_LMCUSTOMIZATION, customizationId, responseString));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public QueryWordsReply queryWordsByLMCustomization(String customizationId) throws GenericException {
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<QueryWordsReply> response = restTemplate.exchange(host + "/customizations/" + customizationId + "/words", HttpMethod.GET, request, QueryWordsReply.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_WORDS_BY_LMCUSTOMIZATION, customizationId, responseString));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public QueryGrammarsReply queryGrammarsByLMCustomization(String customizationId) throws GenericException {
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<QueryGrammarsReply> response = restTemplate.exchange(host + "/customizations/" + customizationId + "/grammars", HttpMethod.GET, request, QueryGrammarsReply.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_GRAMMARS_BY_LMCUSTOMIZATION, customizationId));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public QueryCorporaReply queryCorporaByLMCustomization(String customizationId) throws GenericException {
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<QueryCorporaReply> response = restTemplate.exchange(host + "/customizations/" + customizationId + "/corpora", HttpMethod.GET, request, QueryCorporaReply.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_CORPORA_BY_LMCUSTOMIZATION, customizationId));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String createOrupdateWord(Word word, String customization_id) throws GenericException {
        JSONObject jsoWord = new JSONObject();
        jsoWord.put("display_as", word.getDisplay_as());
        jsoWord.put("sounds_like", word.getSounds_like());
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsoWord.toString(), headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations/" + customization_id + "/words/" + word.getWord(), HttpMethod.PUT, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_CREATEUPDATE_WORD, customization_id, jsoWord, jso));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public byte[] queryAudio(JSONObject jo, String customization_id, String voice) throws GenericException {
        try {
            //HttpEntity<String> request = new HttpEntity<>(createHeaders());
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jo.toString(), headers);
            RestTemplate restTemplate = creatRestTemplate();

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(host + "/synthesize")
//                    .queryParam("text", word)
                    .queryParam("accept", "audio/wav")
                    .queryParam("customization_id", customization_id)
                    .queryParam("voice", voice);

            URI uri = builder.buildAndExpand().toUri();
            ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.POST, request, byte[].class);

            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_AUDIO, customization_id, jso.toString(), voice));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public byte[] queryAudio(String word, String customization_id, String voice) throws GenericException {
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(host + "/synthesize")
                    .queryParam("lastName", "Watney")
                    .queryParam("text", word)
                    .queryParam("accept", "audio/wav")
                    .queryParam("customization_id", customization_id)
                    .queryParam("voice", voice);

            URI uri = builder.buildAndExpand().toUri();
            ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, byte[].class);

            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_AUDIO, customization_id, word, voice));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public QueryVoicesReply queryVoices() throws GenericException {
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<QueryVoicesReply> response = restTemplate.exchange(host + "/voices", HttpMethod.GET, request, QueryVoicesReply.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_VOICES));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String createCustomVoice(JSONObject jsonObject) throws GenericException {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations", HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_CREATE_CUSTOM_VOICE, jsonObject.toString()));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String updateCustomVoice(JSONObject jsonObject, String customization_id) throws GenericException {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations/" + customization_id, HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_UPDATE_CUSTOM_VOICE, customization_id, jsonObject.toString()));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String deleteCustomVoice(String customization_id) throws GenericException {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations/" + customization_id, HttpMethod.DELETE, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_DELETE_CUSTOM_VOICE, customization_id));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String deleteAudio(String customization_id, String name) throws GenericException {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = creatRestTemplate();
            String url = "";
            url = host + "/acoustic_customizations/" + customization_id + "/audio/" + name;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
//            ResponseEntity<String> response = restTemplate.exchange(host + "/acoustic_customizations/" + customization_id + "/audio/" + name, HttpMethod.DELETE, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_DELETE_WORd, customization_id, name));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String deleteWord(String customization_id, String word) throws GenericException {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(host + "/customizations/" + customization_id + "/words/" + word, HttpMethod.DELETE, request, String.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_DELETE_WORd, customization_id, word));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    //-------------------------- used also for validation -----------------------
    public QueryCustomVoicesReply queryCustomVoices() throws GenericException {
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<QueryCustomVoicesReply> response = restTemplate.exchange(host + "/customizations", HttpMethod.GET, request, QueryCustomVoicesReply.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_CUSTOM_VOICES));
            String errorMessage = String.format(Constants.ERRORS.FAILED_TO_QUERY_CUSTOM_VOICES);
            if (jso.has("error"))
                errorMessage = jso.getString("error");
            if (jso.has("description"))
                errorMessage = errorMessage + ", " +  jso.getString("description");

            throw new GenericException(errorMessage, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    // on the server's side:
    //@Path("/customizations/{customization_id}")
    public QueryWordsReply queryWords(String customization_id) throws GenericException {
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            RestTemplate restTemplate = creatRestTemplate();
            ResponseEntity<QueryWordsReply> response = restTemplate.exchange(host + "/customizations/" + customization_id, HttpMethod.GET, request, QueryWordsReply.class);
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            String responseString = httpStatusCodeException.getResponseBodyAsString();
            JSONObject jso = new JSONObject((responseString));
            logger.error(String.format(Constants.ERRORS.FAILED_TO_QUERY_WORDS, customization_id));
            throw new GenericException(jso, httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }


    public static void main(String [] args) {
        RestClient restClient = new RestClient();
        LMCustomizationsReply lmCustomizationsReply = null;
        try {
            lmCustomizationsReply =  restClient.queryLMCustomizations("en-US");
        } catch (GenericException e) {
            e.printStackTrace();
        }

        int jj = 5;


    }



}

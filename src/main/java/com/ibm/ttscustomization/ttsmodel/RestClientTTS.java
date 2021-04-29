package com.ibm.ttscustomization.ttsmodel;

import com.ibm.sttcustomization.model.GenericException;
import com.ibm.ttscustomization.ttsmodel.customizations.QueryCustomVoicesReply;
import com.ibm.ttscustomization.ttsmodel.voices.QueryVoicesReply;
import com.ibm.ttscustomization.ttsmodel.words.QueryWordsReply;
import com.ibm.ttscustomization.ttsutils.Constants;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;

public class RestClientTTS {
    private static final Logger logger = LoggerFactory.getLogger(RestClientTTS.class);

    private String host = "URL";
    private String user = "login";
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

    public RestClientTTS() {
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
            throw new GenericException(jso.getString("error"), httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
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
            throw new GenericException(jso.getString("error"), httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
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
            throw new GenericException(jso.getString("error"), httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
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
            throw new GenericException(jso.getString("error"), httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
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
            throw new GenericException(jso.getString("error"), httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
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
            throw new GenericException(jso.getString("error"), httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
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
            throw new GenericException(jso.getString("error"), httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
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
            throw new GenericException(jso.getString("error"), httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }

    public String updateWord(JSONObject jsonObject, String customization_id) throws GenericException {
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
            logger.error(String.format(Constants.ERRORS.FAILED_TO_UPDATE_WORD, customization_id, jsonObject));
            throw new GenericException(jso.getString("error"), httpStatusCodeException, GenericException.ErrorCode.HTTPSTATUSCODEEXCEPTION);
        }
    }




}

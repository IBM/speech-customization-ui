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

// GetVoices
//curl -k -v -X GET -u "1caa1631-0ff4-44d9-ae8c-f1a8731947f0:MJlT5vRDL6Xw" "https://stream-d.watsonplatform.net/text-to-speech/api/v1/voices"

// GetWords (Keerthana pstg)
// curl -X get -v -u bdb86865-60a4-4e42-bfa8-4c91dfd583d2:L3MIsuh4AGpz " https://stream-s.watsonplatform.net/text-to-speech/api/v1/customizations/71f886d1-4276-4a32-ba81-cf477d8ff1f5"

// GetCustomizations (Keerthana pstg)
//curl -k -v -X GET -u bdb86865-60a4-4e42-bfa8-4c91dfd583d2:L3MIsuh4AGpz "https://stream-s.watsonplatform.net/text-to-speech/api/v1/customizations"

// works
// curl -X GET  -u "bdb86865-60a4-4e42-bfa8-4c91dfd583d2:L3MIsuh4AGpz" --output hello_world.wav "https://stream-s.watsonplatform.net/text-to-speech/api/v1/synthesize?accept=audio/wav&text=Hello%20world&voice=en-US_AllisonVoice&customization_id=71f886d1-4276-4a32-ba81-cf477d8ff1f5"


// GetCustomizations
//curl -k -v -X GET -u "1caa1631-0ff4-44d9-ae8c-f1a8731947f0:MJlT5vRDL6Xw" "https://stream-d.watsonplatform.net/text-to-speech/api/v1/customizations"

//../text-to-speech/api/v1/pronunciation?text=IEEE&voice=es-LA_SofiaVoice&format=ipa&customization_id=3c2b4878-9690-4df9-86b2-cd5fb440b12a
//where cust voice id=3c2b4878-9690-4df9-86b2-cd5fb440b12a
//voice name=es-LA_SofiaVoice
//phoneme format=ipa
//the expected output is
//`{
//  "pronunciation": ".ˈi .ˈtɾi.ple .e"
//}`
//assuming that the custom voice (id=3c2b4878-9690-4df9-86b2-cd5fb440b12a) has IEEE the pronunciation defined as "I tripe E"
//otherwise, the output will be
//`{
//  "pronunciation": ".ˈi .ˈe .ˈe .ˈe"}`
//

/*
.../text-to-speech/api/v1/pronunciation?text=IEEE&voice=es-LA_SofiaVoice&format=ipa&customization_id=3c2b4878-9690-4df9-86b2-cd5fb440b12a
where cust voice id=3c2b4878-9690-4df9-86b2-cd5fb440b12a
voice name=es-LA_SofiaVoice
phoneme format=ipa
the expected output is
`{
 "pronunciation": ".ˈi .ˈtɾi.ple .e"
}`
assuming that the custom voice (id=3c2b4878-9690-4df9-86b2-cd5fb440b12a) has IEEE the pronunciation defined as "I tripe E"
otherwise, the output will be
`{
 "pronunciation": ".ˈi .ˈe .ˈe .ˈe"}`
curl -k -v -X GET -u "1caa1631-0ff4-44d9-ae8c-f1a8731947f0:MJlT5vRDL6Xw" "https://stream-d.watsonplatform.net/text-to-speech/api/v1/pronunciation?text=IEEE&voice=es-LA_SofiaVoice&format=ipa"
curl -k -v -X GET -u "1caa1631-0ff4-44d9-ae8c-f1a8731947f0:MJlT5vRDL6Xw" "https://stream-d.watsonplatform.net/text-to-speech/api/v1/pronunciation?text=IEEE&voice=es-LA_SofiaVoice&format=spr"
curl -k -v -X GET -u "1caa1631-0ff4-44d9-ae8c-f1a8731947f0:MJlT5vRDL6Xw" "https://stream-d.watsonplatform.net/text-to-speech/api/v1/voices
 */

//
// curl -X POST -u "1caa1631-0ff4-44d9-ae8c-f1a8731947f0:MJlT5vRDL6Xw" --header "Content-Type: application/json" --header "Accept: audio/wav" --data "{\"text\":\"IEEE\"}" --output IEEE.wav "https://stream-d.watsonplatform.net/text-to-speech/api/v1/synthesize?voice=en-US_AllisonVoice&customization_id=15d6c8c3-180e-4683-a181-b200dd237cfc"

// works
// curl -X GET  -u "08a2150c-2450-4a70-8278-54c8cb024343:vVn0t2tn4Mq0" --output hello_world.wav "https://stream.watsonplatform.net/text-to-speech/api/v1/synthesize?accept=audio/wav&text=Hello%20world&voice=en-US_AllisonVoice"


//curl -X POST -u bdb86865-60a4-4e42-bfa8-4c91dfd583d2:L3MIsuh4AGpz --header "Content-Type: application/json" --data "{\"words\":[{\"word\":\"EEE\", \"translation\":\"wwwwwwwww\"} ]}" "https://stream-s.watsonplatform.net/text-to-speech/api/v1/validation"

//curl -X POST -u bdb86865-60a4-4e42-bfa8-4c91dfd583d2:L3MIsuh4AGpz --header "Content-Type: application/json" --data "{\"words\":[{\"word\":\"EEE\", \"translation\":\"<phoneme alphabet=\\\"ipa\\\" ph=\\\"təmˈɑto\\\"></phoneme>\"} ]}" "https://stream-s.watsonplatform.net/text-to-speech/api/v1/validation"
//təmˈɑto


public class RestClientTTS {
    private static final Logger logger = LoggerFactory.getLogger(RestClientTTS.class);

    private String host = "https://gateway-s.watsonplatform.net/text-to-speech/api/v1";
    private String user = "bdb86865-60a4-4e42-bfa8-4c91dfd583d2";
    private String password = "L3MIsuh4AGpz";

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

//    public JSONArray validatePronunciations(JSONArray jsaWords, String language) {
//        HttpHeaders headers = createHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
//        RestTemplate restTemplate = creatRestTemplate();
//        ResponseEntity<String> response = restTemplate.exchange(host + "/customizations/" + customization_id , HttpMethod.POST, request, String.class);
//        return response.getBody();
//    }


//    /// !!!!!!!!!!!!!!!!!
//    public static JSONArray validatePronunciations(JSONArray jsonWords, String language, JSONArray errors) {
//
//        WebTarget webTarget = client.target(uri).queryParam("voice", vi.voice);
//        logger.info(String.format("validatePronunciations is about to send %s request %s", prefix.toUpperCase(), webTarget.getUri().toString()));
//
//        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
//        JSONObject jsonRequest = new JSONObject();
//        jsonRequest.put("words",  jsonWords);
//        Response response = builder.post(Entity.entity(jsonRequest.toString(3), MediaType.APPLICATION_JSON));
//        int status = response.getStatus();
//        String responseBody = response.readEntity(String.class);
//        client.close();
//
//        if(status == 200 || status == 302) {
//            JSONObject jsonResponse = new JSONObject(responseBody);
//            return jsonResponse.getJSONArray("words");
//        }
//        else {
//            String error = Constants.ERRORS.FAILED_TO_VALIDATE_PRONUNCIATIONS;
//            errors.put(error);
//            logger.error(error);
//            return null;
//        }
//    }


}

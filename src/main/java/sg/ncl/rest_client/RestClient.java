package sg.ncl.rest_client;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * REST CLient to invoke the services
 * Created by Te Ye
 */
public class RestClient {

    // spring security user:password
    private String AUTHORIZATION_HEADER = "Basic dXNlcjpwYXNzd29yZA==";

    public RestClient() {
    }

    public RestClient(String authorizationHeader) {
        this.AUTHORIZATION_HEADER = authorizationHeader;
    }

    public ResponseEntity sendPostRequestWithJson(String serviceUri, String jsonString) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>(jsonString, headers);
        ResponseEntity responseEntity = restTemplate.exchange(serviceUri, HttpMethod.POST, request, String.class);
        return responseEntity;
    }

    public ResponseEntity sendPostRequest(String serviceUri) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>("parameters", headers);
        ResponseEntity responseEntity = restTemplate.exchange(serviceUri, HttpMethod.POST, request, String.class);
        return responseEntity;
    }

    public ResponseEntity sendPutRequest(String serviceUri, JSONObject object) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(object.toString(), headers);
        ResponseEntity responseEntity = restTemplate.exchange(serviceUri, HttpMethod.PUT, request, String.class);
        return responseEntity;
    }

    public ResponseEntity sendGetRequest(String serviceUri) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_HEADER);

        HttpEntity<String> request = new HttpEntity<String>("parameters", headers);
        ResponseEntity responseEntity = restTemplate.exchange(serviceUri, HttpMethod.GET, request, String.class);
        return responseEntity;
    }

}

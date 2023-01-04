package micro.ucuenca.ec.holaSpring.database;

import micro.ucuenca.ec.holaSpring.utils.Utils;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static micro.ucuenca.ec.holaSpring.Utils.SparqlTemplates.getPlaceQuery;

public class TriplestoreConnection {
    String url = "https://sd-e3dfa127.stardog.cloud:5820/Turismo2";

    private static String getBasicAuthenticationHeader() {
        String email = "ricardo.jarro98@ucuenca.edu.ec";
        String pass = "Chocolate619@";
        String valueToEncode = email + ":" + pass;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    public ResponseEntity<?> PostToTriplestore(String query){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/sparql-update; charset=UTF-8");
        map.put("Authorization", getBasicAuthenticationHeader());
        headers.setAll(map);
        HttpEntity<?> request = new HttpEntity<>(query, headers);

        ResponseEntity<?> response = new RestTemplate().postForEntity(url +"/update", request, String.class);
        System.out.println(response);
        return response;
    }
    
    public ResponseEntity<?> QueryTriplestore(String query){

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/sparql-query; charset=UTF-8");
        //map.put("Accept","application/sparql-results+json");
        map.put("Authorization", getBasicAuthenticationHeader());
        headers.setAll(map);

        HttpEntity<?> request = new HttpEntity<>(query, headers);
        ResponseEntity<?> response = new RestTemplate().postForEntity(url+"/query", request, String.class);
        if(response.getStatusCodeValue() == 200){

            String jsonData = Utils.convertXMLtoJSON(Objects.requireNonNull(response.getBody()).toString());
            if(!jsonData.equalsIgnoreCase("error")){
                return ResponseEntity.ok(jsonData);
            }else{
                return (ResponseEntity<?>) ResponseEntity.internalServerError();
            }

        }
        return response;
    }
}

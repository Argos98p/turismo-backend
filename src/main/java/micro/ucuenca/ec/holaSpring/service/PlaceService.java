package micro.ucuenca.ec.holaSpring.service;

import micro.ucuenca.ec.holaSpring.Utils.SparlQueryInsert;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.utils.Utils;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static micro.ucuenca.ec.holaSpring.Utils.SparqlTemplates.*;


@Service
public class PlaceService {

    static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    private static String url = "https://sd-e3dfa127.stardog.cloud:5820/Turismo2";

    public String toSparqlInsert(Place place){

        SparlQueryInsert insertSparql = new SparlQueryInsert();

        insertSparql.setBaseUri("http://turis-ucuenca/");
        insertSparql.setBase("http://turis-ucuenca/");
        insertSparql.setPrefix("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        insertSparql.setPrefix("dc","http://purl.org/dc/elements/1.1/");
        insertSparql.setPrefix("vcard", "http://www.w3.org/2006/vcard/ns#");
        insertSparql.setPrefix("wgs", "http://www.w3.org/2003/01/geo/wgs84_pos#");
        insertSparql.setPrefix("geo", "http://www.opengis.net/ont/geosparql#");
        insertSparql.setPrefix("tp","http://tour-pedia.org/download/tp.owl");
        /*insertSparql.setPrefix("acco", "http://purl.org/acco/ns#");
        insertSparql.setPrefix("ns", "http://www.w3.org/2006/vcard/ns#");
        insertSparql.setPrefix("ns1", "http://www.w3.org/2003/06/sw-vocab-status/ns#");
        insertSparql.setPrefix("org", "http://www.w3.org/ns/org#");
        insertSparql.setPrefix("owl", "http://www.w3.org/2002/07/owl#" );
        insertSparql.setPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        insertSparql.setPrefix("xml", "http://www.w3.org/XML/1998/namespace");
        insertSparql.setPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        insertSparql.setPrefix("core", "http://purl.org/vocab/frbr/core#");
        insertSparql.setPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        insertSparql.setPrefix("prov", "http://www.w3.org/ns/prov#");
        insertSparql.setPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        insertSparql.setPrefix("vann", "http://purl.org/vocab/vann/");
        insertSparql.setPrefix("terms", "http://purl.org/dc/terms/");
        insertSparql.setPrefix("vcard", "http://www.w3.org/2006/vcard/ns#");
        insertSparql.setPrefix("wgs", "http://www.w3.org/2003/01/geo/wgs84_pos#");
        insertSparql.setPrefix("dbpedia", "http://dbpedia.org/resource/");
        insertSparql.setPrefix( "dbpedia-owl", "http://dbpedia.org/ontology/");*/
        String basePlace=":"+place.getPlaceId();
        //insertSparql.setTriple(basePlace,":createdBy",place.getUserId());
        //insertSparql.setTriple(basePlace,":hasId", uniqueID);
        insertSparql.setTriple(basePlace,":createdBy","\""+place.getUserId()+"\"");
        insertSparql.setTriple(basePlace,"rdf:type","owl:NamedIndividual");
        insertSparql.setTriple(basePlace,"rdf:type"," tp:POI");
        insertSparql.setTriple(basePlace,"dc:title", "\""+place.getTitle()+"\"");
        insertSparql.setTriple(basePlace,"rdf:label","\""+place.getTitle()+"\"");
        insertSparql.setTriple(basePlace,"vcard:fn","\""+place.getTitle()+"\"");
        for (String imagePath: place.getImagesPaths()) {
            insertSparql.setTriple(basePlace, "vcard:hasPhoto", "\""+imagePath+"\"");
        }
        insertSparql.setGeo(basePlace,place.getLatitud(),place.getLongitud());
        return insertSparql.build();
    }


    public ResponseEntity<?> saveInTripleStore(Place place, String query){

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/sparql-update");
        map.put("Authorization", getBasicAuthenticationHeader("ricardo.jarro98@ucuenca.edu.ec", "Chocolate619@"));

        headers.setAll(map);

        HttpEntity<?> request = new HttpEntity<>(query, headers);

        ResponseEntity<?> response = new RestTemplate().postForEntity(url+"/update", request, String.class);
        System.out.println(response);

        return response;
    }


    public ResponseEntity<?> getPlaceFromDB(String placeId){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/sparql-query");
        //map.put("Accept","application/sparql-results+json");
        map.put("Authorization", getBasicAuthenticationHeader("ricardo.jarro98@ucuenca.edu.ec", "Chocolate619@"));
        headers.setAll(map);

        String queryPlace=getPlaceQuery(placeId);
        System.out.println(queryPlace);

        HttpEntity<?> request = new HttpEntity<>(queryPlace, headers);
        ResponseEntity<?> response = new RestTemplate().postForEntity(url+"/query", request, String.class);
        if(response.getStatusCodeValue() == 200){

            String jsonData = Utils.convertXMLtoJSON(Objects.requireNonNull(response.getBody()).toString());
            if(!jsonData.equalsIgnoreCase("error")){
               return ResponseEntity.ok(jsonData);
            }else{
                return (ResponseEntity<?>) ResponseEntity.internalServerError();
            }
            //System.out.println(Objects.requireNonNull(response.getBody()).toString());

        }
        return response;
    }

    public ResponseEntity<?> getAllPOIs(){

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/sparql-query");
        //map.put("Accept","application/sparql-results+json");
        map.put("Authorization", getBasicAuthenticationHeader("ricardo.jarro98@ucuenca.edu.ec", "Chocolate619@"));
        headers.setAll(map);

        String query = getAllPOIsQuery();
        HttpEntity<?> request = new HttpEntity<>(query, headers);
        ResponseEntity<?> response = new RestTemplate().postForEntity(url+"/query", request, String.class);
        if(response.getStatusCodeValue() == 200){

            String jsonData = Utils.convertXMLtoJSON(Objects.requireNonNull(response.getBody()).toString());
            if(!jsonData.equalsIgnoreCase("error")){
                return ResponseEntity.ok(jsonData);
            }else{
                return (ResponseEntity<?>) ResponseEntity.internalServerError();
            }
            //System.out.println(Objects.requireNonNull(response.getBody()).toString());

        }
        return response;
    }

    public ResponseEntity<?> getNearPOIs(String idPlace, String km){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/sparql-query");
        //map.put("Accept","application/sparql-results+json");
        map.put("Authorization", getBasicAuthenticationHeader("ricardo.jarro98@ucuenca.edu.ec", "Chocolate619@"));
        headers.setAll(map);

        String query = getNearPOIsQuery(idPlace,km);
        HttpEntity<?> request = new HttpEntity<>(query, headers);
        ResponseEntity<?> response = new RestTemplate().postForEntity(url+"/query", request, String.class);
        if(response.getStatusCodeValue() == 200){

            String jsonData = Utils.convertXMLtoJSON(Objects.requireNonNull(response.getBody()).toString());
            if(!jsonData.equalsIgnoreCase("error")){
                return ResponseEntity.ok(jsonData);
            }else{
                return (ResponseEntity<?>) ResponseEntity.internalServerError();
            }
            //System.out.println(Objects.requireNonNull(response.getBody()).toString());

        }
        return response;
    }
}

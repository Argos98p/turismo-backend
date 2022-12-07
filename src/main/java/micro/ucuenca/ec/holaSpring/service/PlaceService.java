package micro.ucuenca.ec.holaSpring.service;

import micro.ucuenca.ec.holaSpring.Utils.SparlQueryInsert;
import micro.ucuenca.ec.holaSpring.database.TriplestoreConnection;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.utils.Utils;
import org.apache.jena.base.Sys;
import org.springframework.beans.factory.annotation.Autowired;
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

    TriplestoreConnection triplestoreConnection;

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
        insertSparql.setTriple(basePlace,"dc:description","\""+place.getDescripcion()+"\"");
        insertSparql.setTriple(basePlace,"rdf:type","owl:NamedIndividual");
        insertSparql.setTriple(basePlace,"rdf:type"," tp:POI");
        insertSparql.setTriple(basePlace,"dc:title", "\""+place.getTitle()+"\"");
        insertSparql.setTriple(basePlace,"rdf:label","\""+place.getTitle()+"\"");
        insertSparql.setTriple(basePlace,"vcard:fn","\""+place.getTitle()+"\"");
        String photos = "(";
        /*
        for(int i = 0 ;i<=place.getImagesPaths().size();i++){
            if (i==place.getImagesPaths().size()-1){
                photos= photos.concat("\""+place.getImagesPaths().get(i)+"\"");
            }else{
                photos= photos.concat("\""+place.getImagesPaths().get(i)+"\", ");
            }

        }*/
        for (String imagePath: place.getImagesPaths()) {

            photos= photos.concat("\""+imagePath+"\" ");
            //insertSparql.setTriple(basePlace, "vcard:hasPhoto", "\""+imagePath+"\"");
        }
        photos=photos.concat(")");
        insertSparql.setTriple(basePlace, "vcard:hasPhoto", photos);
        insertSparql.setGeo(basePlace,place.getLatitud(),place.getLongitud());
        return insertSparql.build();
    }


    public ResponseEntity<?> saveInTripleStore(Place place, String query){

        triplestoreConnection = new TriplestoreConnection();
        return triplestoreConnection.PostToTriplestore(query);

    }


    public ResponseEntity<?> getPlaceFromDB(String placeId){

        String queryPlace=getPlaceQuery(placeId);
        triplestoreConnection = new TriplestoreConnection();
        return triplestoreConnection.QueryTriplestore(queryPlace);

    }

    public ResponseEntity<?> getAllPOIs(){
        String query = getAllPOIsQuery();
        triplestoreConnection= new TriplestoreConnection();
        return triplestoreConnection.QueryTriplestore(query);
    }

    public ResponseEntity<?> getNearPOIs(String idPlace, String km){
        String query = getNearPOIsQuery(idPlace,km);
        triplestoreConnection = new TriplestoreConnection();
        return triplestoreConnection.QueryTriplestore(query);
    }


}

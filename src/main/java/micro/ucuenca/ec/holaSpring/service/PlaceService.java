package micro.ucuenca.ec.holaSpring.service;

import micro.ucuenca.ec.holaSpring.Utils.SparlQueryInsert;
import micro.ucuenca.ec.holaSpring.database.TriplestoreConnection;
import micro.ucuenca.ec.holaSpring.fb.FbConnection;
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

import java.util.*;

import static micro.ucuenca.ec.holaSpring.Utils.SparqlTemplates.*;


@Service
public class PlaceService {

    TriplestoreConnection triplestoreConnection;

    static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    public static ArrayList<String> getImageUrls(ArrayList<String> placeIds){
        return FbConnection.getImagesSrcById(placeIds);
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
        String basePlace=":"+place.getPlaceId();
        insertSparql.setTriple(basePlace,":createdBy","\""+place.getUserId()+"\"");
        insertSparql.setTriple(basePlace,"dc:description","\""+place.getDescripcion()+"\"");
        insertSparql.setTriple(basePlace,"rdf:type","owl:NamedIndividual");
        insertSparql.setTriple(basePlace,"rdf:type"," tp:POI");
        insertSparql.setTriple(basePlace,"dc:title", "\""+place.getTitle()+"\"");
        insertSparql.setTriple(basePlace,"rdf:label","\""+place.getTitle()+"\"");
        insertSparql.setTriple(basePlace,"vcard:fn","\""+place.getTitle()+"\"");
        insertSparql.setTriple(basePlace,"vcard:fn","\""+place.getTitle()+"\"");
        insertSparql.setTriple(basePlace, ":status","\"revisar\"");
        insertSparql.setTriple(basePlace,":official","false");
        String fb_ids="(";
        String photos = "(";
        for (String imagePath: place.getImagesPaths()) {
            photos= photos.concat("\""+imagePath+"\" ");
        }
        for(String fb_image_id: place.getFbIds()){
            fb_ids=fb_ids.concat("\""+fb_image_id+"\"");
        }
        photos=photos.concat(")");
        fb_ids=fb_ids.concat(")");
        insertSparql.setTriple(basePlace, "vcard:hasPhoto", photos);
        insertSparql.setTriple(basePlace,":fbPhotosIds",fb_ids);
        insertSparql.setGeo(basePlace,place.getLatitud(),place.getLongitud());
        return insertSparql.build();
    }

    public ResponseEntity<?> saveInTripleStore(Place place, String query){
        triplestoreConnection = new TriplestoreConnection();
        return triplestoreConnection.PostToTriplestore(query);
    }


    public ResponseEntity<?> getPlaceFromDB(String placeId){
        String queryPlace=getPlaceQuery(placeId);
        System.out.println(queryPlace);
        triplestoreConnection = new TriplestoreConnection();
        return triplestoreConnection.QueryTriplestore(queryPlace);
    }

    public ResponseEntity<?> getAllPOIs(){
        String query = getAllPOIsQuery();
        triplestoreConnection= new TriplestoreConnection();
        return triplestoreConnection.QueryTriplestore(query);
    }

    public ResponseEntity<?> getPlacesRevisar(String status){
        String query = getPlacesPorRevisar(status);
        System.out.println(query);
        triplestoreConnection= new TriplestoreConnection();
        return triplestoreConnection.QueryTriplestore(query);
    }


    public ResponseEntity<?> getNearPOIs(String idPlace, String km){
        String query = getNearPOIsQuery(idPlace,km);
        triplestoreConnection = new TriplestoreConnection();
        return triplestoreConnection.QueryTriplestore(query);
    }

    public ResponseEntity<?> rechazarLugar(String idPlace){
        String query = rechazarPlace(idPlace);
        triplestoreConnection=new TriplestoreConnection();
        return triplestoreConnection.PostToTriplestore(query);
    }

    public ResponseEntity<?> aceptarLugar(String placeId) {
        String query = aceptarPlace(placeId);
        triplestoreConnection=new TriplestoreConnection();
        return triplestoreConnection.PostToTriplestore(query);
    }
}

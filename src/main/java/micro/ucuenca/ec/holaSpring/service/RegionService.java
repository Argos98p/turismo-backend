package micro.ucuenca.ec.holaSpring.service;

import micro.ucuenca.ec.holaSpring.Utils.SparlQueryInsert;
import micro.ucuenca.ec.holaSpring.database.TriplestoreConnection;
import micro.ucuenca.ec.holaSpring.model.Region;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RegionService {
     TriplestoreConnection triplestoreConnection;

    public String InsertQueryRegion(Region region){
        SparlQueryInsert insertSparql = new SparlQueryInsert();

        String basePlace="myregion:"+region.getId();
        insertSparql.setBaseUri("http://turis-ucuenca/");
        insertSparql.setBase("http://turis-ucuenca/");
        insertSparql.setPrefix("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        insertSparql.setPrefix("geo", "http://www.opengis.net/ont/geosparql#");
        insertSparql.setPrefix("dbo","http://dbpedia.org/ontology/");
        insertSparql.setPrefix("myregion","http://turis-ucuenca/regiones");
        insertSparql.setTriple(basePlace, "a", "dbo:Region");
        insertSparql.setTriple(basePlace,"geo:hasGeometry", insertSparql.geoPolygon(region));
        return insertSparql.build();
    }

    public ResponseEntity<?> saveInTripleStore(String data){

        triplestoreConnection = new TriplestoreConnection();
        return triplestoreConnection.PostToTriplestore(data);
    }


}

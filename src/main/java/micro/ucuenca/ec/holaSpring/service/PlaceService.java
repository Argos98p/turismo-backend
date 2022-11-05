package micro.ucuenca.ec.holaSpring.service;

import micro.ucuenca.ec.holaSpring.Utils.Slug;
import micro.ucuenca.ec.holaSpring.Utils.SparlQueryInsert;
import micro.ucuenca.ec.holaSpring.model.Place;
import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.stereotype.Service;

@Service
public class PlaceService {

    public String toSparqlInsert(Place place){
        SparlQueryInsert insertSparql = new SparlQueryInsert();

        insertSparql.setBaseUri("http://tour-pedia.org/download/tp.owl");
        insertSparql.setPrefix("dc","http://purl.org/dc/elements/1.1/");
        insertSparql.setPrefix("acco", "http://purl.org/acco/ns#");
        insertSparql.setPrefix("ns", "http://www.w3.org/2006/vcard/ns#");
        insertSparql.setPrefix( "ns1", "http://www.w3.org/2003/06/sw-vocab-status/ns#");
        insertSparql.setPrefix(  "org", "http://www.w3.org/ns/org#");
        insertSparql.setPrefix("owl", "http://www.w3.org/2002/07/owl#" );
        insertSparql.setPrefix( "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        insertSparql.setPrefix( "xml", "http://www.w3.org/XML/1998/namespace");
        insertSparql.setPrefix( "xsd", "http://www.w3.org/2001/XMLSchema#");
        insertSparql.setPrefix("core", "http://purl.org/vocab/frbr/core#");
        insertSparql.setPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        insertSparql.setPrefix("prov", "http://www.w3.org/ns/prov#");
        insertSparql.setPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        insertSparql.setPrefix("vann", "http://purl.org/vocab/vann/");
        insertSparql.setPrefix("terms", "http://purl.org/dc/terms/");
        insertSparql.setPrefix("vcard", "http://www.w3.org/2006/vcard/ns#");
        insertSparql.setPrefix("wgs", "http://www.w3.org/2003/01/geo/wgs84_pos#");
        insertSparql.setPrefix("geo", "http://www.opengis.net/ont/geosparql#");
        insertSparql.setPrefix("dbpedia", "http://dbpedia.org/resource/");
        insertSparql.setPrefix( "dbpedia-owl", "http://dbpedia.org/ontology/");
        String basePlace=":"+new Slug().makeSlug( place.getTitle());
        insertSparql.setTriple(basePlace,"rdf:type","owl:NamedIndividual");
        insertSparql.setTriple(basePlace,"dc:title", "\""+place.getTitle()+"\"");
        insertSparql.setTriple(basePlace,"rdf:label","\""+place.getTitle()+"\"");
        insertSparql.setTriple(basePlace,"vcard:fn","\""+place.getTitle()+"\"");
        for (String imagePath: place.getImagesPaths()) {
            insertSparql.setTriple(basePlace, "vcard:hasPhoto", "\""+imagePath+"\"");
        }
        insertSparql.setGeo(place.getTitle(),place.getLatitud(),place.getLongitud());
        return insertSparql.build();
    }
}

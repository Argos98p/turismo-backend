package micro.ucuenca.ec.holaSpring.Utils;

import micro.ucuenca.ec.holaSpring.model.Place;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class SparlQueryInsert {

    String prefixes;
    String triples;
    String baseUri;
    String base;
    String geo;
    // Build map

    // Replace

    public void setBase(String uri){
        base = "@base  <"+uri+"> .";
    }
    public void setPrefix(String prefix,String uri){
        String templatePrefix = "PREFIX ${prefix}: <${uri}> ";
        Map<String, String> tripleMap = new HashMap<>();
        tripleMap.put("prefix",prefix);
        tripleMap.put("uri",uri);
        StringSubstitutor sub = new StringSubstitutor(tripleMap);
        String resolvedString = sub.replace(templatePrefix);
        prefixes=prefixes.concat("\n"+resolvedString);
    }

    public void setGeo(String geoUri, String lat, String longp){

        String templateGeoComponent =":${geoUri} a geo:Geometry ;\n" +
                "      wgs:lat \"${lat}\"^^xsd:float ;\n" +
                "      wgs:long \"${longp}\"^^xsd:float .";
        Map<String, String>  geoMap = new HashMap<>();
        geoMap.put("geoUri",new Slug().makeSlug(geoUri).concat("_geo"));
        geoMap.put("lat",lat);
        geoMap.put("longp",longp);
        StringSubstitutor sub = new StringSubstitutor(geoMap);
        geo = sub.replace(templateGeoComponent);
        triples = triples.concat("\n:"+new Slug().makeSlug(geoUri)+"  geo:hasGeometry " +":"+new Slug().makeSlug(geoUri).concat("_geo ."));

    }
    public void setTriple(String subject, String predicate, String object){
        String templatePrefix = "${subject} ${predicate} ${object} .";
        Map<String, String>  tripleMap = new HashMap<>();
        tripleMap.put("subject",subject);
        tripleMap.put("predicate", predicate);
        tripleMap.put("object",object);
        StringSubstitutor sub = new StringSubstitutor(tripleMap);
        String resolvedString = sub.replace(templatePrefix);
        triples = triples.concat("\n"+resolvedString);
    }

    public void setBaseUri(String baseUri2){
        baseUri = "PREFIX : <"+baseUri2+"> ";
    }

    public String build(){
        String query = "${baseUri} ${prefixes} \nINSERT { ${triples}\n${geo} } WHERE { }";
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("baseUri",baseUri);
        queryMap.put("prefixes",prefixes);
        queryMap.put("triples",triples);
        queryMap.put("geo",geo);
        //queryMap.put("base",base);
        StringSubstitutor sub = new StringSubstitutor(queryMap);
        return sub.replace(query);
    }

    public SparlQueryInsert() {
        prefixes="";
        baseUri="";
        triples="";
        geo="";
        base="";
    }

}

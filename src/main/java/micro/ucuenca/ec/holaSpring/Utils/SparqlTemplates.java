package micro.ucuenca.ec.holaSpring.Utils;

public class SparqlTemplates {

    public static String getPlaceQuery(String placeId){
        String place=":";

        return """
                prefix : <http://turis-ucuenca/>\s
                prefix tp: <http://tour-pedia.org/download/tp.owl>
                prefix dc: <http://purl.org/dc/elements/1.1/>
                prefix  vcard: <http://www.w3.org/2006/vcard/ns#>
                prefix geo: <http://www.opengis.net/ont/geosparql#>
                prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>
                prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\s
                                
                base  <http://turis-ucuenca/>\s
                                
                SELECT   ?titulo ?creadoPor ?descripcion ?status ?lat ?long (GROUP_CONCAT(DISTINCT ?elemFB ; SEPARATOR = ",") AS ?fbIDs) (GROUP_CONCAT(?elem ; SEPARATOR = ",") AS ?imagenes) WHERE
                 {
                    """ +place.concat(placeId).concat(" ")+"""  
                    :createdBy ?creadoPor ;
                    dc:title ?titulo ;
                    dc:description ?descripcion;
                    vcard:hasPhoto ?imagenes;
                    :status ?status;
                    :fbPhotosIds ?fbIds;
                    geo:hasGeometry ?geom.
                    ?geom wgs:lat ?lat;
                     wgs:long ?long.
                    ?fbIds rdf:rest*/rdf:first ?elemFB.
                    ?imagenes rdf:rest*/rdf:first ?elem .
                   \s
                } GROUP BY ?titulo ?creadoPor ?status ?descripcion ?lat ?long
                """;
    }


    public static String getPlacesPorRevisar(String status){
        status = "\""+status+"\"";
        return """
                prefix : <http://turis-ucuenca/>\s
                prefix tp: <http://tour-pedia.org/download/tp.owl>
                prefix dc: <http://purl.org/dc/elements/1.1/>
                prefix  vcard: <http://www.w3.org/2006/vcard/ns#>
                prefix geo: <http://www.opengis.net/ont/geosparql#>
                prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>
                prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\s
                                
                base  <http://turis-ucuenca/>\s
                                
                SELECT ?url ?titulo ?creadoPor ?descripcion ?status ?lat ?long (GROUP_CONCAT(DISTINCT ?elemFB ; SEPARATOR = ",") AS ?fbIDs) (GROUP_CONCAT(?elem ; SEPARATOR = ",") AS ?images) WHERE
                 {
                   
                    ?url a tp:POI ;
                    :createdBy ?creadoPor ;
                    dc:title ?titulo ;
                    dc:description ?descripcion;
                    vcard:hasPhoto ?imagenes;
                    :fbPhotosIds ?fbIds;
                    :status ?status;
                    geo:hasGeometry ?geom.
                    ?geom wgs:lat ?lat;
                     wgs:long ?long.
                     ?fbIds rdf:rest*/rdf:first ?elemFB.
                    ?imagenes rdf:rest*/rdf:first ?elem .
                   \s
                     FILTER(str(?status) ="""+status+"""
                   )\s
                   \s
                } GROUP BY ?url ?titulo ?creadoPor ?status ?descripcion ?lat ?long
                """;
    }
    public static String getAllPOIsQuery(){
        return """
                prefix : <http://turis-ucuenca/>\s
                prefix tp: <http://tour-pedia.org/download/tp.owl>
                prefix dc: <http://purl.org/dc/elements/1.1/>
                prefix  vcard: <http://www.w3.org/2006/vcard/ns#>
                prefix geo: <http://www.opengis.net/ont/geosparql#>
                prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>
                prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\s
                
                base  <http://turis-ucuenca/>\s
                
                SELECT ?url ?titulo ?creadoPor ?descripcion  ?status ?lat ?long (GROUP_CONCAT(DISTINCT ?elemFB ; SEPARATOR = ",") AS ?fbIDs) (GROUP_CONCAT(?elem ; SEPARATOR = ",") AS ?images) WHERE
                 {
                   # :58a347fe-3ea1-4ce6-8d88-ce27e1a5ee6a :createdBy ?creadoPor ;
                    ?url a tp:POI ;
                    :createdBy ?creadoPor ;
                    dc:title ?titulo ;
                    dc:description ?descripcion;
                    :status ?status;
                    :fbPhotosIds ?fbIds;
                    vcard:hasPhoto ?imagenes;
                    geo:hasGeometry ?geom.
                    ?geom wgs:lat ?lat;
                     wgs:long ?long.
                     ?fbIds rdf:rest*/rdf:first ?elemFB.
                    ?imagenes rdf:rest*/rdf:first ?elem .
                   \s
                } GROUP BY ?url ?titulo ?creadoPor ?status ?descripcion ?lat ?long
                """;
    }

    public static String getNearPOIsQuery(String idPlace,String km){
        return """
                prefix geo: <http://www.opengis.net/ont/geosparql#>
              prefix geof: <http://www.opengis.net/def/function/geosparql/>
              prefix  vcard: <http://www.w3.org/2006/vcard/ns#>
              prefix unit: <http://qudt.org/vocab/unit#>
              prefix dc: <http://purl.org/dc/elements/1.1/>
              prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>
              prefix : <http://turis-ucuenca/>
              prefix tp: <http://tour-pedia.org/download/tp.owl>
              base  <http://turis-ucuenca/>
                             \s
              SELECT   (?destino AS $url) ?titulo  ?distance ?lat ?creadoPor ?long (GROUP_CONCAT(DISTINCT ?elemFB ; SEPARATOR = ",") AS ?fbIDs) (GROUP_CONCAT(?elem ; SEPARATOR = ",") AS ?images) WHERE{
                              :"""+idPlace+"""
                          dc:title ?origenNombre ;
                          dc:title ?titulo ;
                          geo:hasGeometry ?geomOrigen;
                          :fbPhotosIds ?fbIds;
                          :createdBy ?creadoPor ;
                           vcard:hasPhoto ?imagenes;
                          geo:hasGeometry ?geom.
                          ?fbIds rdf:rest*/rdf:first ?elemFB.
                               ?imagenes rdf:rest*/rdf:first ?elem .
                  ?geom wgs:lat ?lat;
                   wgs:long ?long.
                 \s
                  ?destino a tp:POI;
                  geo:hasGeometry ?geomDestino ;
                  dc:title ?titulo .
                  ?geomDestino geof:nearby (?geomOrigen
                  """+km+"""                
                   unit:Kilometer) ;
                  BIND(geof:distance(?geomOrigen, ?geomDestino, unit:Kilometer) as ?distance)
                  } GROUP BY  ?destino ?titulo ?creadoPor  ?lat ?long  ?distance
                """;


    }

    public static String rechazarPlace(String placeId){
        placeId="\"http://turis-ucuenca/"+placeId+"\"";

        return """
                prefix : <http://turis-ucuenca/>
                prefix foaf: <http://xmlns.com/foaf/0.1/>
                prefix myusers: <http://turis-ucuenca/user/>
                prefix tp: <http://tour-pedia.org/download/tp.owl>
                base  <http://turis-ucuenca/>
                               \s
                DELETE { ?id :status "revisar" }
                INSERT {
                    ?id  a  tp:POI;
                    :status "rechazado"
                }
                    WHERE {
                    ?id a  tp:POI;
                    FILTER(str(?id) ="""+placeId+"""
                    ) .
                    }
                """;

    }
    public static String aceptarPlace(String placeId){
        placeId="\"http://turis-ucuenca/"+placeId+"\"";

        return """
                prefix : <http://turis-ucuenca/>
                prefix foaf: <http://xmlns.com/foaf/0.1/>
                prefix myusers: <http://turis-ucuenca/user/>
                prefix tp: <http://tour-pedia.org/download/tp.owl>
                base  <http://turis-ucuenca/>
                               \s
                DELETE { ?id :status "revisar" }
                INSERT {
                    ?id  a  tp:POI;
                    :status "aprobado"
                }
                    WHERE {
                    ?id a  tp:POI;
                    FILTER(str(?id) ="""+placeId+"""
                    ) .
                    }
                """;

    }

    public static String getUserInfo(String userId){
        String aux="\""+"http://turis-ucuenca/user/"+userId+"\"" ;
        return """
                prefix : <http://turis-ucuenca/>
                prefix foaf: <http://xmlns.com/foaf/0.1/>
                prefix myusers: <http://turis-ucuenca/user/>
                base  <http://turis-ucuenca/>
                SELECT ?user  ?org ?mail ?role WHERE{
                    ?user2 a foaf:Person;
                    :role ?role;
                    foaf:mbox ?mail2;
                    OPTIONAL{
                        ?user2  foaf:memberOf ?org2.
                        }
                    FILTER(str(?user2) ="""+aux+"""
                    ) .
                    BIND(REPLACE(STR(?mail2), "http://turis-ucuenca/","") as ?mail).
                    BIND(REPLACE(STR(?org2), "http://turis-ucuenca/org/","") as ?org).
                    BIND(REPLACE(STR(?user2), "http://turis-ucuenca/user/","") as ?user).
                }
                """;
    }

    public static String getAllOrgInfo(String organizacionId) {
        return """
                prefix : <http://turis-ucuenca/>
                prefix org: <http://www.w3.org/TR/vocab-org/>
                prefix myorg: <http://turis-ucuenca/org/>
                prefix myusers: <http://turis-ucuenca/user/>
                prefix foaf: <http://xmlns.com/foaf/0.1/>
                prefix  vcard: <http://www.w3.org/2006/vcard/ns#>
                                
                base  <http://turis-ucuenca/>
                                
                SELECT ?org ?orgName (GROUP_CONCAT(DISTINCT ?region; separator=",")AS ?regiones)  (GROUP_CONCAT( ?miembro; separator=",")AS ?miembros) WHERE{
                    ?org2 a org:Organization ;
                         :admin ?region2;
                         org:hasMember ?miembro2;   \s
                        vcard:organization-name ?orgName.
                    ?miembro2 a foaf:Person.
                   \s
                    BIND(REPLACE(STR(?org2), "http://turis-ucuenca/org/","") as ?org).
                    BIND(REPLACE(STR(?miembro2), "http://turis-ucuenca/user/","") as ?miembro).
                    BIND(REPLACE(STR(?region2), "http://turis-ucuenca/regiones/","") as ?region).
                       \s
                }GROUP BY ?org  ?orgName\s
                """;
    }

    public static String orgByUserId(String userId){
        String aux ="FILTER(STR(?miembro)=\""+userId+"\").";
        return """
                prefix : <http://turis-ucuenca/>
                prefix org: <http://www.w3.org/TR/vocab-org/>
                prefix myorg: <http://turis-ucuenca/org/>
                prefix myusers: <http://turis-ucuenca/user/>
                prefix foaf: <http://xmlns.com/foaf/0.1/>
                prefix  vcard: <http://www.w3.org/2006/vcard/ns#>
                base  <http://turis-ucuenca/>
                SELECT ?org ?orgName (GROUP_CONCAT(DISTINCT ?region; separator=",")AS ?regiones)  (GROUP_CONCAT( ?miembro; separator=",")AS ?miembros) WHERE{
                    ?org2 a org:Organization ;
                         :admin ?region2;
                         org:hasMember ?miembro2;
                        vcard:organization-name ?orgName.
                    ?miembro2 a foaf:Person.
                    BIND(REPLACE(STR(?org2), "http://turis-ucuenca/org/","") as ?org).
                    BIND(REPLACE(STR(?miembro2), "http://turis-ucuenca/user/","") as ?miembro).
                    BIND(REPLACE(STR(?region2), "http://turis-ucuenca/regiones/","") as ?region).
                    """+aux+"""
                }GROUP BY ?org  ?orgName
                """;
    }
}

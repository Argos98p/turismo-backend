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
                                
                SELECT   ?titulo ?creadoPor ?descripcion  ?lat ?long (GROUP_CONCAT(?elem ; SEPARATOR = ",") AS ?imagenes) WHERE
                 {
                    """ +place.concat(placeId).concat(" ")+"""  
                    :createdBy ?creadoPor ;
                    dc:title ?titulo ;
                    dc:description ?descripcion;
                    vcard:hasPhoto ?imagenes;
                    geo:hasGeometry ?geom.
                    ?geom wgs:lat ?lat;
                     wgs:long ?long.
                    ?imagenes rdf:rest*/rdf:first ?elem .
                    #BIND (GROUP_CONCAT(?imagenes ; SEPARATOR = ",") AS ?values)
                    #BIND (CONCAT(STR(?elem),",") AS ?image) .
                    #BIND(CONCAT(STR( ?image ), ?image)  AS ?arrayImagenes ) .
                   \s
                } GROUP BY ?titulo ?creadoPor ?descripcion ?lat ?long
                """;
    }

    public static String getAllPOIsQuery(){
        return """
                prefix tp: <http://tour-pedia.org/download/tp.owl>
                base  <http://turis-ucuenca/>\s
                SELECT * WHERE{
                    ?poi a tp:POI .
                    ?poi ?property ?value .
                }
                """;
    }

    public static String getNearPOIsQuery(String idPlace,String km){
        return """
                prefix geo: <http://www.opengis.net/ont/geosparql#>
                prefix geof: <http://www.opengis.net/def/function/geosparql/>
                prefix unit: <http://qudt.org/vocab/unit#>
                prefix dc: <http://purl.org/dc/elements/1.1/>
                prefix : <http://turis-ucuenca/>
                prefix tp: <http://tour-pedia.org/download/tp.owl>
                base  <http://turis-ucuenca/>\s
                                
                SELECT  ?destino ?destinoNombre ?distance  WHERE{
                                :"""+idPlace+ """
                          dc:title ?origenNombre ;
                          geo:hasGeometry ?geomOrigen.
                 \s
                  ?destino a tp:POI;
                    geo:hasGeometry ?geomDestino ;
                    dc:title ?destinoNombre .
                    ?geomDestino geof:nearby (?geomOrigen 
                    """+km+ """
                     unit:Kilometer) ;
                    BIND(geof:distance(?geomOrigen, ?geomDestino, unit:Kilometer) as ?distance)
                  }
                """;
    }
}

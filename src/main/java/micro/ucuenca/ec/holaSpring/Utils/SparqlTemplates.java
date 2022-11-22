package micro.ucuenca.ec.holaSpring.Utils;

public class SparqlTemplates {

    public static String getPlaceQuery(String placeId){

        return """
                prefix : <http://turis-ucuenca/>\s
                prefix tp: <http://tour-pedia.org/download/tp.owl>
                prefix geo: <http://www.opengis.net/ont/geosparql#>\s
                base  <http://turis-ucuenca/>\s
                  SELECT  ?property ?value WHERE{
                      {
                            :""" +placeId+"""
                            ?property ?value .
                    }
                    UNION
                    \s
                    {
                             :""" +placeId+"""
                              geo:hasGeometry ?geom .
                            ?geom ?property ?value .
                        
                  }
                }""";
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

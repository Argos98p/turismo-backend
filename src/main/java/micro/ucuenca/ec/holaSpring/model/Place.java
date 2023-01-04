package micro.ucuenca.ec.holaSpring.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import micro.ucuenca.ec.holaSpring.fb.FbConnection;
import micro.ucuenca.ec.holaSpring.service.PlaceService;
import org.apache.jena.base.Sys;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Place {
    //private PlaceGeo placeGeo;
    private String placeId;
    private String descripcion;
    private String userId;
    private String latitud;
    private String longitud;
    private String address;
    private String location;
    private String link;
    private String title;
    private String label;
    private String fn;
    private String status;

    private ArrayList<String> imagesPaths;
    private ArrayList<String> fbIds;

    public ArrayList<String> getFbIds() {
        return fbIds;
    }

    public void setFbIds(ArrayList<String> fbIds) {
        this.fbIds = fbIds;
    }

    private double distance ;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public ArrayList<String> getImagesPaths() {
        return imagesPaths;
    }

    public Place() {
        this.imagesPaths = new ArrayList<>();
    }

    public void addImagePath(String pathImageStore){
        this.imagesPaths.add(pathImageStore);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImagesPaths(ArrayList<String> imagesPaths) {
        this.imagesPaths = imagesPaths;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Place{" +
                "latitud='" + latitud + '\'' +
                ", longitud='" + longitud + '\'' +
                ", address='" + address + '\'' +
                ", location='" + location + '\'' +
                ", link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", label='" + label + '\'' +
                ", fn='" + fn + '\'' +
                ", id='" + placeId + "'}";
    }

    public static Place responseOnePlaceParser(String response, String placeId){
        Place place = new Place();
        place.setPlaceId(placeId);
        String resultadoString = null;
        try {
            resultadoString = new String(response.getBytes("ISO-8859-1"));
            JSONParser parser = new JSONParser();
            JSONObject result = null;
            result = (JSONObject) parser.parse(resultadoString);
            JSONObject sparqlObject= (JSONObject) result.get("sparql");
            JSONObject results= (JSONObject) sparqlObject.get("results");
            JSONObject otherResult = (JSONObject) results.get("result");
            JSONArray resultadosArray = (JSONArray) otherResult.get("binding");

            for(int i = 0;i<resultadosArray.size();i++){

                JSONObject myObject = (JSONObject) resultadosArray.get(i);
                String name = (String) myObject.get("name");
                if(name.equalsIgnoreCase("creadoPor")){
                    place.setUserId((String) myObject.get("literal").toString());
                }else if (name.equalsIgnoreCase("descripcion")){
                    place.setDescripcion((String) myObject.get("literal"));
                } else if (name.equalsIgnoreCase("imagenes")) {
                    String imagenes = (String) myObject.get("literal");
                    ArrayList<String> srcImages = new ArrayList<String>(Arrays.asList(imagenes.split(",")));
                    place.setImagesPaths(srcImages);
                }else if(name.equalsIgnoreCase("status")){
                    place.setStatus((String) myObject.get("literal"));
                }
                else if (name.equalsIgnoreCase("fbIDs")){
                    String fb_ids_string = (String) myObject.get("literal");
                    ArrayList<String> fbIds= new ArrayList<String>(Arrays.asList(fb_ids_string.split(",")));
                    System.out.println(fbIds);
                    place.setFbIds(fbIds);
                    place.setImagesPaths( PlaceService.getImageUrls(fbIds));
                } else if (name.equalsIgnoreCase("titulo")) {
                    place.setTitle((String) myObject.get("literal"));
                }else if(name.equalsIgnoreCase("lat")){
                    JSONObject numberObject = (JSONObject) myObject.get("literal");
                    place.setLatitud((String) numberObject.get("content").toString());

                }else if( name.equalsIgnoreCase("long")){
                    JSONObject numberObject = (JSONObject) myObject.get("literal");
                    place.setLongitud((String) numberObject.get("content").toString());
                }
            }
            return place;

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }
    public static ArrayList<Place> responseParserToPlace (String responseBody) {

        String resultadoString = null;
        ArrayList<Place> places = new ArrayList<>();

        try {
            resultadoString = new String(responseBody.getBytes("ISO-8859-1"));
            JSONParser parser = new JSONParser();
            JSONObject result = null;
            result = (JSONObject) parser.parse(resultadoString);

            System.out.println(result);
            JSONObject sparqlObject = (JSONObject) result.get("sparql");
            JSONObject results = (JSONObject) sparqlObject.get("results");

            if (results.get("result") instanceof JSONArray){

            }else{
                if(results.get("result") instanceof JSONObject){
                    Place place = new Place();
                    JSONObject otherResult = (JSONObject) results.get("result");
                    JSONArray resultadosArray = (JSONArray) otherResult.get("binding");

                    for(int i = 0;i<resultadosArray.size();i++){

                        JSONObject myObject = (JSONObject) resultadosArray.get(i);
                        String name = (String) myObject.get("name");
                        if(name.equalsIgnoreCase("creadoPor")){
                            place.setUserId((String) myObject.get("literal").toString());
                        }else if (name.equalsIgnoreCase("url")) {
                            String uri = (String) myObject.get("uri");
                            place.setPlaceId(uri.replace("http://turis-ucuenca/", ""));
                        }else if (name.equalsIgnoreCase("descripcion")){
                            place.setDescripcion((String) myObject.get("literal"));
                        } else if (name.equalsIgnoreCase("imagenes")) {
                            String imagenes = (String) myObject.get("literal");
                            ArrayList<String> srcImages = new ArrayList<String>(Arrays.asList(imagenes.split(",")));
                            place.setImagesPaths(srcImages);
                        }else if (name.equalsIgnoreCase("fbIDs")){
                            String fb_ids_string = (String) myObject.get("literal");
                            ArrayList<String> fbIds= new ArrayList<String>(Arrays.asList(fb_ids_string.split(",")));
                            System.out.println(fbIds);
                            place.setFbIds(fbIds);
                            place.setImagesPaths( PlaceService.getImageUrls(fbIds));
                        }else if(name.equalsIgnoreCase("status")){
                            place.setStatus((String) myObject.get("literal"));
                        }
                        else if (name.equalsIgnoreCase("titulo")) {
                            place.setTitle((String) myObject.get("literal"));
                        }else if(name.equalsIgnoreCase("lat")){
                            JSONObject numberObject = (JSONObject) myObject.get("literal");
                            place.setLatitud((String) numberObject.get("content").toString());

                        }else if( name.equalsIgnoreCase("long")){
                            JSONObject numberObject = (JSONObject) myObject.get("literal");
                            place.setLongitud((String) numberObject.get("content").toString());
                        }
                    }
                    ArrayList<Place> aux= new ArrayList<>();
                    aux.add(place);
                    return  aux;
                }

                return places;
            }

            JSONArray otherResult = (JSONArray) results.get("result");
            for (int j = 0; j < otherResult.size(); j++) {
                JSONObject aux = (JSONObject) otherResult.get(j);
                JSONArray resultadosArray = (JSONArray) aux.get("binding");
                Place place = new Place();
                for (int i = 0; i < resultadosArray.size(); i++) {
                    JSONObject myObject = (JSONObject) resultadosArray.get(i);
                    String name = (String) myObject.get("name");
                    if (name.equalsIgnoreCase("creadoPor")) {
                        place.setUserId((String) myObject.get("literal").toString());
                    } else if (name.equalsIgnoreCase("url")) {
                        String uri = (String) myObject.get("uri");
                        place.setPlaceId(uri.replace("http://turis-ucuenca/", ""));
                    } else if (name.equalsIgnoreCase("descripcion")) {
                        place.setDescripcion((String) myObject.get("literal"));
                    }else if(name.equalsIgnoreCase("status")){
                        place.setStatus((String) myObject.get("literal"));
                    } else if (name.equalsIgnoreCase("images")) {
                        String imagenes = (String) myObject.get("literal");
                        ArrayList<String> srcImages = new ArrayList<String>(Arrays.asList(imagenes.split(",")));
                        place.setImagesPaths(srcImages);
                    } else if (name.equalsIgnoreCase("titulo")) {
                        place.setTitle((String) myObject.get("literal"));
                    }else if (name.equalsIgnoreCase("fbIDs")){
                        String fb_ids_string = (String) myObject.get("literal");
                        ArrayList<String> fbIds= new ArrayList<String>(Arrays.asList(fb_ids_string.split(",")));
                        place.setFbIds(fbIds);
                        place.setImagesPaths( PlaceService.getImageUrls(fbIds));
                    } else if (name.equalsIgnoreCase("lat")) {
                        JSONObject numberObject = (JSONObject) myObject.get("literal");
                        place.setLatitud((String) numberObject.get("content").toString());
                    } else if (name.equalsIgnoreCase("long")) {
                        JSONObject numberObject = (JSONObject) myObject.get("literal");
                        place.setLongitud((String) numberObject.get("content").toString());
                    }
                }
                /*place.setImagesPaths( FbConnection.getImagesSrcById(place.getFbIds()));*/

                places.add(place);
            }


        } catch (UnsupportedEncodingException | ParseException e) {


            e.printStackTrace();


        }
            return places;

    }
}

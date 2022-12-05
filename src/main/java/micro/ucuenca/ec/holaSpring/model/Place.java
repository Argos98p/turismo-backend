package micro.ucuenca.ec.holaSpring.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

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
    private ArrayList<String> imagesPaths;

    public ArrayList<String> getImagesPaths() {
        return imagesPaths;
    }

    public Place() {
        this.imagesPaths = new ArrayList<>();
    }

    public void addImagePath(String pathImageStore){
        this.imagesPaths.add(pathImageStore);
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
                ", fn='" + fn + '\'' ;
    }
}

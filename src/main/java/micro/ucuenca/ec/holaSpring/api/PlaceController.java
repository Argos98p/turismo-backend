package micro.ucuenca.ec.holaSpring.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.service.FileStorageService;
import micro.ucuenca.ec.holaSpring.service.PlaceService;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaceService placeService;

    @PostMapping("/add")
    public Place newPlace(@RequestParam("model") String jsonObject , @RequestParam("files")MultipartFile[] files) throws IOException {

        Place place;
        place = objectMapper.readValue(jsonObject, Place.class);
        String namePlace = place.getTitle();

        Arrays.asList(files).stream().forEach(file -> {
            String fileStorePath = fileStorageService.storeFile(file, namePlace);
            ServletUriComponentsBuilder.fromCurrentContextPath().path(fileStorePath).toUriString();
            place.addImagePath(fileStorePath);
        });
        String query = placeService.toSparqlInsert(place);
        System.out.println(query);
        String url = "https://sd-e3dfa127.stardog.cloud:5820/Turismo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("ricardo.jarro98@ucuenca.edu.ec", "Chocolate619@");
        HttpEntity<?> entity = new HttpEntity<>(headers);


        System.out.println(UriUtils.encodeQuery(query, StandardCharsets.UTF_8));

    /*
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", "").encode()
                .toUriString();
        System.out.println(urlTemplate);
        System.out.println(place);*/

        return null;
    }
}
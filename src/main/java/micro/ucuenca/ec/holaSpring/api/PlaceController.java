package micro.ucuenca.ec.holaSpring.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.service.FileStorageService;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;




    @PostMapping("/add")
    public Place newPlace(@RequestParam("model") String jsonObject , @RequestParam("files")MultipartFile[] files) throws IOException {

            Arrays.asList(files).stream().forEach(file -> {
                Place place = null;
                try {
                    place = objectMapper.readValue(jsonObject, Place.class);
                    String namePlace = place.getTitle();
                    String fileStorePath = fileStorageService.storeFile(file,namePlace);
                    ServletUriComponentsBuilder.fromCurrentContextPath().path(fileStorePath).toUriString();
                    place.addImagePath(fileStorePath);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            });

        return null;
    }

}

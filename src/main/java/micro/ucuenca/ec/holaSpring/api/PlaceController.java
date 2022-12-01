package micro.ucuenca.ec.holaSpring.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.payload.response.MessageResponse;
import micro.ucuenca.ec.holaSpring.service.FileStorageService;
import micro.ucuenca.ec.holaSpring.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    public ResponseEntity<?> newPlace(@RequestParam("model") String jsonObject, @RequestParam("files")MultipartFile[] files)  {

        Place place;
        try {
            place = objectMapper.readValue(jsonObject, Place.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if(place.getUserId()==null){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: ID user is not present"));
        }
        if(place.getTitle()==null){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Name of resource is empty"));
        }
        if(place.getLongitud()==null || place.getLatitud()==null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Coordinates of resource are empty"));
        }

        place.setPlaceId(UUID.randomUUID().toString());

        Arrays.asList(files).stream().forEach(file -> {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            // ContentType
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization","Bearer EAAmqhoYHuBsBAIEgFsPJlG6KgheM9PwieLe2HgqOdQZAlSGZBQwgf3qJVhIwaoNtChbyx8TAL0mqpFNZCJq1bj1cImkKlom8r0dTrTGlvsJFBEJEptmUieQowBxLRKOe16Yj8vRlnbIIwfRonZAYjzZBGGczNoIWF3jLGv46kuBR21t4spw94");
            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            // Load a file from disk.
            Resource file1 = new FileSystemResource((File) file);
            multipartBodyBuilder.part("avatar", file1, MediaType.IMAGE_JPEG);
            // multipart/form-data request body
            MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();
            // The complete http request body.
            HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://graph.facebook.com/v15.0/165980483492633/photos", httpEntity,
                    String.class);
            System.out.println(responseEntity.getStatusCodeValue());
            /*
            String fileStorePath = fileStorageService.storeFile(file, place.getPlaceId());
            ServletUriComponentsBuilder.fromCurrentContextPath().path(fileStorePath).toUriString();
            place.addImagePath(fileStorePath);*/
        });
        /*

        String query = placeService.toSparqlInsert(place);


        ResponseEntity<?> response = placeService.saveInTripleStore(place, query);

        if(response.getStatusCodeValue() == 200){
            return ResponseEntity.ok(new MessageResponse("Place registered successfully!"));
        }

        return response;*/
        return ResponseEntity.ok(new MessageResponse("Place registered successfully!"));

    }

    @GetMapping("/get")
    public ResponseEntity<?> getPlace (@RequestParam("placeId") String placeId){
        return placeService.getPlaceFromDB(placeId);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPlaces(){

        return placeService.getAllPOIs();
    }

    @GetMapping("nearPlaces")
    public ResponseEntity<?> nearPlaces(@RequestParam("placeId") String placeId, @RequestParam("km") String km){
        return placeService.getNearPOIs(placeId,km);
    }
}
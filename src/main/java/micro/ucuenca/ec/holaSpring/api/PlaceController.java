package micro.ucuenca.ec.holaSpring.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.payload.response.MessageResponse;
import micro.ucuenca.ec.holaSpring.service.FileStorageService;
import micro.ucuenca.ec.holaSpring.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public ResponseEntity<?> newPlace(@RequestParam("model") String jsonObject, @RequestParam("files")MultipartFile[] files) throws IOException, InterruptedException {

        Place place;
        place = objectMapper.readValue(jsonObject, Place.class);

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
            String fileStorePath = fileStorageService.storeFile(file, place.getPlaceId());
            ServletUriComponentsBuilder.fromCurrentContextPath().path(fileStorePath).toUriString();
            place.addImagePath(fileStorePath);
        });
        String query = placeService.toSparqlInsert(place);


        ResponseEntity<?> response = placeService.saveInTripleStore(place, query);

        if(response.getStatusCodeValue() == 200){
            return ResponseEntity.ok(new MessageResponse("Place registered successfully!"));
        }

        return response;
        //return ResponseEntity.ok(new MessageResponse("Place registered successfully!"));

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
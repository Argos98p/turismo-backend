package micro.ucuenca.ec.holaSpring.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import micro.ucuenca.ec.holaSpring.fb.FbConnection;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.payload.response.MessageResponse;
import micro.ucuenca.ec.holaSpring.service.FileStorageService;
import micro.ucuenca.ec.holaSpring.service.PlaceService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaceService placeService;


    private File convertMultiPartToFile(MultipartFile file ) throws IOException {
        System.out.println(file.getName());
        File convFile = new File( file.getOriginalFilename() );
        FileOutputStream fos = new FileOutputStream( convFile );
        fos.write( file.getBytes() );
        fos.close();
        return convFile;
    }
    //@PostMapping("/add" )
    @RequestMapping(path = "/add", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
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
        ArrayList<String> imagesPaths= new ArrayList<>();
        for(int i = 0;i<files.length;i++){
            imagesPaths.add(fileStorageService.storeFile(files[i],"" ));
        }

        ArrayList<String> imagesOfResource = FbConnection.ToFacebook(imagesPaths);
        place.setImagesPaths(imagesOfResource);

        String query = placeService.toSparqlInsert(place);


        ResponseEntity<?> response = placeService.saveInTripleStore(place, query);

        if(response.getStatusCodeValue() == 200){
            return ResponseEntity.ok().body(place);
        }

        return ResponseEntity.ok(new MessageResponse("Place registered successfully!"));

    }

    @GetMapping("/get")
    public ResponseEntity<?> getPlace (@RequestParam("placeId") String placeId){

        String resultadoJson = (String) placeService.getPlaceFromDB(placeId).getBody();
        Place place = new Place();
        place.setPlaceId(placeId);

        JSONParser parser = new JSONParser();
        JSONObject result = null;

        try {
            result = (JSONObject) parser.parse(resultadoJson);
            JSONObject sparqlObject= (JSONObject) result.get("sparql");
            JSONObject results= (JSONObject) sparqlObject.get("results");
            JSONObject otherResult = (JSONObject) results.get("result");
            JSONArray resultadosArray = (JSONArray) otherResult.get("binding");

            for(int i = 0;i<resultadosArray.size();i++){

                JSONObject myObject = (JSONObject) resultadosArray.get(i);

                String name = (String) myObject.get("name");

                if(name.equalsIgnoreCase("creadoPor")){
                    place.setUserId((String) myObject.get("literal"));
                }else if (name.equalsIgnoreCase("descripcion")){
                    place.setDescripcion((String) myObject.get("literal"));
                } else if (name.equalsIgnoreCase("imagenes")) {
                    String imagenes= (String) myObject.get("literal");
                    ArrayList<String> srcImages =  new ArrayList<String>(Arrays.asList(imagenes.split(",")));
                    place.setImagesPaths(srcImages);
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


            return ResponseEntity.ok().body(place);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }



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
package micro.ucuenca.ec.holaSpring.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.model.Region;
import micro.ucuenca.ec.holaSpring.payload.response.MessageResponse;
import micro.ucuenca.ec.holaSpring.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/region")
public class RegionController {

    @Autowired
    RegionService regionService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/add")
    public ResponseEntity<?> newRegion(@RequestBody String jsonRequest){
        try {
            Region region = objectMapper.readValue(jsonRequest, Region.class);

            if(region.getNombre()==null || region.getCoordenadas()==null){
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: falta informacion"));
            }
            region.setId(UUID.randomUUID().toString());

            String query = regionService.InsertQueryRegion(region);
            ResponseEntity<?> response = regionService.saveInTripleStore(query);
            if(response.getStatusCodeValue() == 200){
                return ResponseEntity.ok(new MessageResponse("Region registered successfully!"));
            }
            return response;


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

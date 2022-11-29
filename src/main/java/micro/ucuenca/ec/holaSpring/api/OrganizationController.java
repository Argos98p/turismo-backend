package micro.ucuenca.ec.holaSpring.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import micro.ucuenca.ec.holaSpring.model.MemberRequest;
import micro.ucuenca.ec.holaSpring.model.Organization;
import micro.ucuenca.ec.holaSpring.model.OrganizationRegionRequest;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.payload.response.MessageResponse;
import micro.ucuenca.ec.holaSpring.service.OrganizationService;
import org.apache.jena.base.Sys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    @Autowired
    OrganizationService organizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/add")
    public ResponseEntity<?> newOrganization(@RequestParam("organizacion") String jsonObject, @RequestParam("logo") MultipartFile file){

        Organization organization;
        try {
            organization = objectMapper.readValue(jsonObject, Organization.class);

            if(organization.getName()==null || organization.getParroquia()==null){
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: falta informacion"));
            }
            organization.setId(UUID.randomUUID().toString());

            String query = organizationService.toSparqlInsert(organization);

            ResponseEntity<?> response = organizationService.saveInTripleStore( query);

            if(response.getStatusCodeValue() == 200){
                return ResponseEntity.ok(new MessageResponse("Organization registered successfully!"));
            }
            return response;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //return ResponseEntity.ok(new MessageResponse("Place registered successfully!"));
    }


    @PostMapping("/member/add")
    public ResponseEntity<?> agregarMiembro(@RequestBody MemberRequest memberRequest){
        String query = organizationService.queryAddMember(memberRequest);
        ResponseEntity<?> response = organizationService.saveInTripleStore( query);
        if(response.getStatusCodeValue()==200){
            return ResponseEntity.ok(new MessageResponse("Add member successfully!"));
        }else{
            return response;
        }
    }

    @PostMapping("/region/add")
    public ResponseEntity<?> vincularConRegion(@RequestBody OrganizationRegionRequest organizationRegionRequest){

        String query = organizationService.queryVincularRegion(organizationRegionRequest);
        ResponseEntity<?> response = organizationService.saveInTripleStore( query);
        if(response.getStatusCodeValue()==200){
            return ResponseEntity.ok(new MessageResponse("Add member successfully!"));
        }else{
            return response;
        }
    }




}

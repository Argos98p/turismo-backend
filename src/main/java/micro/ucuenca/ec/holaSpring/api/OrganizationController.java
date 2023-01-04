package micro.ucuenca.ec.holaSpring.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import micro.ucuenca.ec.holaSpring.model.MemberRequest;
import micro.ucuenca.ec.holaSpring.model.Organization;
import micro.ucuenca.ec.holaSpring.model.OrganizationRegionRequest;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.model.response.OrgAll;
import micro.ucuenca.ec.holaSpring.model.response.UserInfo;
import micro.ucuenca.ec.holaSpring.model.sparqlResponse.Binding;
import micro.ucuenca.ec.holaSpring.model.sparqlResponse.Root;
import micro.ucuenca.ec.holaSpring.payload.response.MessageResponse;
import micro.ucuenca.ec.holaSpring.service.OrganizationService;
import org.apache.jena.base.Sys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizacion")
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

            if(organization.getName()==null ){
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
        System.out.println(query);
        ResponseEntity<?> response = organizationService.saveInTripleStore( query);
        if(response.getStatusCodeValue()==200){
            return ResponseEntity.ok(new MessageResponse("Add region admin successfully!"));
        }else{
            return response;
        }
    }

    @GetMapping("all")
    public ResponseEntity<?> orgByUser(@RequestParam String user){
        /*ResponseEntity<?> response = organizationService.queryInfoOrganizacion(orgId);*/
        ResponseEntity<?> response = organizationService.queryGetAllOrgByUser(user);
        String responseString = response.getBody().toString();
        System.out.println(responseString);
        ObjectMapper m = new ObjectMapper();
        try {
            Root sparlResponse = m.readValue(responseString,new TypeReference<Root>() {});
            OrgAll orgAll = new OrgAll();
            ArrayList<Binding> info = sparlResponse.getSparql().getResults().getResult().getBinding();

            for (Binding bin : info){
                String atributo = bin.getName();

                if(Objects.equals(atributo, "orgName")){
                   orgAll.setNombre(bin.getLiteral().toString());
                }else if(Objects.equals(atributo, "org")){
                   orgAll.setId(bin.getLiteral().toString());
                } else if (Objects.equals(atributo, "regiones")) {
                   orgAll.setRegiones(bin.getLiteral().toString());
                }
            }

            return  new ResponseEntity<>(orgAll, HttpStatus.OK);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }




}

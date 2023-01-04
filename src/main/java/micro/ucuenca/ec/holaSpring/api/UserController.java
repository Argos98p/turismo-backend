package micro.ucuenca.ec.holaSpring.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import micro.ucuenca.ec.holaSpring.model.response.UserInfo;
import micro.ucuenca.ec.holaSpring.model.sparqlResponse.Binding;
import micro.ucuenca.ec.holaSpring.model.sparqlResponse.Root;
import micro.ucuenca.ec.holaSpring.model.sparqlResponse.Sparql;
import micro.ucuenca.ec.holaSpring.service.UserService;
import org.apache.jena.base.Sys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @CrossOrigin
    @GetMapping("info")
    public ResponseEntity<?> getUserInfo(@RequestParam("user") String user){
        ResponseEntity<?>response = userService.userInfo(user);
        if(response.getStatusCodeValue()==200){
            String responseString = response.getBody().toString();
            System.out.println(responseString);
            ObjectMapper m = new ObjectMapper();
            try {
                Root sparlResponse = m.readValue(responseString,new TypeReference<Root>() {});
                UserInfo userInfo = new UserInfo();
                ArrayList<Binding> info = sparlResponse.getSparql().getResults().getResult().getBinding();

                for (Binding bin : info){
                    String atributo = bin.getName();

                    if(Objects.equals(atributo, "role")){
                        userInfo.setRole(bin.getLiteral().toString());
                    }else if(Objects.equals(atributo, "mail")){
                        userInfo.setEmail(bin.getLiteral().toString());
                    }else if(Objects.equals(atributo, "org")){
                        userInfo.setOrgId(bin.getLiteral().toString());
                    } else if (Objects.equals(atributo, "user")) {
                        userInfo.setId(bin.getLiteral().toString());
                    }
                }

                return  new ResponseEntity<>(userInfo, HttpStatus.OK);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }else{

        }
        return response;
    }
}

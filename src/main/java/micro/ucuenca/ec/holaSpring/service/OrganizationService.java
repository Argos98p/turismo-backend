package micro.ucuenca.ec.holaSpring.service;

import micro.ucuenca.ec.holaSpring.Utils.SparlQueryInsert;
import micro.ucuenca.ec.holaSpring.model.MemberRequest;
import micro.ucuenca.ec.holaSpring.model.Organization;
import micro.ucuenca.ec.holaSpring.model.Place;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static micro.ucuenca.ec.holaSpring.service.PlaceService.getBasicAuthenticationHeader;

@Service
public class OrganizationService {
    public String toSparqlInsert(Organization organization){
        SparlQueryInsert insertSparql = new SparlQueryInsert();
        String basePlace="myorg:"+organization.getId();
        insertSparql.setBaseUri("http://turis-ucuenca/");
        insertSparql.setBase("http://turis-ucuenca/");
        insertSparql.setPrefix("tp","http://tour-pedia.org/download/tp.owl");
        insertSparql.setPrefix("vcard", "http://www.w3.org/2006/vcard/ns#");
        insertSparql.setPrefix("myorg", "http://turis-ucuenca/org/");
        insertSparql.setPrefix("org","http://www.w3.org/TR/vocab-org/");
        insertSparql.setTriple(basePlace, "a", "org:Organization");
        insertSparql.setTriple(basePlace, "vcard:organization-name", "\""+organization.getName()+"\"");
        insertSparql.setTriple(basePlace, "vcard:locality", "\""+organization.getParroquia()+"\"");
        insertSparql.setTriple(basePlace, "vcard:postal-code", "\""+organization.getPostalCode()+"\"");
        //insertSparql.setTriple(basePlace, "vcard:hasLogo", "\""+organization.getLogoUrl()+"\"");
        return insertSparql.build();
    }


    public String queryAddMember(MemberRequest request){
        SparlQueryInsert insertSparql = new SparlQueryInsert();
        insertSparql.setBaseUri("http://turis-ucuenca/");
        insertSparql.setBase("http://turis-ucuenca/");
        insertSparql.setPrefix("myorg","http://turis-ucuenca/org/");
        insertSparql.setPrefix("org","http://www.w3.org/TR/vocab-org/");
        insertSparql.setPrefix("myusers","http://turis-ucuenca/user/");
        insertSparql.setTriple("myusers:"+request.getUserId(),"org:memberOf", "myorg:"+request.getOrganizationId());
        insertSparql.setTriple("myorg:"+request.getOrganizationId(),"org:hasMember","myusers:"+request.getUserId());
        return insertSparql.build();
    }

    public ResponseEntity<?> saveInTripleStore(String query){
        String url = "https://sd-e3dfa127.stardog.cloud:5820/Turismo2";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/sparql-update");
        map.put("Authorization", getBasicAuthenticationHeader("ricardo.jarro98@ucuenca.edu.ec", "Chocolate619@"));

        headers.setAll(map);

        HttpEntity<?> request = new HttpEntity<>(query, headers);

        ResponseEntity<?> response = new RestTemplate().postForEntity(url+"/update", request, String.class);
        System.out.println(response);

        return response;
    }


}

package micro.ucuenca.ec.holaSpring.service;

import micro.ucuenca.ec.holaSpring.Utils.SparlQueryInsert;
import micro.ucuenca.ec.holaSpring.database.TriplestoreConnection;
import micro.ucuenca.ec.holaSpring.model.MemberRequest;
import micro.ucuenca.ec.holaSpring.model.Organization;
import micro.ucuenca.ec.holaSpring.model.OrganizationRegionRequest;
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

    TriplestoreConnection triplestoreConnection;
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
        //insertSparql.setTriple(basePlace, "vcard:locality", "\""+organization.getParroquia()+"\"");
        //insertSparql.setTriple(basePlace, "vcard:postal-code", "\""+organization.getPostalCode()+"\"");
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

    public ResponseEntity<?> saveInTripleStore(String data){
        triplestoreConnection = new TriplestoreConnection();
        return triplestoreConnection.PostToTriplestore(data);
    }


    public String queryVincularRegion(OrganizationRegionRequest organizationRegionRequest) {
        SparlQueryInsert insertSparql = new SparlQueryInsert();
        insertSparql.setBaseUri("http://turis-ucuenca/");
        insertSparql.setBase("http://turis-ucuenca/");
        insertSparql.setPrefix("myorg","http://turis-ucuenca/org/");
        insertSparql.setPrefix("myregiones","http://turis-ucuenca/regiones/");
        insertSparql.setTriple("myregiones:"+organizationRegionRequest.getRegionId(),":isAdminBy","myorg:"+organizationRegionRequest.getOrganizationId());
        insertSparql.setTriple("myorg:"+organizationRegionRequest.getOrganizationId(),":admin","myregiones:"+organizationRegionRequest.getRegionId());
        return insertSparql.build();
    }
}

package micro.ucuenca.ec.holaSpring.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import micro.ucuenca.ec.holaSpring.model.Place;
import micro.ucuenca.ec.holaSpring.service.FileStorageService;
import micro.ucuenca.ec.holaSpring.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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

    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    @PostMapping("/add")
    public Place newPlace(@RequestParam("model") String jsonObject , @RequestParam("files")MultipartFile[] files) throws IOException, InterruptedException {

        Place place;
        place = objectMapper.readValue(jsonObject, Place.class);
        String namePlace = place.getTitle();

        Arrays.asList(files).stream().forEach(file -> {
            String fileStorePath = fileStorageService.storeFile(file, namePlace);
            ServletUriComponentsBuilder.fromCurrentContextPath().path(fileStorePath).toUriString();
            place.addImagePath(fileStorePath);
        });
        String query = placeService.toSparqlInsert(place);

        String url = "https://sd-e3dfa127.stardog.cloud:5820/Turismo";

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/sparql-update");
        map.put("Authorization", getBasicAuthenticationHeader("ricardo.jarro98@ucuenca.edu.ec", "Chocolate619@"));

        headers.setAll(map);

        Map<String, String> req_payload = new HashMap<>();
        req_payload.put("name", "piyush");

        HttpEntity<?> request = new HttpEntity<>(query, headers);

        ResponseEntity<?> response = new RestTemplate().postForEntity(url, request, String.class);

        //ServiceResponse entityResponse = (ServiceResponse) response.getBody();
        System.out.println(response);


        /*

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", UriUtils.encode(query, StandardCharsets.UTF_8));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(builder.toUriString()))
                .header("Authorization", getBasicAuthenticationHeader("ricardo.jarro98@ucuenca.edu.ec", "Chocolate619@"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(httpResponse);*/
        return null;
    }
}
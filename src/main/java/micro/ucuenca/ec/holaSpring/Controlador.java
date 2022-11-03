package micro.ucuenca.ec.holaSpring;

import micro.ucuenca.ec.holaSpring.domain.Placev2;
import micro.ucuenca.ec.holaSpring.model.Place;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;


@RestController
@ResponseBody
@RequestMapping("/api/place")
public class Controlador {

    private final RestTemplate restTemplate;

    public Controlador(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    //@PostMapping("/add")
    /*
    @RequestMapping(path = "/add", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String addNewPlace(@RequestPart Place place,@RequestParam("image") MultipartFile file) throws URISyntaxException {

        System.out.println(file);

        String url = "https://sd-e3dfa127.stardog.cloud:5820/Turismo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("ricardo.jarro98@ucuenca.edu.ec", "Chocolate619@");
        HttpEntity<?> entity = new HttpEntity<>(headers);


        System.out.println(UriUtils.encodeQuery("prefix : <http://tour-pedia.org/download/tp.owl> \n" +
                "SELECT * WHERE { ?f a :Place. }", StandardCharsets.UTF_8));



        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", "").encode()
                .toUriString();
        System.out.println(urlTemplate);
        System.out.println(place);

        return "ok";
    }
*/



}

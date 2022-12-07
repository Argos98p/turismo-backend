package micro.ucuenca.ec.holaSpring.fb;
import micro.ucuenca.ec.holaSpring.model.FbPhoto;
import org.apache.jena.base.Sys;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class FbConnection {
    public static String SendData(String file) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        // ContentType
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer EAAmqhoYHuBsBAMifA6cxIiP5a9jAZBd76MQXhwacJjHivbd044vnL0V5Gh3dDXHmm1FOBrlOAf7gOUvCR75vAZBxvXZAwUZCBeTjWqrZCnQf2iZAiOydzMAZBb2LWuTCCk8v4UI3yOrV4dilfoLubrZAoTNHasWuXTVmwXZBk3H5o3NtSJPpcChVt");
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        // Load a file from disk.
        Resource file1 = new FileSystemResource(file);
        multipartBodyBuilder.part("avatar", file1, MediaType.IMAGE_JPEG);
        // multipart/form-data request body
        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();
        // The complete http request body.
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);
        ResponseEntity<FbPhoto> responseEntity = restTemplate.postForEntity("https://graph.facebook.com/v15.0/165980483492633/photos", httpEntity,
                FbPhoto.class);

        if (responseEntity.getStatusCodeValue() == 200) {
            HttpHeaders headerqs = new HttpHeaders();
            headerqs.add("Authorization", "Bearer EAAmqhoYHuBsBAIEgFsPJlG6KgheM9PwieLe2HgqOdQZAlSGZBQwgf3qJVhIwaoNtChbyx8TAL0mqpFNZCJq1bj1cImkKlom8r0dTrTGlvsJFBEJEptmUieQowBxLRKOe16Yj8vRlnbIIwfRonZAYjzZBGGczNoIWF3jLGv46kuBR21t4spw94");
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headerqs);

            ResponseEntity<String> response = null;

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com/v15.0/" + responseEntity.getBody().getId() + "?fields=images");

            try {
                response = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.GET,
                        entity, String.class);
                return response.getBody();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static ArrayList<String>ToFacebook(ArrayList<String> filesPaths){
        ArrayList<String> imagesSrc=new ArrayList<>();
        for(int i = 0; i< filesPaths.size();i++){
            String resultJson = SendData(filesPaths.get(i));
            if(resultJson !=null){
                JSONParser jp = new JSONParser();
                JSONObject result = null;
                try {
                    result = (JSONObject) jp.parse(resultJson);
                    JSONArray images = (JSONArray) result.get("images");
                    long maxAncho=0;
                    String urlImage="";
                    for(int j = 0 ;j<images.size();j++){
                        JSONObject image= (JSONObject) images.get(j);
                        long ancho = (long) image.get("width");
                        if(maxAncho<ancho){
                            maxAncho=ancho;
                            urlImage= (String) image.get("source");

                        }
                    }
                    imagesSrc.add(urlImage);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        System.out.println(imagesSrc);
        return imagesSrc;
    }
}

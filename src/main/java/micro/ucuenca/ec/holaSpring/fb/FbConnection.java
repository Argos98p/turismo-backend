package micro.ucuenca.ec.holaSpring.fb;
import micro.ucuenca.ec.holaSpring.Utils.Request;
import micro.ucuenca.ec.holaSpring.model.FbPhoto;
import micro.ucuenca.ec.holaSpring.utils.Utils;
import org.apache.jena.base.Sys;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FbConnection {

    private static String tk="Bearer ";
    public static String SendData(String file) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        // ContentType
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", tk);
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
            headerqs.add("Authorization", tk);
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

    public static ArrayList<String> getImagesSrcById (ArrayList<String> fb_ids){
        ArrayList<String> fbIds= new ArrayList<>();
        for(int i = 0; i< fb_ids.size();i++){
            String myUrl="https://graph.facebook.com/v15.0/"+fb_ids.get(i)+"?fields=images";
            System.out.println(myUrl);
            ResponseEntity<?> response = Request.simpleGetRequest(myUrl,tk);
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Request Successful.");
                JSONParser jp = new JSONParser();
                JSONObject result = null;
                try {
                    result = (JSONObject) jp.parse(Objects.requireNonNull(response.getBody()).toString());
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
                    fbIds.add(urlImage);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Request Failed");
                System.out.println(response.getStatusCode());
            }

            /*

            String resultJson = SendData(filesPaths.get(i));
            System.out.println(resultJson);

            if(resultJson !=null){


            }*/
        }


        return fbIds;

    }
    public static  ArrayList<ArrayList<String>>  ToFacebook(ArrayList<String> filesPaths){
        ArrayList<ArrayList<String>> srcs_ids=new ArrayList<>();
        ArrayList<String> imagesSrc=new ArrayList<>();
        ArrayList<String> fbImagesId = new ArrayList<>();
        for(int i = 0; i< filesPaths.size();i++){
            String resultJson = SendData(filesPaths.get(i));
            System.out.println(resultJson);

            if(resultJson !=null){
                JSONParser jp = new JSONParser();
                JSONObject result = null;
                try {
                    result = (JSONObject) jp.parse(resultJson);
                    JSONArray images = (JSONArray) result.get("images");
                    String imageId = (String) result.get("id");
                    fbImagesId.add(imageId);
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

        srcs_ids.add(imagesSrc);
        srcs_ids.add(fbImagesId);

        return srcs_ids;
    }
}

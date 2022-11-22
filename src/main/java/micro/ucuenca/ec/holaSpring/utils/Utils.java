package micro.ucuenca.ec.holaSpring.utils;

import org.apache.jena.base.Sys;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.Base64;

public class Utils {

    public static String convertXMLtoJSON(String xml){
        System.out.println(xml);
        try {
            JSONObject json = XML.toJSONObject(xml);
            String jsonString = json.toString(4);
            System.out.println(jsonString);
            return jsonString;

        }catch (JSONException e) {
// TODO: handle exception
            System.out.println(e.toString());
            return "error";
        }

    }
}

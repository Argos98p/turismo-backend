package micro.ucuenca.ec.holaSpring;

import lombok.extern.slf4j.Slf4j;
import micro.ucuenca.ec.holaSpring.domain.Place;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;


@Controller
@Slf4j
public class Controlador {

    @Value("${index.saludo}")
    private String saludo;
    private List<Place> places = new ArrayList();

    @GetMapping("/inicio")
    public String inicio(Model model){
        //var saludar = "Mensaje asdas con thyleaf";
        /*var place = new Place();
        place.setLatitud("1.123456");
        place.setLongitud("78.12456");
        place.setAddress("direccion 1");
        place.setLocation("Cuenca");
        place.setLink("www.link1.com");
        place.setTitle("lugar 1");
        place.setLabel("label 1");
        place.setFn("prueba 1");

        var place2 = new Place();
        place2.setLatitud("0.123456");
        place2.setLongitud("80.987456");
        place2.setAddress("direccion 2");
        place2.setLocation("Cuenca");
        place2.setLink("www.link2.com");
        place2.setTitle("lugar 2");
        place2.setLabel("label 2");
        place2.setFn("prueba 2");

        places.add(place);
        places.add(place2);*/

        //Visualizar en el navegador
        //model.addAttribute("saludar",saludar);
        //model.addAttribute("saludo",saludo);
        //model.addAttribute("persona",persona);
        model.addAttribute("places",places);
        log.info("Ejecutando el controlador spring MVC");
        return "index";
    }
    @GetMapping("/agregar")
    public String agregar(Place place){
        return "modificar";
    }

    @PostMapping("/guardar")
    public String guardar(Place place){
        places.add(place);
        return "redirect:/inicio";
    }

    @PostMapping("/guardarJson")
    public String agregarPeticion(@RequestBody Place place){
        places.add(place);
        return "redirect:/inicio";
    }


}

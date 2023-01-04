package micro.ucuenca.ec.holaSpring.model.sparqlResponse;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Result {
    @Getter
    @Setter
    public ArrayList<Binding> binding;
}

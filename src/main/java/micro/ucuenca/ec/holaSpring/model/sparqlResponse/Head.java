package micro.ucuenca.ec.holaSpring.model.sparqlResponse;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Head {
    @Getter
    @Setter
    public ArrayList<Variable> variable;
}

package micro.ucuenca.ec.holaSpring.model.sparqlResponse;

import lombok.Getter;
import lombok.Setter;

public class Binding {
    @Getter
    @Setter
    public String name;
    @Getter @Setter
    public Object literal;
}

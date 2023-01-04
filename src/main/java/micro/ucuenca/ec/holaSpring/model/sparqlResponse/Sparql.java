package micro.ucuenca.ec.holaSpring.model.sparqlResponse;

import lombok.Getter;
import lombok.Setter;

public class Sparql {
    @Getter
    @Setter
    public Head head;
    @Getter
    @Setter
    public String xmlns;
    @Getter
    @Setter
    public Results results;
}

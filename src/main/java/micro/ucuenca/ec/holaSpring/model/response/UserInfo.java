package micro.ucuenca.ec.holaSpring.model.response;

import lombok.Getter;
import lombok.Setter;

public class UserInfo {
    @Getter
    @Setter
    String id;
    @Getter
    @Setter
    String email;
    @Getter
    @Setter
    String orgId;
    @Getter
    @Setter
    String role;
}

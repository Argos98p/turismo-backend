package micro.ucuenca.ec.holaSpring.service;

import micro.ucuenca.ec.holaSpring.Utils.SparqlTemplates;
import micro.ucuenca.ec.holaSpring.database.TriplestoreConnection;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    TriplestoreConnection triplestoreConnection;

    public ResponseEntity<?> userInfo(String userId){

        String query = SparqlTemplates.getUserInfo(userId);
        triplestoreConnection = new TriplestoreConnection();
        return triplestoreConnection.QueryTriplestore(query);
    }
}

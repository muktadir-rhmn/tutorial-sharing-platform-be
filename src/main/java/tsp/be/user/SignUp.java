package tsp.be.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.error.MappedValidationException;
import tsp.be.error.SimpleValidationException;
import tsp.be.user.auth.SigninNotRequired;
import tsp.be.user.models.UserRepository;

class SignupRequest {
    public String name;
    public String email;
    public String password;
}

class SignupResponse {
    public String message;
}

@RestController
public class Signup {
    @Autowired
    private UserRepository userRepository;

    @SigninNotRequired
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public SignupResponse signup(@RequestBody SignupRequest signup) {
        validate(signup);
        userRepository.createUser(signup.name, signup.email, signup.password);

        SignupResponse response = new SignupResponse();
        response.message = "Signup Success";
        return response;
    }

    private void validate(SignupRequest signup) {
        if (signup == null) throw new SimpleValidationException("No signup data provided");

        MappedValidationException mappedValidationException = new MappedValidationException();

        if (signup.email == null || signup.email.length() == 0) mappedValidationException.put("email", "You must put email address");
        if (signup.name == null || signup.name.length() == 0) mappedValidationException.put("name", "You must give a user name");
        if (signup.password == null || signup.password.length() < 8) mappedValidationException.put("password", "Password must conatain at least 8 characters");

        if (!mappedValidationException.isEmpty()) throw mappedValidationException;
    }
}

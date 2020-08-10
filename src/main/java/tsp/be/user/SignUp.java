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

import static tsp.be.user.MetaData.USER_MODULE_URL_PREFIX;

class SignUpRequest {
    public String name;
    public String email;
    public String password;
}

class SignUpResponse {
    public String message;
}

@RestController
public class SignUp {
    @Autowired
    private UserRepository userRepository;

    @SigninNotRequired
    @RequestMapping(value = USER_MODULE_URL_PREFIX + "/sign-up", method = RequestMethod.POST)
    public SignUpResponse signUp(@RequestBody SignUpRequest signUp) {
        validate(signUp);
        userRepository.createUser(signUp.name, signUp.email, signUp.password);

        SignUpResponse response = new SignUpResponse();
        response.message = "Sign Up Success";
        return response;
    }

    private void validate(SignUpRequest signup) {
        if (signup == null) throw new SimpleValidationException("No signup data provided");

        MappedValidationException mappedValidationException = new MappedValidationException();

        if (signup.email == null || signup.email.length() == 0) mappedValidationException.put("email", "You must put email address");
        if (signup.name == null || signup.name.length() == 0) mappedValidationException.put("name", "You must give a user name");
        if (signup.password == null || signup.password.length() < 8) mappedValidationException.put("password", "Password must conatain at least 8 characters");

        if (!mappedValidationException.isEmpty()) throw mappedValidationException;
    }
}

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
import tsp.be.utils.Validator;

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

    private void validate(SignUpRequest request) {
        MappedValidationException errors = new MappedValidationException();

        if (Validator.isEmptyString(request.email)) errors.addError("email", "Email is required");
        if (Validator.isEmptyString(request.name)) errors.addError("name", "User name is required");
        if (request.password == null || request.password.length() < 8) errors.addError("password", "Password must contain at least 8 characters");

        errors.throwIfAnyError();
    }
}

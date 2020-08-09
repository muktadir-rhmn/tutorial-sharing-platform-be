package tsp.be.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tsp.be.error.SimpleValidationException;
import tsp.be.user.auth.SigninNotRequired;
import tsp.be.user.auth.TokenManager;
import tsp.be.user.models.User;
import tsp.be.user.models.UserRepository;

class SigninRequest {
    public String email;
    public String password;
}

class SigninResponse {
    public String message;
    public String token;
    public String userID;
    public String userName;
}

@RestController
public class Signin {

    @Autowired
    private UserRepository userRepository;
    private TokenManager tokenManager = TokenManager.getInstance();

    @SigninNotRequired
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public SigninResponse signin(@RequestBody SigninRequest signin) {
        validate(signin);
        SigninResponse response = manageSignin(signin);
        return response;
    }

    private void validate(SigninRequest signin) {

    }

    private SigninResponse manageSignin(SigninRequest signin) {
        User user = userRepository.getUserByEmail(signin.email);
        if (user == null || !user.password.equals(signin.password)) throw new SimpleValidationException("Email & password does not match any account");

        String token = tokenManager.generateToken(user.id, user.name, user.email);

        SigninResponse response = new SigninResponse();
        response.message = "Signin successful";
        response.token = token;
        response.userID = user.id;
        response.userName = user.name;

        return response;
    }

}

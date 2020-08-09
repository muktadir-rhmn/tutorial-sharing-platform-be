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

import static tsp.be.user.MetaData.USER_MODULE;

class SignInRequest {
    public String email;
    public String password;
}

class SignInResponse {
    public String message;
    public String token;
    public String userID;
    public String userName;
}

@RestController
public class SignIn {

    @Autowired
    private UserRepository userRepository;
    private TokenManager tokenManager = TokenManager.getInstance();

    @SigninNotRequired
    @RequestMapping(value = USER_MODULE + "/sign-in", method = RequestMethod.POST)
    public SignInResponse signIn(@RequestBody SignInRequest signIn) {
        validate(signIn);
        SignInResponse response = manageSignIn(signIn);
        return response;
    }

    private void validate(SignInRequest signIn) {

    }

    private SignInResponse manageSignIn(SignInRequest signIn) {
        User user = userRepository.getUserByEmail(signIn.email);
        if (user == null || !user.password.equals(signIn.password)) throw new SimpleValidationException("Email & password does not match any account");

        String token = tokenManager.generateToken(user.id, user.name, user.email);

        SignInResponse response = new SignInResponse();
        response.message = "Sign in successful";
        response.token = token;
        response.userID = user.id;
        response.userName = user.name;

        return response;
    }

}

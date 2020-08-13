package tsp.be.user.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import tsp.be.config.ConfigurationManager;
import tsp.be.config.pojos.JWTConfiguration;
import tsp.be.user.UserDescriptor;

import java.util.Map;

/**
 * every value in the claim of tokens must be string
 */
public class TokenManager {
    private static final TokenManager instance = new TokenManager();
    public static TokenManager getInstance() {
        return instance;
    }

    private JWTConfiguration configuration;
    private Algorithm signingAlgorithm;
    private JWTVerifier tokenVerifier;

    private TokenManager() {
        configuration = ConfigurationManager.getJWTConfiguration();
        signingAlgorithm = Algorithm.HMAC256(configuration.secretKey);
        tokenVerifier = JWT.require(signingAlgorithm)
                .withIssuer(configuration.issuer)
                .build();
    }

    public String generateToken(String userID, String userEmail, String userName) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer(configuration.issuer)
                    .withClaim("userID", userID)
                    .withClaim("userEmail", userEmail)
                    .withClaim("userName", userName)
                    .sign(signingAlgorithm);
        } catch (JWTCreationException exception){
            exception.printStackTrace();
        }

        return token;
    }

    public UserDescriptor verifyTokenAndDecodeData(String token) {
        if (token == null) return null;

        try {

            DecodedJWT decodedToken = tokenVerifier.verify(token);
            Map<String, Claim> claimMap = decodedToken.getClaims();
            String userID = claimMap.get("userID").asString();
            String userEmail = claimMap.get("userEmail").asString();
            String userName = claimMap.get("userName").asString();


            return new UserDescriptor(userID, userEmail, userName);
        } catch (JWTCreationException | JWTDecodeException exception){
            return null;
        }
    }
}

package messenger.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import messenger.config.ConfigurationManager;
import messenger.config.pojos.JWTConfiguration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenManager {

    public enum TOKEN_TYPE {
        USER,
        INITIATOR,
    }

    private JWTConfiguration configuration;
    private Algorithm signingAlgorithm;
    private JWTVerifier tokenVerifier;

    public TokenManager() {
        configuration = ConfigurationManager.getJWTConfiguration();
        signingAlgorithm = Algorithm.HMAC256(configuration.secretKey);
        tokenVerifier = JWT.require(signingAlgorithm)
                .withIssuer(configuration.issuer)
                .build();
    }

    public String generateTokenForUser(Long userID, String userName, String email) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer(configuration.issuer)
                    .withClaim("tokenType", TOKEN_TYPE.USER.toString())
                    .withClaim("userID", userID.toString())
                    .withClaim("userName", userName)
                    .withClaim("email", email)
                    .sign(signingAlgorithm);
        } catch (JWTCreationException exception){
            exception.printStackTrace();
        }

        return token;
    }

    public String generateTokenForInitiator(String initiatorName) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer(configuration.issuer)
                    .withClaim("tokenType", TOKEN_TYPE.INITIATOR.toString())
                    .withClaim("initiatorName", initiatorName)
                    .sign(signingAlgorithm);
        } catch (JWTCreationException exception){
            exception.printStackTrace();
        }

        return token;
    }

    public Map<String, String> verifyToken(String token) {
        Map<String, String> playloadData = new HashMap<>();
        try {
            DecodedJWT decodedToken = tokenVerifier.verify(token);
            Map<String, Claim> claimMap = decodedToken.getClaims();


            for (String key: claimMap.keySet()) {
                playloadData.put(key, claimMap.get(key).asString());
            }
        } catch (JWTCreationException exception){
            //todo: handle this case
        }

        return playloadData;
    }
}

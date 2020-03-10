package messenger.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import messenger.config.ConfigurationManager;
import messenger.config.pojos.JWTConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * every value in the claim of tokens must be string
 */
public class TokenManager {
    private static final TokenManager instance = new TokenManager();
    public static TokenManager getInstance() {
        return instance;
    }

    public enum USER_TYPE {
        SIGNED_IN,
        INITIATOR,
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

    public String generateTokenForUser(Long userID, String userName, String email) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer(configuration.issuer)
                    .withClaim("userType", USER_TYPE.SIGNED_IN.toString())
                    .withClaim("userID", userID.toString())
                    .withClaim("userName", userName)
                    .withClaim("email", email)
                    .sign(signingAlgorithm);
        } catch (JWTCreationException exception){
            exception.printStackTrace();
        }

        return token;
    }

    public String generateTokenForInitiator(Long threadID, String initiatorName) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer(configuration.issuer)
                    .withClaim("userType", USER_TYPE.INITIATOR.toString())
                    .withClaim("initiatorName", initiatorName)
                    .withClaim("threadID", threadID.toString())
                    .sign(signingAlgorithm);
        } catch (JWTCreationException exception){
            exception.printStackTrace();
        }

        return token;
    }

    public boolean verifyTokenAndSetRequestAttr(String token, HttpServletRequest request) {
        try {
            DecodedJWT decodedToken = tokenVerifier.verify(token);
            Map<String, Claim> claimMap = decodedToken.getClaims();

            for (String key: claimMap.keySet()) {
                request.setAttribute(key, claimMap.get(key).asString());
            }
        } catch (JWTCreationException exception){
            return false;
        }
        return true;
    }
}

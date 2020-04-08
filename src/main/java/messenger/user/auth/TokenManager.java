package messenger.user.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import messenger.config.ConfigurationManager;
import messenger.config.pojos.JWTConfiguration;
import messenger.db.models.User;
import messenger.user.UserDescriptor;

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
                    .withClaim("userType", UserDescriptor.USER_TYPE_SINGED_IN)
                    .withClaim("userName", userName)
                    .withClaim("userID", userID)
                    .sign(signingAlgorithm);
        } catch (JWTCreationException exception){
            exception.printStackTrace();
        }

        return token;
    }

    public String generateTokenForInitiator(Long userID, Long threadID, String initiatorName) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer(configuration.issuer)
                    .withClaim("userType", UserDescriptor.USER_TYPE_INITIATOR)
                    .withClaim("userName", initiatorName)
                    .withClaim("userID", userID)
                    .withClaim("threadID", threadID)
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
            String userType = claimMap.get("userType").asString();
            String userName = claimMap.get("userName").asString();
            long userID = claimMap.get("userID").asLong();

            Long threadID;
            if (userType.equals(UserDescriptor.USER_TYPE_INITIATOR)) threadID = claimMap.get("threadID").asLong();
            else threadID = null;

            return new UserDescriptor(userType, userName, userID, threadID);
        } catch (JWTCreationException | JWTDecodeException exception){
            return null;
        }
    }
}

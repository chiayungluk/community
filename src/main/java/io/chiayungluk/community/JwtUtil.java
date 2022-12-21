package io.chiayungluk.community;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
public class JwtUtil {
    public static UserDto verifyJWTToken(String token, String secret) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        ObjectMapper om = new ObjectMapper();
        try {
            String payload = new String(Base64.getUrlDecoder().decode(jwt.getPayload()), StandardCharsets.UTF_8);
            return om.readValue(payload, UserDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
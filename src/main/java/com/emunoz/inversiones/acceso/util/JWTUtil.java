package com.emunoz.inversiones.acceso.util;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * @author Mahesh
 */
@Component
@Log4j2
public class JWTUtil {
    @Value("${security.jwt.secret}")
    private String key;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.ttlMillis}")
    private long ttlMillis;

    /**
     * Create a new token.
     *
     * @param id
     * @param subject
     * @param permission
     * @return
     */
    public String create(String id, String subject, String permission) {

        // The JWT signature algorithm used to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //  sign JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());


        //  set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer).claim("permission", permission)
                .signWith(signatureAlgorithm, signingKey);

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    /**
     * Method to validate and read the JWT
     *
     * @param jwt
     * @return
     */
    public String getValue(String jwt) {
        log.info("Token email {}",jwt);
        // This line will throw an exception if it is not a signed JWS (as
        // expected)
        Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key))
                .parseClaimsJws(jwt).getBody();

        log.info("Token Email {}",claims.getSubject());
        return claims.getSubject();

    }

    /**
     * Method to validate and read the JWT
     *
     * @param jwt
     * @return
     */
    public String getKey(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as
        // expected)
        Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key))
                .parseClaimsJws(jwt).getBody();

        log.info("ID {}",claims.getId());
        return claims.getId();
    }


    public Integer getPermission(String jwt) {
        log.info("Token permission {}",jwt);
        // This line will throw an exception if it is not a signed JWS (as
        // expected)
        try {
            Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key))
                    .parseClaimsJws(jwt).getBody();

            return Integer.parseInt(claims.get("permission", String.class));
        } catch (SignatureException ex){
            log.error(ex.getMessage());
            return 0;
        }

    }

    public void verifyToken(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                    .parseClaimsJws(jwt)
                    .getBody();

            String id = claims.getId();
            String email = claims.getSubject();
            String permission = claims.get("permission", String.class);

            // Mostrar valores del token

        } catch (JwtException ex) {

        }
    }
}
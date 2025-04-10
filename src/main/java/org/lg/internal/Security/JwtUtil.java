package org.lg.internal.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.lg.Model.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;


public class JwtUtil {
    private final static String SECRET_KEY=generateSecretKey();
    private final static long EXPIRATION_TIME_MILLIS = 8 * 60 * 60 * 1000;

    private static String generateSecretKey() {
        byte[] key = new byte[32]; // 256 bits = 32 bytes
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    public static String generateToken(String uuid, String username, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_MILLIS);

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(uuid) // typically UUID or user ID
                .claim("username", username)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true; // Treat parsing errors as expired/invalid
        }
    }
    public static  Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
    public static boolean validateService(Service service) {
        if (service == null || service.getPassKey() == null) return false;
        return !isTokenExpired(service.getPassKey());
    }

    public static String getUserUUIDFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject(); // Assuming UUID is set as subject
    }
}

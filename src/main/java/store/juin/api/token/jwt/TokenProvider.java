package store.juin.api.token.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * 토큰 관련 설정을 담당하는 클래스,
 * 토큰을 발급하고 자격증명을 관리.
 */
@Slf4j
@Component
public class TokenProvider {
    private final Key secretKey;

    public TokenProvider(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String createToken(String username, long accessTokenValidationTime) {
        // FIXME: roles 설정해야 됨.
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("roles", "ROLE_ADMIN");

        final Date date = new Date(System.currentTimeMillis());

        return Jwts.builder()
//                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + accessTokenValidationTime))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Exception은 EntryPoint에서 핸들링함.
    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
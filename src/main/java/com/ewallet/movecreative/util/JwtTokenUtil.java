package com.ewallet.movecreative.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ewallet.movecreative.service.CustomUserDetails;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {

    private SecretKey jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${jwt.expiration.token}")
    private Long jwtExpirationToken;

    public String generateToken(CustomUserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();


        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpirationToken);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getId().toString(0))
                // .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }
    
    public String extractUsername(String token){
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser().setSigningKey(jwtSecretKey)
                        .parseClaimsJws(token)
                        .getBody()
                        .getExpiration();
                        
        return expiration.before(new Date());
    }


}

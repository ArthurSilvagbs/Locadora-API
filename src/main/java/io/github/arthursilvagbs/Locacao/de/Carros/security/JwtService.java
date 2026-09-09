package io.github.arthursilvagbs.Locacao.de.Carros.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

   private static final long EXPIRATION_TIME = 1000 * 60 * 60;

   @Value("${jwt.secret}")
   private String secret;

   public String generateToken(UserDetailsImpl userDetails) {
      return Jwts.builder()
         .subject(userDetails.getUsername())
         .claim("role", userDetails.getAuthorities())
         .issuedAt(new Date(System.currentTimeMillis()))
         .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
         .signWith(getSignInKey())
         .compact();
   }

   public String extractUsername(String token) {
      return extractClaim(token, Claims::getSubject);
   }

   public boolean isTokenValid(String token, UserDetails userDetails) {
      String username = extractUsername(token);
      return username.equals(userDetails.getUsername()) && !istokenExpired(token);
   }

   private boolean istokenExpired(String token) {
      return extractClaim(token, Claims::getExpiration).before(new Date());
   }

   private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
      Claims claims = extractAllClaim(token);
      return claimsResolver.apply(claims);
   }

   private Claims extractAllClaim(String token) {
      return Jwts.parser()
         .verifyWith(getSignInKey())
         .build()
         .parseSignedClaims(token)
         .getPayload();
   }

   private SecretKey getSignInKey() {
      byte[] keyBytes = secret.getBytes();
      return Keys.hmacShaKeyFor(keyBytes);
   }
}

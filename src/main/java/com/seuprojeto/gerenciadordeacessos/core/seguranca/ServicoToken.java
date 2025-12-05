package com.seuprojeto.gerenciadordeacessos.core.seguranca;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Serviço de Geração e Validação de Tokens JWT.
 * Responsabilidade Única: Gerenciar o ciclo de vida do token JWT.
 */
@Service
public class ServicoToken {

    @Value("${jwt.secret}")
    private String chaveSecreta;

    @Value("${jwt.expiration}")
    private long validadeToken;

    /**
     * Extrai o email (subject) do token.
     */
    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    /**
     * Extrai uma claim específica do token.
     */
    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodasClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Gera um token JWT para o UserDetails.
     */
    public String gerarToken(UserDetails userDetails) {
        return gerarToken(new HashMap<>(), userDetails);
    }

    /**
     * Gera um token JWT com claims extras.
     */
    public String gerarToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validadeToken))
                .signWith(getChaveAssinatura(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida se o token é válido (não expirado e corresponde ao usuário).
     */
    public boolean validarToken(String token, UserDetails userDetails) {
        final String email = extrairEmail(token);
        return (email.equals(userDetails.getUsername())) && !tokenExpirado(token);
    }

    /**
     * Valida se o token é válido (apenas não expirado).
     */
    public boolean validarToken(String token) {
        return !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return extrairDataExpiracao(token).before(new Date());
    }

    private Date extrairDataExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    private Claims extrairTodasClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getChaveAssinatura())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getChaveAssinatura() {
        byte[] keyBytes = Decoders.BASE64.decode(chaveSecreta);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

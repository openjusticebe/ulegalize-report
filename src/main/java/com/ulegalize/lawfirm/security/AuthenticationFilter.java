package com.ulegalize.lawfirm.security;

import com.auth0.jwk.GuavaCachedJwkProvider;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.ulegalize.dto.AppMetadata;
import com.ulegalize.lawfirm.model.LawfirmToken;
import com.ulegalize.lawfirm.rest.LawfirmV2Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {


    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String BEARER = "Bearer ";

    @Value("${app.lawfirm.url}")
    private String lawfirmUrl;

    @Value("${app.auth0.domain}")
    private String AUTH0_DOMAIN;

    @Autowired
    private LawfirmV2Api lawfirmV2Api;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        //CORS not allowed with multiple answer
        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equalsIgnoreCase(request.getMethod())) {
//			response.setHeader("Access-Control-Allow-Headers", "Content-type,Accept,X-Access-Token,X-Key");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.addHeader("Access-Control-Allow-Headers", "X-Key");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            response.addHeader("Access-Control-Allow-Headers", "Accept");
            response.addHeader("Access-Control-Allow-Headers", "X-Access-Token");
            response.addHeader("Access-Control-Allow-Headers", "delimiter");
            response.addHeader("Access-Control-Allow-Headers", "dropbox_token");
            response.addHeader("Access-Control-Allow-Headers", "containershare");
//			response.setHeader("Access-Control-Allow-Headers", "Content-type,Accept,X-Access-Token,X-Key");

            response.addHeader("Access-Control-Max-Age", "3600");
        }
        final String authBearerHeader = request.getHeader(this.AUTHORIZATION_HEADER);

        log.debug(" {} Authorization token", authBearerHeader);
        if (authBearerHeader != null && !authBearerHeader.isEmpty()) {
            String token = authBearerHeader.replace(BEARER, "");


            GuavaCachedJwkProvider provider = new GuavaCachedJwkProvider(new UrlJwkProvider(AUTH0_DOMAIN));
            RSAKeyProvider keyProvider = new RSAKeyProvider() {
                @Override
                public RSAPublicKey getPublicKeyById(String s) {
                    Jwk jwk = null;
                    try {
                        jwk = provider.get(s);
                        return (RSAPublicKey) jwk.getPublicKey();
                    } catch (JwkException | com.auth0.jwk.JwkException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public RSAPrivateKey getPrivateKey() {
                    return null;
                }

                @Override
                public String getPrivateKeyId() {
                    return null;
                }

            };


            Algorithm algorithm = Algorithm.RSA256(keyProvider);
            DecodedJWT decodedJWT = JWT
                    .require(algorithm)
//                    .withClaim("email", "John")
                    .acceptLeeway(1) // 1 sec for nbf, iat and exp
                    .build()
                    .verify(token);

            // parse the token.
            String authUserId = decodedJWT.getSubject();

            if (authUserId != null) {
                try {
                    String email = decodedJWT.getClaim(lawfirmUrl + "email") != null ? decodedJWT.getClaim(lawfirmUrl + "email").asString() : "";
                    String clientFrom = decodedJWT.getClaim(lawfirmUrl + "client") != null ? decodedJWT.getClaim(lawfirmUrl + "client").asString() : "workspace";

                    log.info("user connected email : {}", decodedJWT.getClaim(lawfirmUrl + "email").asString());
                    log.info("user connected user id : {}", authUserId);

                    AppMetadata appMetadata = decodedJWT.getClaim(lawfirmUrl + "app_metadata").as(AppMetadata.class);
                    log.info("new user (signup submitted : {}", appMetadata.getSignedup_submitted());

                    LawfirmToken userProfile;
                    // Except for sign up and first time connection
                    userProfile = lawfirmV2Api.getUserProfile(email, decodedJWT.getToken());
                    userProfile.setClientFrom(clientFrom);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userProfile, null, userProfile.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    log.error("ERROR ", e);
                }
            }
        }
        if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(request, response);
        }
    }
}
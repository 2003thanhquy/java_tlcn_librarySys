package com.spkt.librasys.config;

import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import com.spkt.librasys.dto.request.IntrospectRequest;
import com.spkt.librasys.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import com.nimbusds.jose.JOSEException;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
public class CustomJwtDecode implements JwtDecoder{
    @Value("${jwt.signer-key}")
    private String signerKey;

    private NimbusJwtDecoder nimbusJwtDecoder = null;
    @Autowired
    private AuthenticationService authenticationService;


    @Override
    public Jwt decode(String token) throws JwtException {
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        var response = authenticationService.introspect(
                IntrospectRequest.builder().token(token).build());

        if (!response.isValid()) throw new JwtException("Token invalid!!!!!!!");


        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}

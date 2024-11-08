package com.spkt.librasys.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true) //@AllowRoles({})
public class SecurityConfig {
    private final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/documents",        // Cho phép truy cập tài liệu công khai
            "/api/v1/documents/{documentId}" ,   // Cho phép xem chi tiết tài liệu mà không cần xác thực
    };
    private final String[] PUBLIC_POST_ENDPOINTS = {
            "/api/v1/users",
            "/api/v1/users/**",
            "/api/v1/auth/**",
            "/api/v1/auth/login",
            "/api/v1/token",
            "/api/v1/auth/login-google",
            "/oauth2/**",
            "/api/v1/program-classes/upload",
            "/api/v1/cloudinary/**"
    };

    @Value("${jwt.signer-key}")
    private String signerKey;
    @Autowired
    private CustomJwtDecode customJwtDecode;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request->
                        request.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Cho phép tất cả các yêu cầu OPTIONS
                                .requestMatchers(HttpMethod.POST,PUBLIC_POST_ENDPOINTS).permitAll()
                                .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                                .requestMatchers( "/swagger-ui/**", "/swagger-ui.html","/api-docs", "/api-docs/**").permitAll() // Cho phép truy cập các endpoint của Swagger


                                //.hasAuthority("SCOPE_ADMIN")
                                // .hasRole(Role.ADMIN.name())
                                .anyRequest().authenticated())
//                .oauth2Login(oauth2Login ->
//                        oauth2Login
//                                .loginPage("/api/v1/auth/login-google")  // Endpoint đăng nhập Google
//                                .defaultSuccessUrl("/api/v1/auth/oauth2/success", true)
//                                .failureUrl("/api/v1/auth/oauth2/failure")
////                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
////                                        .userService(customOAuth2UserService()))  // Service lấy thông tin người dùngs
//                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecode)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );
        ;


        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");//default SCOPE_
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
//            web.ignoring().requestMatchers(PUBLIC_GET_ENDPOINTS)
//                    .requestMatchers(PUBLIC_POST_ENDPOINTS);

        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}
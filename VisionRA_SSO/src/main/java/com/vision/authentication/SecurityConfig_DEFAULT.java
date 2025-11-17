package com.vision.authentication;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@Profile("DEFAULT")
public class SecurityConfig_DEFAULT {

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;
    
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Value("${app.allowed.urls}")
    private String allowedURLs = "";
    
    @Value("${vision.allowed.origin}")
	private String[] allowedOrigin;
    
    @Bean
	SecurityFilterChain defaultSecurityFilter(HttpSecurity http) throws Exception {
    	
		http
		.cors(corsconfig -> corsconfig.configurationSource(new CorsConfigurationSource() {
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				 CorsConfiguration corsConfig = new CorsConfiguration();
		            corsConfig.setAllowedOrigins(Arrays.asList(allowedOrigin)); // Wildcard allowed if credentials are not needed
		            corsConfig.setAllowedMethods(Arrays.asList("*")); // Allow all methods
		            corsConfig.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
		            corsConfig.setMaxAge(3600L);
		            corsConfig.setAllowCredentials(false); // Ensure credentials are not allowed
		            return corsConfig;
			}
		}))
		.csrf(csrfconfig -> csrfconfig.disable())
		.addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
		.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
				.requestMatchers(allowedURLs.split(",")).permitAll()
				.anyRequest().authenticated())
		.formLogin(flConfig -> flConfig.disable())
		.httpBasic(hbConfig -> hbConfig.disable());
		return http.build();
	}
    
    @Bean
    public AuthenticationManager authenticationManager() {
        // Using ProviderManager to create an AuthenticationManager with the custom provider
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }
  
}


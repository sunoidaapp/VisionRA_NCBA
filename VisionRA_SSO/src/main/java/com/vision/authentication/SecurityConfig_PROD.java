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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@Profile("PROD")
public class SecurityConfig_PROD {

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;
    
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Value("${app.allowed.urls}")
    private String allowedURLs = "";
    
    @Value("${vision.allowed.origin}")
	private String[] allowedOrigin;
    
    @Value("${vision.csrf.secure}")
    private String csrfSecureFlag;
    
    @Bean
	SecurityFilterChain defaultSecurityFilter(HttpSecurity http) throws Exception {
    	
		CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
		//withHttpOnlyFalse() - Makes the cookies available to be read by the UI application which will by not possible by default
		CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
		csrfTokenRepository.setCookiePath("/");
	    csrfTokenRepository.setCookieCustomizer(t -> t
	    	    .path("/") // The cookie will be accessible site-wide
//	    	    .domain("10.16.1.216") // The domain should match exactly where the Angular app is hosted
	    	    .httpOnly(false) // The cookie can be accessed by JavaScript (needed for Angular)
	    	    .secure("Y".equalsIgnoreCase(csrfSecureFlag)?true:false) // Use true in production with HTTPS
	    	    .sameSite("Lax") // "Lax" is a safe default for most cases, unless cross-origin
	    	    .maxAge(-1L)); // Setting maxAge to -1 makes it a session cookie
	    
		http
//		.requiresChannel(rcc -> rcc.anyRequest().requiresSecure()) //HTTPS Only 
		.cors(corsconfig -> corsconfig.configurationSource(new CorsConfigurationSource() {
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				 CorsConfiguration corsConfig = new CorsConfiguration();
		            corsConfig.setAllowedOrigins(Arrays.asList(allowedOrigin)); // Wildcard allowed if credentials are not needed
		            corsConfig.setAllowedMethods(Arrays.asList("*")); // Allow all methods
		            corsConfig.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
		            corsConfig.setMaxAge(3600L);
		            corsConfig.setAllowCredentials(true); // Ensure credentials are allowed for CSRF trust
		            return corsConfig;
			}
		}))
		.csrf(csrfconfig -> csrfconfig
				.ignoringRequestMatchers("/login")
				.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
				.csrfTokenRepository(csrfTokenRepository))
		.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
		.addFilterAfter(jwtAuthenticationFilter, CsrfCookieFilter.class)
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


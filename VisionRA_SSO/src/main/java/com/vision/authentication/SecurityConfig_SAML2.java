package com.vision.authentication;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Profile("SAML2")
public class SecurityConfig_SAML2 {

	@Autowired
	JwtAuthenticationFilter jwtAuthenticationFilter;

	/*
	 * @Autowired private RelyingPartyRegistrationRepository
	 * relyingPartyRegistrationRepository;
	 */

	@Value("${app.allowed.saml.urls}")
	private String allowedURLs;
	
	@Value("${spring.security.saml2.relyingparty.registration.my-saml2.assertingparty.metadata-uri}")
	private String metadataUri;
	
	@Value("${saml2.registrationId}")
	private String registrationId;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
/*	    http
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        .csrf(csrf -> csrf.disable());
	    return http.build();*/
		RelyingPartyRegistrationResolver relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(
				this.relyingPartyRegistrationRepository());

		Saml2MetadataFilter filter = new Saml2MetadataFilter(relyingPartyRegistrationResolver,
				new OpenSamlMetadataResolver());
		http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable());
		
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				// Allow access to /refreshToken without authentication
				.requestMatchers(allowedURLs.split(",")).permitAll().anyRequest().authenticated())
		.saml2Login(withDefaults()).saml2Logout(withDefaults())
		// .addFilterBefore(filter, Saml2WebSsoAuthenticationRequestFilter.class)
		.addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class)
		.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.addAllowedOrigin("*"); // Or specify allowed origins
	    configuration.addAllowedMethod("*");
	    configuration.addAllowedHeader("*");
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}

	@Bean
	public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
		RelyingPartyRegistration registration = RelyingPartyRegistrations.fromMetadataLocation(metadataUri)
				.registrationId(registrationId)
				.build();
		return new InMemoryRelyingPartyRegistrationRepository(registration);
	}

}
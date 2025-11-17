package com.vision.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
//@EnableSwagger2
public class SwaggerConfig {
	/*
	 * @Bean public Docket productApi() { return new
	 * Docket(DocumentationType.SWAGGER_2).select()
	 * .apis(RequestHandlerSelectors.basePackage("com.vision.controller")).paths(
	 * Predicates.not(PathSelectors.regex("/options-controller.*"))).build()
	 * .apiInfo(metaData());
	 * 
	 * 
	 * //
	 * .paths(Predicates.not(PathSelectors.regex("/OptionsController.*"))).build().
	 * apiInfo(metaData()); }
	 */
	
	// uncomment the below code for access swagger 26-10-2023
	/*
	 * @Bean public Docket api() { return new Docket(DocumentationType.SWAGGER_2)
	 * .select() .apis(RequestHandlerSelectors.basePackage("com.vision.controller"))
	 * // Your controller package .paths(PathSelectors.any()) .build(); } private
	 * ApiInfo metaData() { ApiInfo apiInfo = new
	 * ApiInfo("Revenue Assurance REST API",
	 * "Spring Boot REST API for Revenue Assurance", "1.0", "Terms of service", new
	 * Contact("Sunoida", "", "sunoidaSupport@sunoida.com"), null, null); return
	 * apiInfo; }
	 */
	 @Bean
		public OpenAPI customOpenAPI() {
			return new OpenAPI().info(new Info().title("Revenue Assurance REST API").version("1.0")
					.description("Spring Boot REST API for Revenue Assurance")
					.termsOfService("")
					.contact(new Contact().name("Sunoida").email("sunoidaSupport@sunoida.com"))
					.license(new License().name("Apache 2.0")));
		}
	 
	
//	@Bean
//	public OpenAPI customOpenAPI() {
//		return new OpenAPI()
//				.info(new Info().title("Vision API").version("1.0").description("Auto-generated OpenAPI Docs"));
//	}

}
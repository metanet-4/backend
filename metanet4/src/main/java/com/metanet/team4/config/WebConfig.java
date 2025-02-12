package com.metanet.team4.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableAspectJAutoProxy
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:3000", "https://bhntheatre.netlify.app")
				.allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
	            .exposedHeaders("Set-Cookie", "Authorization")
				.allowCredentials(true);
	}

}

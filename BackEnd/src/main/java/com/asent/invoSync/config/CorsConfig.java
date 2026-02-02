package com.asent.invoSync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.beans.factory.annotation.Value;
@Configuration
public class CorsConfig implements WebMvcConfigurer {
	@Value("${BASE_URL}")
	private String baseUrl;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // ✅ React origin
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}

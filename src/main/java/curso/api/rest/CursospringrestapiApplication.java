package curso.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableCaching
public class CursospringrestapiApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(CursospringrestapiApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("123"));
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/usuario/**").allowedMethods("POST").allowedOrigins("localhost:8080");
	}
}

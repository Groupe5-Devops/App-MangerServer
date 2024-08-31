package server_fullstack.demo;

import java.util.Arrays;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import server_fullstack.demo.enumeration.Status;
import server_fullstack.demo.model.Server;
import server_fullstack.demo.repo.ServerRepo;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	CommandLineRunner run(ServerRepo serverRepo) {
		return args -> {
			serverRepo.save(new Server(null, "8.8.8.8", "Google", "32 GB", "VM-Tooling", "http://34.70.132.118:30003/server/image/server5.png", Status.SERVER_DOWN));
			serverRepo.save(new Server(null, "192.168.1.155", "Kali Linux", "16 GB", "Personal PC", "http://34.70.132.118:30003/server/image/server1.png", Status.SERVER_DOWN));
			serverRepo.save(new Server(null, "192.168.1.154", "Ubuntu Linux", "8 GB", "Dell Tower", "http://34.70.132.118:30003/server/image/server2.png", Status.SERVER_DOWN));
			serverRepo.save(new Server(null, "192.168.1.153", "Red Hat Enterprise Linux", "16 GB", "Web Server", "http://34.70.132.118:30003/server/image/server3.png", Status.SERVER_DOWN));
			serverRepo.save(new Server(null, "192.168.1.152", "Debian", "32 GB", "Ultra PC", "http://34.70.132.118:30003/server/image/server4.png", Status.SERVER_UP));
		};
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200","http://localhost","http://34.70.132.118:30004"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Filename"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}
}

// The CommandLineRunner() method is annotated with @Bean, which means that Spring will manage the instance returned by this method as a bean.
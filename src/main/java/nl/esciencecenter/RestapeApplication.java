package nl.esciencecenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "RestApe API", version = "0.3.0", description = "RESTfull API for the APE (Automated Pipeline Explorer) library."), servers = @Server(url = "http://localhost:4444", description = "Local server"))
public class RestapeApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestapeApplication.class, args);
	}

}

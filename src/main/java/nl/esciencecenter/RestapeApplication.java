package nl.esciencecenter;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * The main class for the RESTful APE API.
 * 
 * @version 0.3.0
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "RestApe API", version = "0.3.0", description = "RESTfull API for the APE (Automated Pipeline Explorer) library."), servers = @Server(url = "http://localhost:REST_APE_PORT", description = "Local server"))
public class RestapeApplication {

	private static final Logger log = LoggerFactory.getLogger(RestapeApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RestapeApplication.class);

		// Port should be .env variable REST_APE_PORT
		app.setDefaultProperties(
				Collections.singletonMap("server.port", Dotenv.load().get("REST_APE_PORT")));

		log.info("Starting RestApe API server...");
		app.run(args);
	}

}

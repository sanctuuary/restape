package nl.esciencecenter;

import java.util.Collections;
import java.util.Optional;

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
 * @version 0.3.1
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "RestApe API", version = "1.0.0", description = "RESTful API for the APE (Automated Pipeline Explorer) library."), servers = @Server(url = "http://localhost:REST_APE_PORT", description = "Local server"))
public class RestapeApplication {

	private static final Logger log = LoggerFactory.getLogger(RestapeApplication.class);
	private static String servicePort = "4444";
	static {
		// Load environment variables from .env file
		try{
		Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
		servicePort = Optional.ofNullable(dotenv.get("REST_APE_PORT")).orElse("4444");
		} catch (Exception e) {
			log.warn("Could not load .env file, using default port " + servicePort);
		}
	}


	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RestapeApplication.class);

		// Port should be .env variable REST_APE_PORT
		app.setDefaultProperties(
				Collections.singletonMap("server.port", servicePort));

		log.info("Starting RestApe API server...");
		app.run(args);
	}

}

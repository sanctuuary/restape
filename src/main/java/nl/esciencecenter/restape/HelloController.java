package nl.esciencecenter.restape;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/test")
    public String hello() {
        return String.format("Hello!");
    }

    // @GetMapping("/parse_domain")
    // public String parseDomain(@RequestParam(value = "name", defaultValue =
    // "World") String name) {
    // String ontologyPath = "";
    // String toolAnnotationsPath = "";

    // return String.format("Hello %s!", name);
    // }
}
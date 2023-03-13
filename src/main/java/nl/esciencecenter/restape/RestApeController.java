package nl.esciencecenter.restape;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApeController {

    @GetMapping("/")
    public String index() {
        return "You are using RestAPE RESTful API!";
    }

    @GetMapping("/test")
    public String hello() {
        return String.format("Hello!");
    }

    @GetMapping("/parse_domain")
    public String parseDomain(
            @RequestParam(value = "ontologyPath", defaultValue = "Path not provided") String ontologyPath,
            @RequestParam(value = "toolAnnotationsPath", defaultValue = "Path not provided again") String toolAnnotationsPath) {

        return ontologyPath + "\n" + toolAnnotationsPath;
    }
}
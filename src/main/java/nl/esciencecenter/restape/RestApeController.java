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

    @GetMapping("/github")
    public String hello() {
        return "https://github.com/sanctuuary/restape";
    }

    @GetMapping("/parse_domain")
    public String parseDomain(
            @RequestParam(value = "ontologyURL", defaultValue = "Path not provided") String ontologyURL,
            @RequestParam(value = "toolAnnotationsURL", defaultValue = "Path not provided again") String toolAnnotationsURL) {

        return ontologyURL + "\n" + toolAnnotationsURL;
    }
}
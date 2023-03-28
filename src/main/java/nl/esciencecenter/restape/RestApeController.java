package nl.esciencecenter.restape;

import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
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

    @GetMapping("/get_data")
    public String parseDomain(
            @RequestParam(value = "config_path", defaultValue = "Path not provided") String configPath) {
        try {
            return RestApeUtils.getData(configPath).toString();
        } catch (IOException | OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            return e.getMessage();
        }
    }

    @GetMapping("/get_tools")
    public String parseTools(
            @RequestParam(value = "config_path", defaultValue = "Path not provided") String configPath) {
        try {
            return RestApeUtils.getTools(configPath).toString();
        } catch (IOException | OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            return e.getMessage();
        }
    }
}
package nl.esciencecenter.controller;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import nl.esciencecenter.models.APEConfig;
import nl.esciencecenter.models.TaxonomyElem;
import nl.esciencecenter.restape.RestApeUtils;

/**
 * This class represents the RestApe controller.
 * TODO: Setup response code 400 and 404 when needed.
 * 
 * @author Vedran
 */
@RestController
public class RestApeController {

        @GetMapping("/")
        @Operation(summary = "Index", description = "Index of the RestApe API", tags = { "Index" })
        public String index() {
                return "You are using RestAPE RESTful API!";
        }

        @GetMapping("/get_data")
        @Operation(summary = "Retrieve data taxonomy", description = "Retrieve data (taxonomy) within the domain.", tags = {
                        "Domain" }, parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(schema = @Schema(implementation = TaxonomyElem.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public String getData(
                        @RequestParam("config_path") String configPath) {
                try {
                        return RestApeUtils.getData(configPath).toString();
                } catch (IOException | OWLOntologyCreationException e) {
                        // TODO Auto-generated catch block
                        return e.getMessage();
                }
        }

        @GetMapping("/get_tools")
        @Operation(summary = "Retrieve tool taxonomy", description = "Retrieve tools (taxonomy) within the domain.", tags = {
                        "Domain" }, parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(schema = @Schema(implementation = TaxonomyElem.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public String getTools(
                        @RequestParam("config_path") String configPath) {
                try {
                        return RestApeUtils.getTools(configPath).toString();
                } catch (IOException | OWLOntologyCreationException e) {
                        // TODO Auto-generated catch block
                        return e.getMessage();
                }
        }

        @GetMapping("/run_synthesis")
        @Operation(summary = "Run workflow synthesis", description = "Run workflow synthesis using the APE library.", tags = {
                        "APE" }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON object containing the configuration for the synthesis.", content = @Content(schema = @Schema(implementation = APEConfig.class))), parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Synthesis solutions are returned."),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public String runSynthesis(
                        @RequestBody(required = true) JSONObject configJson,
                        @RequestParam(defaultValue = "User not identified") String userId) {
                try {
                        JSONObject config = new JSONObject(configJson);
                        return RestApeUtils.runSynthesis(config, userId).toString();
                } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        return e.getMessage();
                }
        }
}
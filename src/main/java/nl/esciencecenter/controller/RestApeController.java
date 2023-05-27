package nl.esciencecenter.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import nl.esciencecenter.models.documentation.APEConfig;
import nl.esciencecenter.models.documentation.TaxonomyElem;
import nl.esciencecenter.restape.ApeAPI;
import nl.esciencecenter.restape.IOUtils;
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
                return "Welcome to the RestApe API!";
        }
        

        @GetMapping("/get_data")
        @Operation(summary = "Retrieve data taxonomy", description = "Retrieve data (taxonomy) within the domain.", tags = {
                        "Domain" }, parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(schema = @Schema(implementation = TaxonomyElem.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getData(
                        @RequestParam("config_path") String configPath) {
                try {
                        return ResponseEntity.ok().body(ApeAPI.getData(configPath).toString());
                } catch (IOException | OWLOntologyCreationException e) {
                        // TODO Auto-generated catch block
                        return ResponseEntity.badRequest().body(e.getMessage());
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
        public ResponseEntity<String> getTools(
                        @RequestParam("config_path") String configPath) {
                try {
                        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                                        .body(ApeAPI.getTools(configPath).toString());
                } catch (IOException | OWLOntologyCreationException e) {
                        // TODO Auto-generated catch block
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        @GetMapping("/get_constraints")
        @Operation(summary = "Retrieve constraint templates", description = "Retrieve constraint templates used to specify synthesis problem.", tags = {
                        "Domain" }, parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(schema = @Schema(implementation = TaxonomyElem.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getConstraints(@RequestParam("config_path") String configPath) {
                try {
                        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                                        .body(ApeAPI.getConstraints(configPath).toString());
                } catch (IOException | OWLOntologyCreationException e) {
                        // TODO Auto-generated catch block
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        @PostMapping("/run_synthesis")
        @Operation(summary = "Run workflow synthesis", description = "Run workflow synthesis using the APE library.", tags = {
                        "APE" }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON object containing the configuration for the synthesis.", content = @Content(schema = @Schema(implementation = APEConfig.class))), parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Synthesis solutions are returned."),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> runSynthesis(
                        @RequestBody(required = true) Map configJson) {
                try {
                        JSONObject config = new JSONObject(configJson);

                        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                                        .body(ApeAPI.runSynthesis(config).toString());
                } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }
        
        @GetMapping("/get_image")
        @Operation(summary = "Retrieve an image representing the workflow.", description = "Retrieve a image from the file system representing the workflow previously generated.", tags = {
                        "Domain" }, parameters = {
                                        @Parameter(name = "file_name", description = "Name of the image file.", example = "workflowSolution_0.png"),
                                        @Parameter(name="run_id",description="ID of the corresponding synthesis run.",example="04ce2ef00c1685150252568")
                                                                                                        
                                        }, responses = {
                                                @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE)),
                                                @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                @ApiResponse(responseCode = "404", description = "Not found")
                                        })
                public ResponseEntity<?> getImage(
                        @RequestParam("file_name") String fileName,
                        @RequestParam("run_id") String runID) {
                        Path path = RestApeUtils.calculatePath(runID, "Figures", fileName);
                        try {
                                return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                                        .body(IOUtils.getImageFromFileSystem(path));
                        } catch (IOException e) {
                                return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        @GetMapping("/get_cwl")
        @Operation(summary = "Retrieve a file", description = "Retrieve a file from the workflow system.", tags = {
                        "Domain" },
                        parameters = {
                                        @Parameter(name = "file_name", 
                                                description = "Name of the CWL file.", 
                                                example = "workflowSolution_0.cwl"),
                                        @Parameter(name = "run_id",
                                                description = "ID of the corresponding synthesis run.",
                                                example="04ce2ef00c1685150252568")
                                
                        }, responses = {
                                @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(mediaType = "application/x-yaml")),
                                @ApiResponse(responseCode = "400", description = "Invalid input"),
                                @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getCwl(
                        @RequestParam("file_name") String fileName,
                        @RequestParam("run_id") String runID) {
                try {
                        Path path = RestApeUtils.calculatePath(runID, "CWL", fileName);
                        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-yaml"))
                                                                .body(IOUtils.getLocalCwlFile(path));
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }
}
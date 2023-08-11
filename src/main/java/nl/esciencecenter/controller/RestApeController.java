package nl.esciencecenter.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

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
import nl.esciencecenter.models.documentation.ConstraintElem;
import nl.esciencecenter.models.documentation.TaxonomyElem;
import nl.esciencecenter.restape.ApeAPI;
import nl.esciencecenter.restape.IOUtils;
import nl.esciencecenter.restape.RestApeUtils;
import nl.uu.cs.ape.configuration.APEConfigException;

/**
 * This class represents the RestApe controller.
 * TODO: Setup response code 400 and 404 when needed.
 * 
 * @author Vedran
 */
@RestController
public class RestApeController {

        /**
         * Index of the RestApe API. Welcome message.
         * 
         * @return Welcome message.
         */
        @GetMapping("/")
        @Operation(summary = "Index", description = "Index of the RestApe API", tags = { "Index" })
        public String index() {
                return "Welcome to the RestApe API!";
        }

        /**
         * Retrieve data taxonomy based on the provided domain configuration file.
         * 
         * @param configPath URL to the APE configuration file.
         * @return Taxonomy of data terms.
         */
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
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(ApeAPI.getData(configPath).toString());
                } catch (IOException | OWLOntologyCreationException e) {
                        // TODO Auto-generated catch block
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        /**
         * Retrieve tool taxonomy based on the provided domain configuration file.
         * 
         * @param configPath URL to the APE configuration file.
         * @return Taxonomy of tool terms.
         */
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
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(ApeAPI.getTools(configPath).toString());
                } catch (IOException | OWLOntologyCreationException e) {
                        // TODO Auto-generated catch block
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        /**
         * Retrieve constraint templates based on the provided domain configuration
         * file.
         * 
         * @param configPath URL to the APE configuration file.
         * @return Constraint templates.
         */
        @GetMapping("/get_constraints")
        @Operation(summary = "Retrieve constraint templates", description = "Retrieve constraint templates used to specify synthesis problem.", tags = {
                        "Domain" }, parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(schema = @Schema(implementation = ConstraintElem.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getConstraints(@RequestParam("config_path") String configPath) {
                try {
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(ApeAPI.getConstraints(configPath).toString());
                } catch (IOException | OWLOntologyCreationException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        /**
         * Synthesize workflow based on the provided run configuration file.
         * 
         * @param configJson JSON object containing the configuration for the synthesis.
         * @return List of resulting solutions, where each element describes a workflow
         *         (name,length, runID, etc.)
         */
        @PostMapping("/run_synthesis")
        @Operation(summary = "Run workflow synthesis", description = "Run workflow synthesis using the APE library. Returns the list of resulting solutions, where each element describes a workflow (name,length, runID, etc.).", tags = {
                        "APE" }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON object containing the configuration for the synthesis.", content = @Content(schema = @Schema(implementation = APEConfig.class))), parameters = {
                                        @Parameter(name = "configJson", description = "APE configuration JSON file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Synthesis solutions are returned."),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> runSynthesis(
                        @RequestBody(required = true) Map configJson) {
                try {
                        JSONObject config = new JSONObject(configJson);

                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(ApeAPI.runSynthesis(config).toString());
                } catch (APEConfigException | JSONException | OWLOntologyCreationException | IOException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        /**
         * Retrieve the solution workflow based on the provided run ID and a solution
         * name.
         * 
         * @param fileName Name of the workflow file (provided under 'name' after
         *                 the synthesis run).
         * @param runID    ID of the corresponding synthesis run (provided under
         *                 'run_id' after the synthesis run).
         * @return Image in PNG format representing the workflow.
         */
        @GetMapping("/get_image")
        @Operation(summary = "Retrieve an image representing the workflow.", description = "Retrieve a image from the file system representing the workflow previously generated.", tags = {
                        "Dowload" }, parameters = {
                                        @Parameter(name = "file_name", description = "Name of the image file (provided under 'name' after the synthesis run).", example = "workflowSolution_0.png"),
                                        @Parameter(name = "run_id", description = "ID of the corresponding synthesis run (provided under 'run_id' after the synthesis run).", example = "04ce2ef00c1685150252568")

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
                        return ResponseEntity.badRequest().body("The file could not be found.");
                }
        }

        /**
         * Retrieve the solution CWL workflow based on the provided run ID and a
         * solution
         * 
         * @param fileName Name of the CWL file (provided under 'name' after the
         *                 synthesis run).
         * @param runID    ID of the corresponding synthesis run (provided under
         *                 'run_id' after the synthesis run).
         * @return CWL file representing the workflow.
         */
        @GetMapping("/get_cwl")
        @Operation(summary = "Retrieve a cwl file", description = "Retrieve a cwl file from the workflow system.", tags = {
                        "Dowload" }, parameters = {
                                        @Parameter(name = "file_name", description = "Name of the CWL file (provided under 'figure_name' after the synthesis run).", example = "workflowSolution_0.cwl"),
                                        @Parameter(name = "run_id", description = "ID of the corresponding synthesis run (provided under 'run_id' after the synthesis run).", example = "04ce2ef00c1685150252568")

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
                        return ResponseEntity.badRequest().body("The file could not be found.");
                }
        }

        /**
         * Retrieve the CWL input file based on the provided run ID.
         * 
         * @param runID ID of the corresponding synthesis run (provided under 'run_id'
         *              after the synthesis run).
         * @return CWL input file (.yml) representing the workflow inputs.
         */
        @GetMapping("/get_cwl_input")
        @Operation(summary = "Retrieve a cwl input file", description = "Retrieve a cwl input file from the workflow system.", tags = {
                        "Dowload" }, parameters = {
                                        @Parameter(name = "run_id", description = "ID of the corresponding synthesis run (provided under 'run_id' after the synthesis run).", example = "04ce2ef00c1685150252568")

        }, responses = {
                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(mediaType = "application/x-yaml")),
                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getCwlInput(
                        @RequestParam("run_id") String runID) {
                try {
                        Path path = RestApeUtils.calculatePath(runID, "CWL", "input.yml");
                        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-yaml"))
                                        .body(IOUtils.getLocalCwlFile(path));
                } catch (IOException e) {
                        return ResponseEntity.badRequest().body("The file could not be found.");
                }
        }
}
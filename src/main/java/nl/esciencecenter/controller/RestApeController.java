package nl.esciencecenter.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import nl.esciencecenter.controller.dto.APEConfig;
import nl.esciencecenter.controller.dto.CWLFileInfo;
import nl.esciencecenter.controller.dto.CWLZip;
import nl.esciencecenter.controller.dto.ConstraintElem;
import nl.esciencecenter.controller.dto.ImgFileInfo;
import nl.esciencecenter.controller.dto.TaxonomyElem;
import nl.esciencecenter.restape.ApeAPI;
import nl.esciencecenter.restape.IOUtils;
import nl.esciencecenter.restape.RestApeUtils;
import nl.uu.cs.ape.configuration.APEConfigException;

/**
 * This class represents the RESTful APE controller.
 * TODO: Setup response code 400 and 404 when needed.
 * 
 * @author Vedran
 */
@RestController
public class RestApeController {

        private static final String invalidRunIDMsg = "The run ID is invalid.";
        private static final String invalidFileNameMsg = "The file name format is invalid.";

        /**
         * Index of the RESTful APE API. Welcome message.
         * 
         * @return Welcome message.
         */
        @GetMapping("/")
        @Operation(summary = "Index", description = "Index of the RESTful APE API", tags = { "Index" })
        public String index() {
                return "Welcome to the RESTful APE API!";
        }

        /**
         * Retrieve data taxonomy based on the provided domain configuration file.
         * 
         * @param configPath URL to the APE configuration file.
         * @return Taxonomy of data terms.
         * @throws IOException
         * @throws OWLOntologyCreationException
         */
        @GetMapping("/data_taxonomy")
        @Operation(summary = "Retrieve data taxonomy", description = "Retrieve data (taxonomy) within the domain.", tags = {
                        "Domain" }, parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(schema = @Schema(implementation = TaxonomyElem.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getData(
                        @RequestParam("config_path") String configPath)
                        throws OWLOntologyCreationException, IOException, IllegalArgumentException {
                RestApeUtils.validateURL(configPath);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(ApeAPI.getData(configPath).toString());
        }

        /**
         * Retrieve tool taxonomy based on the provided domain configuration file.
         * 
         * @param configPath URL to the APE configuration file.
         * @return Taxonomy of tool terms.
         * @throws IOException
         * @throws OWLOntologyCreationException
         */
        @GetMapping("/tools_taxonomy")
        @Operation(summary = "Retrieve tool taxonomy", description = "Retrieve tools (taxonomy) within the domain.", tags = {
                        "Domain" }, parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(schema = @Schema(implementation = TaxonomyElem.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getTools(
                        @RequestParam("config_path") String configPath)
                        throws OWLOntologyCreationException, IOException {
                RestApeUtils.validateURL(configPath);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(ApeAPI.getTools(configPath).toString());
        }

        /**
         * Retrieve constraint templates based on the provided domain configuration
         * file.
         * 
         * @param configPath URL to the APE configuration file.
         * @return Constraint templates.
         */
        @GetMapping("/constraints")
        @Operation(summary = "Retrieve constraint templates", description = "Retrieve constraint templates used to specify synthesis problem.", tags = {
                        "Domain" }, parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(schema = @Schema(implementation = ConstraintElem.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getConstraints(@RequestParam("config_path") String configPath)
                        throws JSONException, OWLOntologyCreationException, IOException {

                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(ApeAPI.getConstraints(configPath).toString());
        }

        /**
         * Synthesize workflow based on the provided run configuration file.
         * 
         * @param configJson JSON object containing the configuration for the synthesis.
         * @return List of resulting solutions, where each element describes a workflow
         *         (name,length, run_id, etc.)
         * @throws OWLOntologyCreationException
         */
        @PostMapping("/run_synthesis")
        @Operation(summary = "Run workflow synthesis", description = "Run workflow synthesis using the APE library. Returns the list of resulting solutions, where each element describes a workflow (name,length, run_id, etc.).", tags = {
                        "APE" }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON object containing the configuration for the synthesis.", content = @Content(schema = @Schema(implementation = APEConfig.class))), parameters = {
                                        @Parameter(name = "configJson", description = "APE configuration JSON file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Synthesis solutions are returned."),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found"),
                                                        @ApiResponse(responseCode = "500", description = "Internal server error"),

        })
        public ResponseEntity<String> runSynthesis(
                        @RequestBody(required = true) Map<String, Object> configJson)
                        throws APEConfigException, JSONException, OWLOntologyCreationException, IOException {
                JSONObject config = new JSONObject(configJson);

                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(ApeAPI.runSynthesis(config, false).toString());
        }

        /**
         * Synthesize workflow based on the provided run configuration file.
         * 
         * @param configJson JSON object containing the configuration for the synthesis.
         * @return List of resulting solutions, where each element describes a workflow
         *         (name,length, run_id, etc.)
         * @throws IOException
         */
        @PostMapping("/run_synthesis_and_bench")
        @Operation(summary = "Run workflow synthesis and provide design-time benchmarks", description = "Run workflow synthesis using the APE library. In addition, evaluate design time benchmarks of the generated workflows. Returns the list of resulting solutions, where each element describes a workflow (name,length, run_id, etc.).", tags = {
                        "APE" }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON object containing the configuration for the synthesis.", content = @Content(schema = @Schema(implementation = APEConfig.class))), parameters = {
                                        @Parameter(name = "configJson", description = "APE configuration JSON file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Synthesis solutions are returned."),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found"),
                                                        @ApiResponse(responseCode = "500", description = "Internal server error"),

        })
        public ResponseEntity<String> runSynthesisAndBench(
                        @RequestBody(required = true) Map<String, Object> configJson)
                        throws APEConfigException, JSONException, OWLOntologyCreationException, IOException {
                JSONObject config = new JSONObject(configJson);

                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(ApeAPI.runSynthesis(config, true).toString());
        }

        /**
         * Retrieve the solution workflow based on the provided run ID and a candidate
         * solution.
         * 
         * @param fileName Name of the workflow file (provided under 'name' after
         *                 the synthesis run).
         * @param runID    ID of the corresponding synthesis run (provided under
         *                 'run_id' after the synthesis run).
         * @return Image in PNG format representing the workflow.
         * @throws IOException
         */
        @PostMapping("/image")
        @Operation(summary = "Retrieve an image representing the workflow.", description = "Retrieve a image from the file system representing the workflow generated.", tags = {
                        "Download" }, parameters = {
                                        @Parameter(name = "file_name", description = "Name of the image file (provided under 'name' after the synthesis run).", example = "workflowSolution_0"),
                                        @Parameter(name = "format", description = "Format of the image ('png' or 'svg').", example = "png"),
                                        @Parameter(name = "run_id", description = "ID of the corresponding synthesis run (provided under 'run_id' after the synthesis run).", example = "04ce2ef00c1685150252568")

        }, responses = {
                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE)),
                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                        @ApiResponse(responseCode = "404", description = "Not found")
        })
        public ResponseEntity<?> getImage(
                        @RequestBody(required = true) ImgFileInfo imgFileInfo) throws IOException {

                Path path = imgFileInfo.calculatePath();
                return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                                .body(new FileSystemResource(path));
        }

        /**
         * Retrieve the solution CWL workflow based on the provided run ID and a
         * candidate solution.
         * 
         * @param fileName Name of the CWL file (provided under 'name' after the
         *                 synthesis run).
         * @param runID    ID of the corresponding synthesis run (provided under
         *                 'run_id' after the synthesis run).
         * @return CWL file representing the workflow.
         */
        @PostMapping("/cwl")
        @Operation(summary = "Retrieve a cwl file", description = "Retrieve a cwl file from the file system, describing the workflow.", tags = {
                        "Download" }, parameters = {

                                        @Parameter(name = "file_name", description = "Name of the CWL file (provided under 'figure_name' after the synthesis run).", example = "workflowSolution_0.cwl"),
                                        @Parameter(name = "run_id", description = "ID of the corresponding synthesis run (provided under 'run_id' after the synthesis run).", example = "04ce2ef00c1685150252568")

        }, responses = {
                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(mediaType = "application/x-yaml")),
                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getCwl(
                        @RequestBody(required = true) CWLFileInfo cwlInfoJson) throws IOException {

                Path path = cwlInfoJson.calculatePath();
                return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-yaml"))
                                .body(IOUtils.getLocalCwlFile(path));
        }

        /**
         * Retrieve the CWL input file based on the provided run ID.
         * 
         * @param runID ID of the corresponding synthesis run (provided under 'run_id'
         *              after the synthesis run).
         * @return CWL input file (.yml) representing the workflow inputs.
         */
        @GetMapping("/cwl_input")
        @Operation(summary = "Retrieve a cwl input file", description = "Retrieve a cwl input file from the file system, allowing to execute the workflows in the run.", tags = {
                        "Download" }, parameters = {
                                        @Parameter(name = "run_id", description = "ID of the corresponding synthesis run (provided under 'run_id' after the synthesis run).", example = "04ce2ef00c1685150252568")

        }, responses = {
                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(mediaType = "application/x-yaml")),
                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getCwlInput(
                        @RequestParam("run_id") String runID) {
                if (!RestApeUtils.isValidRunID(runID)) {
                        return ResponseEntity.badRequest().body(invalidRunIDMsg);
                }
                try {
                        Path path = RestApeUtils.calculatePath(runID, "CWL", "input.yml");
                        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-yaml"))
                                        .body(IOUtils.getLocalCwlFile(path));
                } catch (IOException e) {
                        return ResponseEntity.badRequest().body("The CWL input file could not be found.");
                }
        }

        /**
         * Retrieve the design-time benchmark information based on the provided run ID
         * and a
         * candidate solution.
         * 
         * @param fileName Name of the CWL file (provided under 'name' after the
         *                 synthesis run).
         * @param runID    ID of the corresponding synthesis run (provided under
         *                 'run_id' after the synthesis run).
         * @return CWL file representing the workflow.
         */
        @GetMapping("/design_time_benchmarks")
        @Operation(summary = "Retrieve a design-time benchmark file", description = "Retrieve a design-time benchmark file from the file system, describing the workflow.", tags = {
                        "Download" }, parameters = {
                                        @Parameter(name = "file_name", description = "Name of the benchmark file (provided under 'bench_name' after the synthesis run).", example = "workflowSolution_0.json"),
                                        @Parameter(name = "run_id", description = "ID of the corresponding synthesis run (provided under 'run_id' after the synthesis run).", example = "04ce2ef00c1685150252568")

        }, responses = {
                        @ApiResponse(responseCode = "200", description = "Successful operation. Taxonomy of data terms is provided.", content = @Content(mediaType = "application/x-yaml")),
                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                        @ApiResponse(responseCode = "404", description = "Not found")

        })
        public ResponseEntity<String> getBenchmarks(
                        @RequestParam("file_name") String fileName,
                        @RequestParam("run_id") String runID) {
                if (!RestApeUtils.isValidRunID(runID)) {
                        return ResponseEntity.badRequest().body(invalidRunIDMsg);
                } else if (!RestApeUtils.isValidAPEFileName(fileName, "json")) {
                        return ResponseEntity.badRequest().body(invalidFileNameMsg);
                }
                try {
                        Path path = RestApeUtils.calculatePath(runID, "CWL", fileName);
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(IOUtils.getLocalBenchmarkFile(path));
                } catch (IOException e) {
                        return ResponseEntity.badRequest().body("The CWL file could not be found.");
                }
        }

        /**
         * Retrieve the CWL solution files based on the provided run ID and CWL file
         * names.
         * TODO: Exeptions don't handle all cases or illegal arguments (e.g. invalid
         * workflow name that ends with an open quotation`candidate_solution_1.cwl"`).
         * 
         * @param cwlFilesJson JSON object containing the run_id and the list of CWL
         *                     files.
         * @return CWL file representing the workflow.
         */
        @PostMapping("/cwl_zip")
        @Operation(summary = "Retrieve the zip of cwl files.", description = "Retrieve the zip comprising CWL files specified in the request body. The request body should be a JSON object with the following fields: 'run_id' and 'workflows'. The 'run_id' field specifies the ID of the synthesis run, while the 'workflows' field is a list of CWL file names (provided under 'name' after the synthesis run).", tags = {
                        "Download" }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON object containing the following fields: 'run_id' and 'workflows'.", content = @Content(schema = @Schema(implementation = CWLZip.class))), parameters = {
                                        @Parameter(name = "cwlFilesJson", description = "Synthesis run_id and the cwl file names.") }, responses = {
                                                        @ApiResponse(responseCode = "200", description = "Successful operation. Synthesis solutions are returned."),
                                                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                                                        @ApiResponse(responseCode = "404", description = "Not found"),
                                                        @ApiResponse(responseCode = "500", description = "Internal server error"),

        })
        public ResponseEntity<?> getZipCWLs(
                        @RequestBody(required = true) CWLZip cwlZipInfo) {
                try {
                        List<Path> cwlFilePaths = cwlZipInfo.getCWLPaths();

                        // Add the CWL input file to the zip
                        Path cwlInputPath = RestApeUtils.calculatePath(cwlZipInfo.getRunID(), "CWL", "input.yml");
                        cwlFilePaths.add(cwlInputPath);

                        // Zip the CWL files
                        Path zipPath = IOUtils.zipFiles(cwlFilePaths, cwlInputPath.getParent());

                        Resource zipResource = new UrlResource(zipPath.toUri());
                        String zipContentType = Files.probeContentType(zipPath);
                        return ResponseEntity.ok()
                                        .contentType(MediaType.parseMediaType(
                                                        zipContentType != null ? zipContentType : "application/zip"))
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"" + zipPath.getFileName().toString()
                                                                        + "\"")
                                        .body(zipResource);
                } catch (IOException e) {
                        return ResponseEntity.badRequest()
                                        .body("An error occurred while creating the zip file.");
                } catch (ClassCastException e) {
                        return ResponseEntity.badRequest().body("JSON structure is not not valid.");
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> handleException(IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
        }

        @ExceptionHandler(OWLOntologyCreationException.class)
        public ResponseEntity<String> handleException(OWLOntologyCreationException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
        }

        @ExceptionHandler(IOException.class)
        public ResponseEntity<String> handleException(IOException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
        }

        @ExceptionHandler(JSONException.class)
        public ResponseEntity<String> handleException(JSONException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
        }

        @ExceptionHandler(APEConfigException.class)
        public ResponseEntity<String> handleException(APEConfigException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
        }

}
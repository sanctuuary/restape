package nl.esciencecenter.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
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
import nl.esciencecenter.models.documentation.CWLZip;
import nl.esciencecenter.models.documentation.ConstraintElem;
import nl.esciencecenter.models.documentation.TaxonomyElem;
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
                        @RequestParam("config_path") String configPath) {
                try {
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(ApeAPI.getData(configPath).toString());
                } catch (IOException | OWLOntologyCreationException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        /**
         * Retrieve tool taxonomy based on the provided domain configuration file.
         * 
         * @param configPath URL to the APE configuration file.
         * @return Taxonomy of tool terms.
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
                        @RequestParam("config_path") String configPath) {
                try {
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(ApeAPI.getTools(configPath).toString());
                } catch (IOException | OWLOntologyCreationException e) {
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
        @GetMapping("/constraints")
        @Operation(summary = "Retrieve constraint templates", description = "Retrieve constraint templates used to specify synthesis problem.", tags = {
                        "Domain" }, parameters = {
                                        @Parameter(name = "config_path", description = "URL to the APE configuration file.", example = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json") }, externalDocs = @ExternalDocumentation(description = "More information about the APE configuration file can be found here.", url = "https://ape-framework.readthedocs.io/en/latest/docs/specifications/setup.html#configuration-file"), responses = {
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
         *         (name,length, run_id, etc.)
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
                        @RequestBody(required = true) Map<String, Object> configJson) {
                try {
                        JSONObject config = new JSONObject(configJson);

                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(ApeAPI.runSynthesis(config, false).toString());
                } catch (APEConfigException | JSONException | OWLOntologyCreationException | IOException e) {
                        return ResponseEntity.internalServerError().body(e.getMessage());
                }
        }

        /**
         * Synthesize workflow based on the provided run configuration file.
         * 
         * @param configJson JSON object containing the configuration for the synthesis.
         * @return List of resulting solutions, where each element describes a workflow
         *         (name,length, run_id, etc.)
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
                        @RequestBody(required = true) Map<String, Object> configJson) {
                try {
                        JSONObject config = new JSONObject(configJson);

                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(ApeAPI.runSynthesis(config, true).toString());
                } catch (APEConfigException | JSONException | OWLOntologyCreationException | IOException e) {
                        return ResponseEntity.internalServerError().body(e.getMessage());
                }
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
         */
        @GetMapping("/image")
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
                        @RequestParam("file_name") String fileName,
                        @RequestParam("format") String imgFormat,
                        @RequestParam("run_id") String runID) {
                Path path = null;
                if (imgFormat.equalsIgnoreCase("png")) {
                        path = RestApeUtils.calculatePath(runID, "Figures", fileName + ".png");
                } else if (imgFormat.equalsIgnoreCase("svg")) {
                        path = RestApeUtils.calculatePath(runID, "Figures", fileName + ".svg");
                } else {
                        return ResponseEntity.badRequest().body("The specified image format is not supported.");
                }
                FileSystemResource resource = new FileSystemResource(path);
                try {
                        return ResponseEntity.ok()
                                        .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                                        .body(resource);
                } catch (IOException e) {
                        return ResponseEntity.badRequest().body("The image file could not be found.");
                }
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
        @GetMapping("/cwl")
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
                        @RequestParam("file_name") String fileName,
                        @RequestParam("run_id") String runID) {
                try {
                        Path path = RestApeUtils.calculatePath(runID, "CWL", fileName);
                        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-yaml"))
                                        .body(IOUtils.getLocalCwlFile(path));
                } catch (IOException e) {
                        return ResponseEntity.badRequest().body("The CWL file could not be found.");
                }
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
         * names
         * 
         * @param cwlFilesJson JSON object containing the run_id and the list of CWL files.
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
                        @RequestBody(required = true) Map<String, Object> cwlFilesJson) {
                try {
                        String runID = (String) cwlFilesJson.get("run_id");

                        List<String> workflowNames = (List<String>) cwlFilesJson.get("workflows");
                        List<Path> cwlFilePaths = workflowNames.stream()
                                        .map(fileName -> RestApeUtils.calculatePath(runID, "CWL", fileName))
                                        .collect(Collectors.toList());

                        Path cwlInputPath = RestApeUtils.calculatePath(runID, "CWL", "input.yml");
                        cwlFilePaths.add(cwlInputPath);

                        Path zipPath = cwlInputPath.getParent().resolve("workflows.zip");
                        try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
                                        ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                                for (Path file : cwlFilePaths) {
                                        zipOut.putNextEntry(new ZipEntry(file.getFileName().toString()));
                                        Files.copy(file, zipOut);
                                        zipOut.closeEntry();
                                }
                        }

                        Resource resource = new UrlResource(zipPath.toUri());
                        String contentType = Files.probeContentType(zipPath);
                        return ResponseEntity.ok()
                                        .contentType(MediaType.parseMediaType(
                                                        contentType != null ? contentType : "application/zip"))
                                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                                        "attachment; filename=\"" + zipPath.getFileName().toString()
                                                                        + "\"")
                                        .body(resource);
                } catch (Exception e) { // Catching Exception to cover all bases, consider catching more specific
                                        // exceptions
                        return ResponseEntity.badRequest().body("An error occurred: " + e.getMessage());
                }
        }

        // public boolean zipFilesToStream(List<Path> files, ZipOutputStream zipOut)
        // throws IOException {
        // for (Path file : files) {
        // zipOut.putNextEntry(new ZipEntry(file.getFileName().toString()));
        // Files.copy(file, zipOut);
        // zipOut.closeEntry();
        // }
        // return true;
        // }

        // public void test() {
        // final FileOutputStream fos = new FileOutputStream(
        // cwlInputPath.toAbsolutePath() + "/compressed.zip");
        // ZipOutputStream zipOut = new ZipOutputStream(fos);

        // for (Path cwlFile : cwlFilePaths) {
        // File fileToZip = cwlFile.toFile();
        // FileInputStream fis = new FileInputStream(fileToZip);
        // ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        // zipOut.putNextEntry(zipEntry);

        // byte[] bytes = new byte[1024];
        // int length;
        // while ((length = fis.read(bytes)) >= 0) {
        // zipOut.write(bytes, 0, length);
        // }
        // fis.close();
        // }

        // zipOut.close();
        // fos.close();}
}
package nl.esciencecenter.restape;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.t;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.esciencecenter.models.benchmarks.BenchmarkBase;
import nl.esciencecenter.models.benchmarks.BioToolsBenchmark;
import nl.esciencecenter.models.benchmarks.OpenEBenchmark;
import nl.uu.cs.ape.solver.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.solver.solutionStructure.SolutionsList;
import nl.uu.cs.ape.utils.APEFiles;

/**
 * The {@code ToolBenchmarkingAPIs} class provides methods to compute the tool
 * metrics provided by OpenEBench API.<br>
 * <br>
 * 
 * <strong>Important:</strong>
 * The OpenEBench API does not provide a well structured API at the moment. The
 * current API interface is available at
 * {@link https://openebench.bsc.es/monitor}. Therefore, some of the methods in
 * this class are hardcoded to be able to utilize the current API interface.<br>
 * <br>
 * The new API version will in BioSchemas format, in JSON-LD, aligned with
 * BioConda (see documentation at
 * {@link https://openebench.bsc.es/bioschemas/}), however that API is not yet
 * available.
 * 
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ToolBenchmarkingAPIs {

   public static final String restAPEtoolID = "restAPEtoolID";
   private static final Logger log = LoggerFactory.getLogger(ToolBenchmarkingAPIs.class);
   public static final OkHttpClient client = new OkHttpClient();

   /**
    * Compute the benchmarks for the workflows.
    * 
    * @param candidateSolutions - SolutionsList object, which contains the results
    *                           of the synthesis as well as information about the
    *                           synthesis run.
    * @return - boolean to indicate if the benchmarks were computed successfully
    */
   static boolean computeBenchmarks(SolutionsList candidateSolutions, String runID) {
      candidateSolutions.getParallelStream().forEach(workflow -> {
         JSONArray biotoolsbenchmark = computeBiotoolsBenchmark(workflow);
         JSONArray openEBenchmarks = computeOpenEBenchmarks(workflow);
         openEBenchmarks.forEach(biotoolsbenchmark::put);

         JSONObject workflowBenchmarks = computeWorkflowSpecificFields(workflow, runID);
         workflowBenchmarks.put("benchmarks", biotoolsbenchmark);

         String titleBenchmark = workflow.getFileName() + ".json";
         Path solFolder = candidateSolutions.getRunConfiguration().getSolutionDirPath2CWL();
         File script = solFolder.resolve(titleBenchmark).toFile();
         try {
            APEFiles.write2file(workflowBenchmarks.toString(2), script, false);
         } catch (JSONException | IOException e) {
            e.printStackTrace();
         }

      });

      return true;
   }

   private static JSONObject computeWorkflowSpecificFields(SolutionWorkflow workflow, String runID) {
      JSONObject benchmarkResult = new JSONObject();
      // Set workflow specific fields
      benchmarkResult.put("runID", runID);
      benchmarkResult.put("domainID", "1");
      benchmarkResult.put("workflowName", workflow.getFileName());

      return benchmarkResult;
   }

   /**
    * Compute the bio.tools benchmarks for the workflows and return it in JSON
    * format.
    * 
    * @param workflow
    * @return
    */
   private static JSONArray computeBiotoolsBenchmark(SolutionWorkflow workflow) {

      // for each tool in the workflow, get the biotools annotations from bio.tool API
      List<JSONObject> biotoolsAnnotations = new ArrayList<>();

      workflow.getModuleNodes().forEach(toolNode -> {
         String toolID = toolNode.getUsedModule().getPredicateLabel();
         try {

            JSONObject biotoolsEntry = ToolBenchmarkingAPIs.fetchToolFromBioTools(toolID);
            biotoolsEntry.put(ToolBenchmarkingAPIs.restAPEtoolID, toolNode.getUsedModule().getPredicateLabel());
            biotoolsAnnotations.add(biotoolsEntry);
         } catch (JSONException | IOException e) {
            JSONObject biotoolsEntry = new JSONObject();
            biotoolsEntry.put(ToolBenchmarkingAPIs.restAPEtoolID, toolNode.getUsedModule().getPredicateLabel());
            biotoolsAnnotations.add(biotoolsEntry);
            e.printStackTrace();
         }
      });

      JSONArray benchmarks = new JSONArray();

      BenchmarkBase linuxBenchmark = new BenchmarkBase("Linux", "Linux (OS) supported tools",
            "Number of tools which support Linux OS.", "operatingSystem", "Linux");
      benchmarks.put(BioToolsBenchmark.countOSEntries(biotoolsAnnotations, linuxBenchmark).getJson());

      BenchmarkBase macOSBenchmark = new BenchmarkBase("Mac OS", "Mac OS supported tools",
            "Number of tools which support Mac OS.", "operatingSystem", "Mac");
      benchmarks.put(BioToolsBenchmark.countOSEntries(biotoolsAnnotations, macOSBenchmark).getJson());

      BenchmarkBase windowsBenchmark = new BenchmarkBase("Windows", "Windows (OS) supported tools",
            "Number of tools which support Windows OS.", "operatingSystem", "Windows");
      benchmarks.put(BioToolsBenchmark.countOSEntries(biotoolsAnnotations, windowsBenchmark).getJson());

      /*
       * BenchmarkBase licensedBenchmark = new BenchmarkBase("Licensed",
       * "Tools with a license",
       * "Number of tools which have a license specified.", "license", null);
       * benchmarks.put(BioToolsBenchmark.countLicencedEntries(biotoolsAnnotations,
       * licensedBenchmark).getJson());
       * 
       * BenchmarkBase bioToolBenchmark = new BenchmarkBase("In bio.tools",
       * "Available in bio.tools",
       * "Number of tools annotated in bio.tools.", null, null);
       * benchmarks.put(BioToolsBenchmark.countEntries(biotoolsAnnotations,
       * bioToolBenchmark).getJson());
       * 
       * BenchmarkBase openEBenchmark = new BenchmarkBase("In OpenEBench",
       * "Available in OpenEBench",
       * "Number of tools tracked in OpenEBench.", null, null);
       * benchmarks.put(BioToolsBenchmark.countEntries(biotoolsAnnotations,
       * bioToolBenchmark).getJson());
       */

      return benchmarks;

   }

   /**
    * Compute the OpenEBench benchmarks for the workflows and return it in JSON
    * format.
    * 
    * @param workflow
    * @return
    */
   static JSONArray computeOpenEBenchmarks(SolutionWorkflow workflow) {
      /*
       * For each tool in the workflow, get the OpenEBench annotations from OpenEBench
       * API
       */
      List<JSONObject> openEBenchBiotoolsMetrics = new ArrayList<>();

      workflow.getModuleNodes().forEach(toolNode -> {
         String toolID = toolNode.getUsedModule().getPredicateLabel();
         try {
            JSONObject openEBenchEntry = ToolBenchmarkingAPIs.fetchOEBMetricsForBiotoolsVersion(toolID);
            openEBenchEntry.put(ToolBenchmarkingAPIs.restAPEtoolID,
                  toolNode.getUsedModule().getPredicateLabel());
            openEBenchBiotoolsMetrics.add(openEBenchEntry);
         } catch (JSONException | IOException e) {
            JSONObject openEBenchEntry = new JSONObject();
            openEBenchEntry.put(ToolBenchmarkingAPIs.restAPEtoolID,
                  toolNode.getUsedModule().getPredicateLabel());
            openEBenchBiotoolsMetrics.add(openEBenchEntry);
            e.printStackTrace();
         }
      });

      JSONArray benchmarks = new JSONArray();

      BenchmarkBase licenseBenchmark = new BenchmarkBase("License", "License information available",
            "Number of tools which have a license specified.", "license", null);
      benchmarks.put(OpenEBenchmark.countLicenceOpenness(openEBenchBiotoolsMetrics, licenseBenchmark).getJson());

      BenchmarkBase citationsBenchmark = new BenchmarkBase("Citations", "Citations annotated per tool",
            "Number of citations per tool.", "citation", null);
      benchmarks.put(OpenEBenchmark.countCitationsBenchmark(openEBenchBiotoolsMetrics, citationsBenchmark).getJson());

      return benchmarks;

   }

   /**
    * Retrieve a JSON object corresponding to the tool from bio.tools for the given
    * tool ID.
    * The method uses the bio.tools API to fetch the annotations.
    * 
    * @param toolID - tool ID, not case sensitive. IDs are transformed into lower
    *               case as used in bio.tools, e.g.,
    *               "comet", "blast", etc.
    * @return JSONObject containing the metrics for the tool.
    * @throws IOException   In case the tool is not found in bio.tools.
    * @throws JSONException In case the JSON object returned by bio.tools cannot be
    *                       parsed.
    */
   public static JSONObject fetchToolFromBioTools(String toolID) throws JSONException, IOException {
      JSONObject bioToolAnnotation;
      toolID = toolID.toLowerCase();
      String urlToBioTools = "https://bio.tools/api/" + toolID +
            "?format=json";
      Request request = new Request.Builder().url(urlToBioTools).build();
      Response response = client.newCall(request).execute();

      if (!response.isSuccessful()) {
         throw new IOException("Tool " + toolID + " not found in bio.tools.");
      }

      bioToolAnnotation = new JSONObject(response.body().string());
      response.close();

      log.debug("The list of tools successfully fetched from bio.tools.");
      return bioToolAnnotation;
   }

   /**
    * Retrieve a list of JSON objects containing the metrics for each tool version
    * corresponding to the given tool ID from OpenEBench API. All version from
    * BioConda will be included, as well as all copies of bio.tools entry, each
    * representing a platform ("cdd", "cmd", "app", etc.).
    * 
    * @param toolID - tool ID, not case sensitive, (as used in
    *               bio.tools), e.g., "comet", "blast", etc.
    * @return List of JSONObjects, each containing the metrics for a tool
    *         version.
    * @throws IOException   - In case the tool is not found in bio.tools.
    * @throws JSONException - In case the JSON object returned by bio.tools or
    *                       OpenEBench API cannot be parsed.
    */
   public static List<JSONObject> fetchToolMetricsPerVersionFromOEB(String toolID)
         throws JSONException, IOException {
      toolID = toolID.toLowerCase();
      JSONArray openEBenchAggregateAnnotation = fetchToolAggregateFromOEB(toolID);
      List<String> toolOEBVersionsURLs = getToolVersionsURLs(openEBenchAggregateAnnotation);

      /*
       * Correct the URLs to point to the metrics rather than general tool
       * information. The OpenEBench API does not provide a more direct way to
       * retrieve the metrics.
       */
      toolOEBVersionsURLs = swapOEBCallTool2Metric(toolOEBVersionsURLs);

      List<JSONObject> openEBenchToolVersions = new ArrayList<>();

      // retrieve the JSON metrics for each tool version
      toolOEBVersionsURLs.forEach(metricOEBenchURL -> {
         try {
            File file = APEFiles.readPathToFile(metricOEBenchURL);
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            openEBenchToolVersions.add(new JSONObject(content));
         } catch (JSONException e) {
            log.error("Tool version metrics JSON provided by OEB could not be parsed.");
            e.printStackTrace();
         } catch (IOException e) {
            log.error("Tool version metrics file could not be created.");
            e.printStackTrace();
         }
      });

      log.debug("The list of tool versions was successfully fetched from OpenEBench.");
      return openEBenchToolVersions;

   }

   /**
    * Retrieve a JSON objects containing the metrics for the bio.tools entry for
    * the given tool ID from OpenEBench API.
    * 
    * @param toolID - tool ID, not case sensitive, (as used in
    *               bio.tools), e.g., "comet", "blast", etc.
    * @return JSONObject, containing the metrics for the tool version.
    * @throws IOException   - In case the tool is not found in bio.tools.
    * @throws JSONException - In case the JSON object returned by bio.tools or
    *                       OpenEBench API cannot be parsed.
    */
   public static JSONObject fetchOEBMetricsForBiotoolsVersion(String toolID)
         throws JSONException, IOException {
      toolID = toolID.toLowerCase();
      JSONArray openEBenchAggregateAnnotation = fetchToolAggregateFromOEB(toolID);

      String biotoolsVersionURL = getToolVersionsURLs(openEBenchAggregateAnnotation).stream()
            .filter(url -> url.contains("biotools:"))
            .findFirst().orElse(null);

      if (biotoolsVersionURL == null) {
         return new JSONObject();
      }

      /*
       * Correct the URL to point to the metrics rather than general tool
       * information. The OpenEBench API does not provide a more direct way to
       * retrieve the metrics.
       */
      biotoolsVersionURL = swapOEBCallTool2Metric(biotoolsVersionURL);

      // retrieve the JSON metrics for each tool version
      String metricsJson = "";
      try {
         File metricsFile = APEFiles.readPathToFile(biotoolsVersionURL);
         metricsJson = FileUtils.readFileToString(metricsFile, StandardCharsets.UTF_8);
      } catch (JSONException e) {
         log.error("Tool version metrics JSON provided by OEB could not be parsed.");
         e.printStackTrace();
      } catch (IOException e) {
         log.error("Tool version metrics file could not be created.");
         e.printStackTrace();
      }

      log.debug("The list of tool versions was successfully fetched from OpenEBench.");
      return new JSONObject(metricsJson);

   }

   /**
    * Use the given URL to fetch the JSON object, using the OkHttp client. In case
    * of a failure, the method will return empty JSON object.
    * 
    * @param url - URL to be used to fetch the JSON object
    * @return JSON object
    */
   public static JSONObject getJSONfromURL(String url) {
      JSONObject responseJSON = null;
      try {
         Request request = new Request.Builder().url(url).build();
         Response response = client.newCall(request).execute();

         if (!response.isSuccessful()) {
            throw new IOException("Unexpected code when trying to fetch" + response);
         }

         responseJSON = new JSONObject(response.body().string());
         response.close();
      } catch (IOException e) {
         e.printStackTrace();
      }

      return responseJSON;

   }

   /**
    * Parse OpenEBench aggregated annotations to get the list of tool versions and
    * their URLs.
    * 
    * @param openEBenchAggregateAnnotation - JSONArray of JSONObjects, each of
    *                                      which contains a aggregated tool
    *                                      annotation.
    * @return List of tool version URLs.
    */
   static List<String> getToolVersionsURLs(JSONArray openEBenchAggregateAnnotation) throws JSONException {

      List<String> openEBenchToolVersions = new ArrayList<>();
      // Retrieve a list of tool versions

      openEBenchAggregateAnnotation.forEach(tool -> ((JSONObject) tool).getJSONArray("entities").forEach(
            toolType -> {
               if (((JSONObject) toolType).get("type") != null) {
                  ((JSONObject) toolType).getJSONArray("tools")
                        .forEach(
                              toolVersion -> openEBenchToolVersions.add(((JSONObject) toolVersion).getString("@id")));
               }
            }));
      return openEBenchToolVersions;
   }

   /**
    * Filter out non-bio.tools URLs from the list of tool versions, by removing all
    * URLs that do not contain "biotools:" string.
    * 
    * @param openEBenchToolVersionURLs
    * @return true if the list was modified, false otherwise
    */
   static boolean filterOutNonBioTools(List<String> openEBenchToolVersionURLs) {
      return openEBenchToolVersionURLs.removeIf(url -> !url.contains("biotools:"));
   }

   /**
    * Create a new list of URLs that references tool metrics rather than general
    * tool
    * information. In practice, the method replaces `/tool/` with `/metric/` in the
    * URL.
    * 
    * @param openEBenchToolVersionURLs
    * @return List of URLs that reference tool metrics.
    */
   static List<String> swapOEBCallTool2Metric(List<String> openEBenchToolVersionURLs) {
      return openEBenchToolVersionURLs.stream().map(url -> swapOEBCallTool2Metric(url)).toList();
   }

   /**
    * Create a new URL that references tool metrics rather than general tool
    * information. In practice, the method replaces `/tool/` with `/metric/` in the
    * URL.
    * 
    * @param openEBenchToolVersionURL
    * @return URL that references tool metrics.
    */
   static String swapOEBCallTool2Metric(String openEBenchToolVersionURL) {
      return openEBenchToolVersionURL.replaceFirst("/tool/", "/metrics/");
   }

   /**
    * Get a list of tool versions from OpenEBench based on the tool ID. Each entry
    * contains general information about the tool version and a link (under "@id")
    * to
    * the detailed information. The same link, when `/tool/` is replaced with
    * `/metrics/` can be used to retrieve the metrics for the tool version.<br>
    * <br>
    * <strong>Important:</strong>
    * The OpenEBench API does not provide a more direct way to
    * retrieve the metrics, and thus, the URLs must be corrected to point to the
    * metrics rather than general tool
    * information.
    * 
    * @param toolID
    * @return
    * @throws JSONException In case the JSON object returned by OpenEBench API
    *                       cannot be parsed.
    * @throws IOException   In case a local file cannot be created.
    */
   static JSONArray fetchToolAggregateFromOEB(String toolID) throws JSONException, IOException {
      JSONArray openEBenchAnnotation;
      String urlToAggregateOEB = "https://openebench.bsc.es/monitor/rest/aggregate?id=" + toolID;

      File file = APEFiles.readPathToFile(urlToAggregateOEB);
      openEBenchAnnotation = APEFiles.readFileToJSONArray(file);

      log.debug("The list of tool aggregations was successfully fetched from OpenEBench.");
      return openEBenchAnnotation;
   }

   /**
    * Parse the JSON object returned by OpenEBench API describing the tool metrics
    * and return whether the tool has an OSI approved license.
    * 
    * @param tootMetrics - JSON object returned by OpenEBench API describing the
    *                    tool metrics.
    * @return true if the tool has an OSI approved license, false otherwise.
    */
   public static LicenseType isOSIFromOEBMetrics(JSONObject tootMetrics) throws JSONException {
      JSONObject licenseJson;
      try {
         licenseJson = tootMetrics.getJSONObject("project").getJSONObject("license");
      } catch (JSONException e) {
         return LicenseType.Unknown;
      }

      boolean isOSI = licenseJson.getBoolean("osi");
      if (isOSI) {
         return LicenseType.OSI_Approved;
      } else if (licenseJson.getBoolean("open_source")) {
         return LicenseType.Open;
      } else {
         return LicenseType.Closed;
      }
   }

}

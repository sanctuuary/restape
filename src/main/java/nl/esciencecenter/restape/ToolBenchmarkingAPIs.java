package nl.esciencecenter.restape;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.io.FileUtils;
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
   private static final OkHttpClient client = new OkHttpClient();

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
         /* 
          * Compute the benchmarks for the workflow and save them in a JSON file.
          */
         JSONArray combinedBenchmarks = computeWorkflowBenchmarks(workflow);
         JSONObject workflowBenchmarks = computeWorkflowSpecificFields(workflow, runID);
         workflowBenchmarks.put("benchmarks", combinedBenchmarks);

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

   /**
    * Compute the benchmarks (based on bio.tools and OpenEBench APIs) for the workflows and return it in JSON format.
    * 
    * @param workflow - workflow for which the benchmarks should be computed.
    * @return JSONArray containing the benchmarks for the workflow.
    */
   private static JSONArray computeWorkflowBenchmarks(SolutionWorkflow workflow) {
      JSONArray biotoolsBenchmark = computeBiotoolsBenchmark(workflow);
      JSONArray openEBenchmarks = computeOpenEBenchmarks(workflow);

      for (int i = 0; i < openEBenchmarks.length(); i++) {
         biotoolsBenchmark.put(openEBenchmarks.get(i));
      }

      return biotoolsBenchmark;
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
    * Compute the bio.tools benchmarks for the workflows by using bio.tools API to get information about each tool in the workflow. The result (the benchmarks) is returned in JSON
    * format.
    * 
    * @param workflow - workflow for which the benchmarks should be computed.
    * @return JSONArray containing the benchmarks for the workflow.
    */
   private static JSONArray computeBiotoolsBenchmark(SolutionWorkflow workflow) {

      // for each tool in the workflow, get the biotools annotations from bio.tool API
      List<JSONObject> biotoolsAnnotations = new ArrayList<>();

      workflow.getModuleNodes().forEach(toolNode -> {
         String toolID = toolNode.getUsedModule().getPredicateLabel();
         JSONObject biotoolsEntry = new JSONObject();
         try {
            biotoolsEntry = ToolBenchmarkingAPIs.fetchToolFromBioTools(toolID);
         } catch (JSONException | IOException e) {
            e.printStackTrace();
         } finally {
            biotoolsEntry.put(ToolBenchmarkingAPIs.restAPEtoolID, toolNode.getUsedModule().getPredicateLabel());
            biotoolsAnnotations.add(biotoolsEntry);
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

      return benchmarks;

   }

   /**
    * Compute the OpenEBench benchmarks for the workflows by using OpenEBench API to get information about each tool in the workflow. The result (the benchmarks) is returned in JSON
    * format.
    * 
    * @param workflow - workflow for which the benchmarks should be computed. 
    * @return 
    */
   private static JSONArray computeOpenEBenchmarks(SolutionWorkflow workflow) {
      /*
       * For each tool in the workflow, get the OpenEBench annotations from OpenEBench
       * API
       */
      List<JSONObject> openEBenchBiotoolsMetrics = new ArrayList<>();

      workflow.getModuleNodes().forEach(toolNode -> {
         String toolID = toolNode.getUsedModule().getPredicateLabel();
         JSONObject openEBenchEntry = new JSONObject();
         try {
            openEBenchEntry = ToolBenchmarkingAPIs.fetchOEBMetricsForBiotoolsVersion(toolID);
         } catch (JSONException e) {
            e.printStackTrace();
         } catch (IOException e) {
            log.error("Tool {} not found in OpenEBench. It will not be benchmarked.", toolID);
         } finally {
            openEBenchEntry.put(ToolBenchmarkingAPIs.restAPEtoolID, toolNode.getUsedModule().getPredicateLabel());
            openEBenchBiotoolsMetrics.add(openEBenchEntry);
         }
      });

      JSONArray benchmarks = new JSONArray();

      BenchmarkBase licenseBenchmark = new BenchmarkBase("License", "License information available",
            "Number of tools which have a license specified.", "license", null);
      benchmarks.put(OpenEBenchmark.countLicenseOpenness(openEBenchBiotoolsMetrics, licenseBenchmark).getJson());

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
   static JSONObject fetchToolFromBioTools(String toolID) throws JSONException, IOException {
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
    * @throws IOException   - In case the tool is not found in OpenEBench.
    * @throws JSONException - In case the JSON object returned by bio.tools or
    *                       OpenEBench API cannot be parsed.
    */
   static List<JSONObject> fetchToolMetricsPerVersionFromOEB(String toolID)
         throws JSONException, IOException {
      toolID = toolID.toLowerCase();
      JSONArray openEBenchAggregateAnnotation = fetchToolAggregateFromOEB(toolID);
      List<String> toolOEBVersionsURLs = getToolVersionsURLs(openEBenchAggregateAnnotation);

      /*
       * Correct the URLs to point to the metrics rather than general tool
       * information. The OpenEBench API does not provide a more direct way to
       * retrieve the metrics.
       */
      toolOEBVersionsURLs = replaceTool2MetricInOEBCall(toolOEBVersionsURLs);

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
   static JSONObject fetchOEBMetricsForBiotoolsVersion(String toolID)
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
      biotoolsVersionURL = replaceTool2MetricInOEBCall(biotoolsVersionURL);

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
   static List<String> replaceTool2MetricInOEBCall(List<String> openEBenchToolVersionURLs) {
      return openEBenchToolVersionURLs.stream().map(url -> replaceTool2MetricInOEBCall(url)).toList();
   }

   /**
    * Create a new URL that references tool metrics rather than general tool
    * information. In practice, the method replaces `/tool/` with `/metric/` in the
    * URL.
    * 
    * @param openEBenchToolVersionURL
    * @return URL that references tool metrics.
    */
   static String replaceTool2MetricInOEBCall(String openEBenchToolVersionURL) {
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

}

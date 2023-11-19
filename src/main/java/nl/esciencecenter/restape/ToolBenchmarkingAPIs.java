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

import com.oracle.truffle.regex.tregex.util.json.JsonObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.esciencecenter.models.BenchmarkBase;
import nl.esciencecenter.models.BioToolsBenchmark;
import nl.uu.cs.ape.solver.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.solver.solutionStructure.SolutionsList;
import nl.uu.cs.ape.utils.APEFiles;

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
         JSONObject workflowBenchmarks = RestApeUtils.combineJSONObjects(
               computeWorkflowSpecificFields(workflow, runID),
               computeBiotoolsBenchmark(workflow),
               computeOpenEBenchmarks(workflow));
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
   private static JSONObject computeBiotoolsBenchmark(SolutionWorkflow workflow) {
      JSONObject benchmarkResult = new JSONObject();

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

      BenchmarkBase licensedBenchmark = new BenchmarkBase("Licensed", "Tools with a license",
            "Number of tools which have a license specified.", "license", null);
      benchmarks.put(BioToolsBenchmark.countLicencedEntries(biotoolsAnnotations, licensedBenchmark).getJson());

      BenchmarkBase linuxBenchmark = new BenchmarkBase("Linux", "Linux (OS) supported tools",
            "Number of tools which support Linux OS.", "operatingSystem", "Linux");
      benchmarks.put(BioToolsBenchmark.countOSEntries(biotoolsAnnotations, linuxBenchmark).getJson());

      BenchmarkBase macOSBenchmark = new BenchmarkBase("Mac OS", "Mac OS supported tools",
            "Number of tools which support Mac OS.", "operatingSystem", "Mac");
      benchmarks.put(BioToolsBenchmark.countOSEntries(biotoolsAnnotations, macOSBenchmark).getJson());

      BenchmarkBase windowsBenchmark = new BenchmarkBase("Windows", "Windows (OS) supported tools",
            "Number of tools which support Windows OS.", "operatingSystem", "Windows");
      benchmarks.put(BioToolsBenchmark.countOSEntries(biotoolsAnnotations, windowsBenchmark).getJson());

      BenchmarkBase bioToolBenchmark = new BenchmarkBase("In bio.tools", "Available in bio.tools",
            "Number of tools annotated in bio.tools.", null, null);
      benchmarks.put(BioToolsBenchmark.countEntries(biotoolsAnnotations, bioToolBenchmark).getJson());

      // BenchmarkBase openEBenchmark = new BenchmarkBase("In OpenEBench", "Available
      // in OpenEBench",
      // "Number of tools tracked in OpenEBench.", null, null);
      // benchmarks.put(BioToolsBenchmark.countEntries(biotoolsAnnotations,
      // bioToolBenchmark).getJson());

      benchmarkResult.put("benchmarks", benchmarks);

      return benchmarkResult;

   }

   /**
    * Compute the OpenEBench benchmarks for the workflows and return it in JSON
    * format.
    * 
    * @param workflow
    * @return
    */
   private static JSONObject computeOpenEBenchmarks(SolutionWorkflow workflow) {
      JSONObject benchmarkResult = new JSONObject();

      // for each tool in the workflow, get the openEBench annotations from bio.tool
      // API
      List<JSONObject> openEBenchAnnotations = new ArrayList<>();

      // TODO: uncoment
      // workflow.getModuleNodes().forEach(toolNode -> {
      // String toolID = toolNode.getUsedModule().getPredicateLabel();
      // try {

      // JSONArray openEBenchEntry =
      // ToolBenchmarkingAPIs.fetchToolVersionsFromOEB(toolID);
      // openEBenchEntry.put(ToolBenchmarkingAPIs.restAPEtoolID,
      // toolNode.getUsedModule().getPredicateLabel());
      // openEBenchAnnotations.add(openEBenchEntry);
      // } catch (JSONException | IOException e) {
      // JSONObject openEBenchEntry = new JSONObject();
      // openEBenchEntry.put(ToolBenchmarkingAPIs.restAPEtoolID,
      // toolNode.getUsedModule().getPredicateLabel());
      // openEBenchAnnotations.add(openEBenchEntry);
      // e.printStackTrace();
      // }
      // });

      JSONArray benchmarks = new JSONArray();

      benchmarkResult.put("benchmarks", benchmarks);

      return benchmarkResult;

   }

   /**
    * Get the JSON annotations from bio.tools for the given tool IDs.
    * The method uses the bio.tools API to fetch the annotations.
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
    * Retrieve a list of JSON objects corresponding to tool versions from
    * OpenEBench for the given tool ID.
    * 
    * @param toolID            - tool ID, not case sensitive, (as used in
    *                          bio.tools), e.g., "comet", "blast", etc.
    * @param biotoolsExclusive - if true, only bio.tools URLs will be returned
    * @return JSONArray of JSONObjects, each containing the metrics for a tool
    *         version.
    * @throws IOException
    * @throws JSONException
    */
   public static JSONArray fetchToolVersionsFromOEB(String toolID, boolean biotoolsExclusive)
         throws JSONException, IOException {
      toolID = toolID.toLowerCase();
      JSONArray openEBenchAggregateAnnotation = fetchToolAggregateFromOEB(toolID);
      List<String> toolOEBVersionsURLs = getToolVersionsURLs(openEBenchAggregateAnnotation);

      if (biotoolsExclusive) {
         filterOutNonBioTools(toolOEBVersionsURLs);
      }
      swapOEBCallTool2Metric(toolOEBVersionsURLs);

      JSONArray openEBenchToolVersions = new JSONArray();

      // retrieve the JSON metrics for each tool version
      toolOEBVersionsURLs.forEach(metricOEBenchURL -> {
         try {
            openEBenchToolVersions.put(APEFiles.readPathToJSONObject(metricOEBenchURL));
         } catch (JSONException | IOException e) {
            e.printStackTrace();
         }
      });

      log.debug("The list of tool versions was successfully fetched from OpenEBench.");
      return openEBenchToolVersions;
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
    * @param openEBenchAggregateAnnotation
    * @return
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
    * Change the URL to retrieve detailed tool metrics rather than general tool
    * information.
    * 
    * @param openEBenchToolVersionURLs
    * @return
    */
   static List<String> swapOEBCallTool2Metric(List<String> openEBenchToolVersionURLs) {
      openEBenchToolVersionURLs.forEach(url -> url.replaceFirst("/tool/", "/metric/"));
      return openEBenchToolVersionURLs;
   }

   /**
    * Get a list of tool versions from OpenEBench based on the tool ID. Each entry
    * contains general information about the tool version and a link (under "@id")
    * to
    * the detailed information. The same link, when `/tool/` is replaced with
    * `/metrics/` can be used to retrieve the metrics for the tool version.
    * 
    * @param toolID
    * @return
    * @throws JSONException
    * @throws IOException
    */
   public static JSONArray fetchToolAggregateFromOEB(String toolID) throws JSONException, IOException {
      JSONArray openEBenchAnnotation;
      String urlToAggregateOEB = "https://openebench.bsc.es/monitor/rest/aggregate?id=" + toolID;

      File file = APEFiles.readPathToFile(urlToAggregateOEB);
      openEBenchAnnotation = APEFiles.readFileToJSONArray(file);

      log.debug("The list of tool aggregations was successfully fetched from OpenEBench.");
      return openEBenchAnnotation;
   }

}

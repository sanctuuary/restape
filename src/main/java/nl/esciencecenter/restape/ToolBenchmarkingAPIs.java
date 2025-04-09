package nl.esciencecenter.restape;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.esciencecenter.externalAPIs.BioToolsBenchmarkProcessor;
import nl.esciencecenter.externalAPIs.BioToolsRestClient;
import nl.esciencecenter.externalAPIs.OpenEBenchBenchmarkProcessor;
import nl.esciencecenter.externalAPIs.OpenEBenchRestClient;
import nl.esciencecenter.models.benchmarks.Benchmark;
import nl.esciencecenter.models.benchmarks.BenchmarkBase;
import nl.uu.cs.ape.solver.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.solver.solutionStructure.SolutionsList;
import nl.uu.cs.ape.solver.solutionStructure.cwl.DefaultCWLCreator;
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
         JSONObject workflowBenchmarks = computeWorkflowSpecificFields(workflow, runID);
         JSONArray benchmarks = computeWorkflowBenchmarks(workflow);
         JSONObject additionalBenchmarks = getPubmetricBenchmarks(workflow);

         if (!additionalBenchmarks.isEmpty() && additionalBenchmarks.has("benchmarks")) {
            additionalBenchmarks.getJSONArray("benchmarks").forEach(benchmark -> benchmarks.put(benchmark));
         }

         workflowBenchmarks.put("benchmarks", benchmarks);

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
    * Get the Pubmetric benchmarks for the workflow.
    * 
    * @param workflow the SolutionWorkflow instance
    * @return JSON response from Pubmetric API
    */
   public static JSONObject getPubmetricBenchmarks(SolutionWorkflow workflow) {
      DefaultCWLCreator cwlCreator = new DefaultCWLCreator(workflow);
      String cwlFileContent = cwlCreator.generate();
      byte[] cwlFileBytes = cwlFileContent.getBytes();

      return sendPostToPubmetric(cwlFileBytes);
   }

   /**
    * Send a POST request to the Pubmetric API to get benchmarks.
    * 
    * @param cwlFileBytes byte array of CWL file content
    * @return JSON response from Pubmetric API
    */
   public static JSONObject sendPostToPubmetric(byte[] cwlFileBytes) {
      // Create the HTTP client
      CloseableHttpClient httpClient = HttpClients.createDefault();

      String url = String.format("http://%s:%s/score_workflow/", Dotenv.load().get("PUBMETRIC_ENDPOINT"), Dotenv.load().get("PUBMETRIC_PORT"));
      HttpPost uploadFile = new HttpPost(url);

      // Create a multipart entity with the CWL file
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.addBinaryBody("cwl_file", cwlFileBytes, org.apache.http.entity.ContentType.DEFAULT_BINARY,
            "workflow.cwl");

      HttpEntity multipart = builder.build();
      uploadFile.setEntity(multipart);

      // Execute the request
      CloseableHttpResponse response;
      try {
         response = httpClient.execute(uploadFile);
         HttpEntity responseEntity = response.getEntity();

         // Print the response
         if (responseEntity != null) {
            String responseString = EntityUtils.toString(responseEntity);
            return new JSONObject(responseString);
         }

         // Close resources
         response.close();
         httpClient.close();
      } catch (IOException e) {
         log.error("Error while fetching the Pubmetric benchmarks");
      } catch (JSONException e) {
         log.error("Error while parsing the Pubmetric benchmarks");
      }

      return new JSONObject();
   }

   /**
    * Compute the benchmarks (based on bio.tools and OpenEBench APIs) for the
    * workflows and return it in JSON format.
    * 
    * @param workflow - workflow for which the benchmarks should be computed.
    * @return JSONArray containing the benchmarks for the workflow.
    */
   private static JSONArray computeWorkflowBenchmarks(SolutionWorkflow workflow) {

      JSONArray benchmarksJSON = new JSONArray();
      computeBiotoolsBenchmark(workflow).forEach(benchmark -> benchmarksJSON.put(benchmark.toJSON()));
      computeOpenEBenchmarks(workflow).forEach(benchmark -> benchmarksJSON.put(benchmark.toJSON()));

      return benchmarksJSON;
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
    * Compute the bio.tools benchmarks for the workflows by using bio.tools API to
    * get information about each tool in the workflow. The result (the benchmarks)
    * is returned in JSON
    * format.
    * 
    * @param workflow - workflow for which the benchmarks should be computed.
    * @return JSONArray containing the benchmarks for the workflow.
    */
   private static List<Benchmark> computeBiotoolsBenchmark(SolutionWorkflow workflow) {

      // for each tool in the workflow, get the biotools annotations from bio.tool API
      List<JSONObject> biotoolsAnnotations = new ArrayList<>();

      workflow.getModuleNodes().forEach(toolNode -> {
         String toolID = toolNode.getUsedModule().getPredicateLabel();
         JSONObject biotoolsEntry = new JSONObject();
         try {
            biotoolsEntry = BioToolsRestClient.fetchToolFromBioTools(toolID);
         } catch (JSONException | IOException e) {
            log.warn(e.getMessage());
         } finally {
            biotoolsEntry.put(ToolBenchmarkingAPIs.restAPEtoolID, toolNode.getUsedModule().getPredicateLabel());
            biotoolsAnnotations.add(biotoolsEntry);
         }
      });

      List<Benchmark> benchmarks = new ArrayList<>();

      String unitOS = "supported / not supported";
      BenchmarkBase linuxBenchmark = new BenchmarkBase("Linux", "OS", "Linux (OS) supported tools",
            unitOS, "operatingSystem", "Linux");
      benchmarks.add(BioToolsBenchmarkProcessor.benchmarkOSSupport(biotoolsAnnotations, linuxBenchmark));

      BenchmarkBase macOSBenchmark = new BenchmarkBase("Mac OS", "OS", "Mac OS supported tools",
            unitOS, "operatingSystem", "Mac");
      benchmarks.add(BioToolsBenchmarkProcessor.benchmarkOSSupport(biotoolsAnnotations, macOSBenchmark));

      BenchmarkBase windowsBenchmark = new BenchmarkBase("Windows", "OS", "Windows (OS) supported tools",
            unitOS, "operatingSystem", "Windows");
      benchmarks.add(BioToolsBenchmarkProcessor.benchmarkOSSupport(biotoolsAnnotations, windowsBenchmark));

      return benchmarks;

   }

   /**
    * Compute the OpenEBench benchmarks for the workflows by using OpenEBench API
    * to get information about each tool in the workflow. The result (the
    * benchmarks) is returned in JSON
    * format.
    * 
    * @param workflow - workflow for which the benchmarks should be computed.
    * @return
    */
   private static List<Benchmark> computeOpenEBenchmarks(SolutionWorkflow workflow) {
      /*
       * For each tool in the workflow, get the OpenEBench annotations from OpenEBench
       * API.
       */
      List<JSONObject> openEBenchBiotoolsMetrics = new ArrayList<>();

      /* Check if OpenEBench service is available. */
      // OpenEBenchRestClient.checkAvailability();

      workflow.getModuleNodes().forEach(toolNode -> {
         String toolID = toolNode.getUsedModule().getPredicateLabel();
         JSONObject openEBenchEntry = new JSONObject();
         try {
            openEBenchEntry = OpenEBenchRestClient.fetchToolMetricsBiotoolsVersion(toolID);
         } catch (JSONException e) {
            e.printStackTrace();
         } catch (IOException e) {
            log.error("Tool {} not found in OpenEBench. It will not be benchmarked.", toolID);
         } finally {
            openEBenchEntry.put(ToolBenchmarkingAPIs.restAPEtoolID, toolNode.getUsedModule().getPredicateLabel());
            openEBenchBiotoolsMetrics.add(openEBenchEntry);
         }
      });

      List<Benchmark> benchmarks = new ArrayList<>();

      BenchmarkBase licenseBenchmark = new BenchmarkBase("License", "License", "License information available",
            "license type", "license", null);
      benchmarks.add(OpenEBenchBenchmarkProcessor.benchmarkLicenses(openEBenchBiotoolsMetrics, licenseBenchmark));

      BenchmarkBase citationsBenchmark = new BenchmarkBase("Citations", "Bibliometrics", "Citations annotated per tool",
            "citation count", "citation", null);
      benchmarks
            .add(OpenEBenchBenchmarkProcessor.countCitationsBenchmark(openEBenchBiotoolsMetrics, citationsBenchmark));

      return benchmarks;

   }

}

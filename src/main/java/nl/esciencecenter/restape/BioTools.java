package nl.esciencecenter.restape;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BioTools {

   private static final Logger log = LoggerFactory.getLogger(BioTools.class);
   public static final OkHttpClient client = new OkHttpClient();

   public static JSONObject fetchToolFromBioTools(String toolID) throws JSONException, IOException {
      JSONObject bioToolAnnotation;
      toolID = toolID.toLowerCase();
      // String urlToJson = "https://130.226.25.21/api/" + toolID + "?format=json";
      String urlToJson = "https://raw.githubusercontent.com/bio-tools/content-sandbox/master/data/" +
            toolID + "/"
            + toolID + ".biotools.json";
      Request request = new Request.Builder().url(urlToJson).build();
      Response response = client.newCall(request).execute();

      try {
         if (!response.isSuccessful()) {
            throw new IOException("Unexpected code when trying to fetch" + response);
         }

         bioToolAnnotation = new JSONObject(response.body().string());
      } catch (IOException e) {
         if (response != null) {
            response.close();
         }
         throw e;
      }

      if (response != null) {
         response.close();
      }

      log.debug("The list of tools successfully fetched from bio.tools.");
      return bioToolAnnotation;
   }

   public static JSONObject countEntries(List<JSONObject> biotoolsAnnotations, int workflowLength) {

      JSONObject benchmarkJson = new JSONObject();
      benchmarkJson.put("benchmark_title", "bio.tool");
      benchmarkJson.put("benchmark_long_title", "Available in bio.tools");
      benchmarkJson.put("benchmark_description", "Number of tools annotated in bio.tools.");

      int count = biotoolsAnnotations.size();
      double desirability = strictDistribution(count, workflowLength);

      benchmarkJson.put("value", ratioString(count, workflowLength));
      benchmarkJson.put("desirability_value", Double.toString(desirability));
      return benchmarkJson;
   }

   private static String ratioString(int count, int length) {
      return count + "/" + length;
   }

   public static JSONObject countLinuxEntries(List<JSONObject> biotoolsAnnotations, int workflowLength) {

      JSONObject benchmarkJson = new JSONObject();
      benchmarkJson.put("benchmark_title", "Linux");
      benchmarkJson.put("benchmark_long_title", "Linux (OS) supported tools");
      benchmarkJson.put("benchmark_description", "Number of tools which support Linux OS.");

      int count = countArrayFields(biotoolsAnnotations, "operatingSystem", "Linux");
      double desirability = normalDistribution(count, workflowLength);

      benchmarkJson.put("value", ratioString(count, workflowLength));
      benchmarkJson.put("desirability_value", Double.toString(desirability));

      return benchmarkJson;
   }

   private static double normalDistribution(int count, int workflowLength) {
      return 1.0 * count / workflowLength;
   }

   private static double strictDistribution(int count, int workflowLength) {
      if (count == workflowLength) {
         return 1;
      } else {
         return 1.0 * count / workflowLength / 10;
      }
   }

   public static JSONObject countMacOSEntries(List<JSONObject> biotoolsAnnotations, int workflowLength) {

      JSONObject benchmarkJson = new JSONObject();
      benchmarkJson.put("benchmark_title", "Mac OS");
      benchmarkJson.put("benchmark_long_title", "Mac OS supported tools");
      benchmarkJson.put("benchmark_description", "Number of tools which support Mac OS.");

      int count = countArrayFields(biotoolsAnnotations, "operatingSystem", "Mac");
      double desirability = normalDistribution(count, workflowLength);

      benchmarkJson.put("value", ratioString(count, workflowLength));
      benchmarkJson.put("desirability_value", Double.toString(desirability));

      return benchmarkJson;
   }

   public static JSONObject countWindowsEntries(List<JSONObject> biotoolsAnnotations, int workflowLength) {

      JSONObject benchmarkJson = new JSONObject();
      benchmarkJson.put("benchmark_title", "Windows");
      benchmarkJson.put("benchmark_long_title", "Windows (OS) supported tools");
      benchmarkJson.put("benchmark_description", "Number of tools which support Windows OS.");

      int count = countArrayFields(biotoolsAnnotations, "operatingSystem", "Windows");
      double desirability = normalDistribution(count, workflowLength);

      benchmarkJson.put("value", ratioString(count, workflowLength));
      benchmarkJson.put("desirability_value", Double.toString(desirability));

      return benchmarkJson;
   }

   public static JSONObject countLicencedEntries(List<JSONObject> biotoolsAnnotations, int workflowLength) {

      JSONObject benchmarkJson = new JSONObject();
      benchmarkJson.put("benchmark_title", "Licensed");
      benchmarkJson.put("benchmark_long_title", "Tools with a license");
      benchmarkJson.put("benchmark_description", "Number of tools which have a license specified.");

      int count = countExistanceOfFields(biotoolsAnnotations, "license");
      double desirability = strictDistribution(count, workflowLength);

      benchmarkJson.put("value", ratioString(count, workflowLength));
      benchmarkJson.put("desirability_value", Double.toString(desirability));

      return benchmarkJson;
   }

   private static int countArrayFields(List<JSONObject> biotoolsAnnotations, String fieldName, String fieldValue) {
      int count = 0;

      // for each tool in the workflow, get the biotools metadata from bio.tool API
      biotoolsAnnotations.stream().filter(tool -> inStringArray(tool, fieldName, fieldValue)).forEach(biotoolJson -> {

      });
      return count;
   }

   private static int countExistanceOfFields(List<JSONObject> biotoolsAnnotations, String fieldName) {
      int count = 0;

      // for each tool in the workflow, get the biotools metadata from bio.tool API
      biotoolsAnnotations.stream().filter(tool -> inAvailable(tool, fieldName)).forEach(biotoolJson -> {

      });
      return count;
   }

   /**
    * Check whether the given value is in the set of given values.
    */
   private static boolean inStringArray(JSONObject biotoolJson, String fieldName, String expectedFieldValue) {
      if (biotoolJson == null || biotoolJson.isEmpty()) {
         return false;
      }
      try {
         JSONArray fieldValues = biotoolJson.getJSONArray(fieldName);
         for (int i = 0; i < biotoolJson.length(); i++) {
            if (fieldValues.getString(i).equals(expectedFieldValue)) {
               return true;
            }
         }
      } catch (JSONException e) {
         return false;
      }
      return false;
   }

   private static boolean inAvailable(JSONObject biotoolJson, String fieldName) {
      try {
         if (biotoolJson.get(fieldName) != null) {
            return true;
         }
      } catch (JSONException x) {
         return false;
      }
      return false;
   }
}

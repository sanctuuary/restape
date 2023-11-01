package nl.esciencecenter.restape;

import java.io.IOException;
import java.util.ArrayList;
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
import nl.esciencecenter.models.BenchmarkBase;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BioTools {

   private static final Logger log = LoggerFactory.getLogger(BioTools.class);
   public static final OkHttpClient client = new OkHttpClient();

   /**
    * Get the JSON annotations from bio.tools for the given tool IDs.
    * The method uses the bio.tools API to fetch the annotations.
    */
   public static JSONObject fetchToolFromBioTools(String toolID) throws JSONException, IOException {
      JSONObject bioToolAnnotation;
      toolID = toolID.toLowerCase();
      String urlToBioToolsJson = "https://bio.tools/api/" + toolID +
            "?format=json";
      Request request = new Request.Builder().url(urlToBioToolsJson).build();
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

}

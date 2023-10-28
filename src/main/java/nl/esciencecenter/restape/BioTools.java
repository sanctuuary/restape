package nl.esciencecenter.restape;

import java.io.File;
import java.io.IOException;
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

   public static JSONObject fetchToolBioTools(String toolID) throws JSONException, IOException {
      JSONObject bioToolAnnotation;
      Request request = (new Request.Builder()).url("https://bio.tools/api/" + toolID + "?format=json").build();
      Response response = client.newCall(request).execute();

      try {
         if (!response.isSuccessful()) {
            throw new IOException("Unexpected code when trying to fetch" + response);
         }

         bioToolAnnotation = new JSONObject(response.body().string());
      } catch (Throwable var9) {
         if (response != null) {
            try {
               response.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (response != null) {
         response.close();
      }

      log.debug("The list of tools successfully fetched from bio.tools.");
      return bioToolAnnotation;
   }
}

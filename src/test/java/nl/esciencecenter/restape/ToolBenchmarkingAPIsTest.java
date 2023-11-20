package nl.esciencecenter.restape;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ToolBenchmarkingAPIsTest {

    @Test
    void getToolsFromURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> ApeAPI.getTools(urlPath));

    }

    @Test
    void fetchToolAggregateFromOEB() {
        String toolID = "comet";
        JSONArray openEBenchAggregateAnnotation = null;
        try {
            openEBenchAggregateAnnotation = ToolBenchmarkingAPIs.fetchToolAggregateFromOEB(toolID);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            fail();
        }
        assertFalse(openEBenchAggregateAnnotation == null || openEBenchAggregateAnnotation.isEmpty());
    }

    @Test
    void getToolVersionsURLs() {
        String toolID = "comet";
        List<String> toolOEBVersionsURLs = null;
        try {
            JSONArray openEBenchAggregateAnnotation = ToolBenchmarkingAPIs.fetchToolAggregateFromOEB(toolID);
            toolOEBVersionsURLs = ToolBenchmarkingAPIs.getToolVersionsURLs(openEBenchAggregateAnnotation);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            fail();
        }

        assertFalse(toolOEBVersionsURLs == null || toolOEBVersionsURLs.isEmpty()
                || !toolOEBVersionsURLs.get(0).contains("https://openebench.bsc.es/"));
    }

}
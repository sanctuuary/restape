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

/**
 * {@link ToolBenchmarkingAPIsTest} tests the methods in
 * {@link ToolBenchmarkingAPIs}.
 */
@SpringBootTest
class ToolBenchmarkingAPIsTest {

    private static final String TOOL_ID = "comet";

    /**
     * Test whether tool annotations from the OpenEBench API can be retrieved.
     */
    @Test
    void fetchToolAggregateFromOEB() {
        try {
            JSONArray openEBenchAggregateAnnotation = ToolBenchmarkingAPIs.fetchToolAggregateFromOEB(TOOL_ID);
            assertFalse(openEBenchAggregateAnnotation == null || openEBenchAggregateAnnotation.isEmpty());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Test whether tool versions URLs can be computed from the retrieved OpenEBench
     * API tool annotations.
     */
    @Test
    void getToolVersionsURLs() {
        try {
            JSONArray openEBenchAggregateAnnotation = ToolBenchmarkingAPIs.fetchToolAggregateFromOEB(TOOL_ID);
            List<String> toolOEBVersionsURLs = ToolBenchmarkingAPIs.getToolVersionsURLs(openEBenchAggregateAnnotation);

            assertFalse(toolOEBVersionsURLs == null || toolOEBVersionsURLs.isEmpty()
                    || !toolOEBVersionsURLs.get(0).contains("https://openebench.bsc.es/"));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            fail();
        }
    }

}

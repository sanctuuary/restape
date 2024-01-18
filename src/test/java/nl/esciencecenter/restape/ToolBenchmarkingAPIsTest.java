package nl.esciencecenter.restape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Arrays;
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

    @Test
    void testSwapOEBCallTool2Metric() {
        List<String> input = Arrays.asList(
                "http://example.com/api/tool/version1",
                "http://example.com/api/test/version2");

        List<String> expected = Arrays.asList(
                "http://example.com/api/metrics/version1",
                "http://example.com/api/test/version2");

        List<String> result = ToolBenchmarkingAPIs.swapOEBCallTool2Metric(input);

        assertEquals(expected, result, "The URLs should be correctly transformed");
    }

    /**
     * Test whether tool annotations from the OpenEBench API can be retrieved.
     */
    @Test
    void testFetchToolAggregateFromOEB() {
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
    void testGetToolVersionsURLs() {
        try {
            JSONArray openEBenchAggregateAnnotation = ToolBenchmarkingAPIs.fetchToolAggregateFromOEB(TOOL_ID);
            List<String> toolOEBVersionsURLs = ToolBenchmarkingAPIs.getToolVersionsURLs(openEBenchAggregateAnnotation);

            assertTrue(toolOEBVersionsURLs != null && !toolOEBVersionsURLs.isEmpty()
                    && toolOEBVersionsURLs.get(0).contains("https://openebench.bsc.es/"));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testFetchToolVersionsFromOEB() {
        try {
            JSONArray toolVersions = ToolBenchmarkingAPIs.fetchToolVersionsFromOEB(TOOL_ID, true);

            assertTrue(toolVersions != null && !toolVersions.isEmpty());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void test() {
        String toolID = "comet";
    }

}

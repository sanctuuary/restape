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
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nl.esciencecenter.models.benchmarks.OpenEBenchmark;

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

        List<String> result = ToolBenchmarkingAPIs.replaceTool2MetricInOEBCall(input);

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

    /**
     * Test for isOSIFromOEBMetrics method.
     * It verifies that the method correctly identifies and returns the OSI status
     * from the provided JSON object representing tool metrics.
     */
    @Test
    void testIsOSIFromOEBMetrics() {
        // Constructing the JSON structure inline for the tool metrics
        JSONObject mockToolMetrics = new JSONObject("""
                    {
                        "project": {
                            "license": {
                                "osi": true
                            }
                        }
                    }
                """);

        // Testing the isOSIFromOEBMetrics method and asserting that the OSI status is
        // true
        assertTrue(OpenEBenchmark.isOSIFromOEBMetrics(mockToolMetrics) == LicenseType.OSI_Approved,
                "The method should return true for OSI license");
    }

    @Test
    void testFetchToolMetricsPerVersionFromOEB() {
        try {
            List<JSONObject> allToolMetrics = ToolBenchmarkingAPIs.fetchToolMetricsPerVersionFromOEB(TOOL_ID);
            assertTrue(allToolMetrics != null && allToolMetrics.size() > 0);
            allToolMetrics.forEach(toolMetrics -> {
                try {
                    toolMetrics.get("project");
                } catch (JSONException e) {
                    fail("An exception occurred while checking for 'osi license' in 'project'. The JSON object could not be parsed correctly.");
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testFetchOEBMetricsForBiotoolsVersion() {
        try {
            JSONObject toolMetrics = ToolBenchmarkingAPIs.fetchOEBMetricsForBiotoolsVersion("shelx");
            OpenEBenchmark.isOSIFromOEBMetrics(toolMetrics);
        } catch (JSONException e) {
            fail("An exception occurred while checking for 'osi license' in 'project'. The JSON object could not be parsed correctly.");
        } catch (ClassCastException e) {
            fail("The 'project' field is not a JSONObject");
        } catch (IOException e) {
            fail("Failed to fetch metrics for bio.tools version for tool  + " + TOOL_ID);
        }
    }

}

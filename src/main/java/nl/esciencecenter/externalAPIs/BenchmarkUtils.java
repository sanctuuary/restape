package nl.esciencecenter.externalAPIs;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.esciencecenter.models.benchmarks.WorkflowStepBenchmark;
import nl.esciencecenter.restape.ToolBenchmarkingAPIs;

/**
 * Class {@code BenchmarkUtils} contains static methods helpful for computing
 * the benchmarks based on JSON objects.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BenchmarkUtils {

    /**
     * Benchmark for each JSONObject in the list whether it contains the specified
     * field or not. In practice it is used to check whether a tool has a specific
     * field annotated (documentation,license available, etc.).
     * 
     * @param biotoolsAnnotations List of JSONObjects containing the bio.tools
     *                            annotations according to biotoolsSchema.
     * @param fieldName
     * @return
     */
    protected static List<WorkflowStepBenchmark> benchmarkFieldAvailability(List<JSONObject> biotoolsAnnotations,
            String fieldName) {
        List<WorkflowStepBenchmark> biotoolsEntries = new ArrayList<>();

        biotoolsAnnotations.stream().forEach(toolAnnot -> {
            WorkflowStepBenchmark biotoolsEntryBenchmark = new WorkflowStepBenchmark();
            biotoolsEntryBenchmark.setDescription(toolAnnot.getString(ToolBenchmarkingAPIs.restAPEtoolID));
            if (!BenchmarkUtils.fieldInJson(toolAnnot, fieldName)) {
                biotoolsEntryBenchmark.setDesirabilityValue(0);
                biotoolsEntryBenchmark.setValue("not available");
            } else {
                biotoolsEntryBenchmark.setDesirabilityValue(1);
                biotoolsEntryBenchmark.setValue("available");
            }
            biotoolsEntries.add(biotoolsEntryBenchmark);
        });

        return biotoolsEntries;
    }

    /**
     * Benchmark for each JSONObject in the list whether it contains the expected
     * value for specified field or not. In practice it is used to check whether a
     * tool has a specific field annotated with the expected value (OS is Windows,
     * License is Apache 2.0, etc.).
     * 
     * @param biotoolsAnnotations - List of JSONObjects containing the bio.tools
     *                            annotations according to biotoolsSchema.
     * @param fieldName           - Name of the field to be checked.
     * @param expectedFieldValue          - Value of the field that is expected.
     * @return List of {@link WorkflowStepBenchmark} objects containing the
     *         benchmark value and desirability value according to whether it
     *         contains the expected value or not for each tool in the workflow.
     */
    protected static List<WorkflowStepBenchmark> benchmarkFieldValue(List<JSONObject> biotoolsAnnotations,
            String fieldName,
            String expectedFieldValue) {
        List<WorkflowStepBenchmark> biotoolsEntries = new ArrayList<>();

        biotoolsAnnotations.stream().forEach(toolAnnot -> {
            WorkflowStepBenchmark biotoolsEntryBenchmark = new WorkflowStepBenchmark();
            biotoolsEntryBenchmark.setDescription(toolAnnot.getString(ToolBenchmarkingAPIs.restAPEtoolID));
            if (!BenchmarkUtils.fieldValueInJson(toolAnnot, fieldName, expectedFieldValue)) {
                biotoolsEntryBenchmark.setDesirabilityValue(0);
                biotoolsEntryBenchmark.setValue("not supported");
            } else {
                biotoolsEntryBenchmark.setDesirabilityValue(1);
                biotoolsEntryBenchmark.setValue("supported");
            }
            biotoolsEntries.add(biotoolsEntryBenchmark);
        });

        return biotoolsEntries;
    }

    /**
     * Check whether the given field is in the given JSON object.
     * 
     * @param jsonObject - JSON object to check.
     * @param fieldName  - Name of the field to check.
     * @return True if the field is in the JSON object, false otherwise.
     */
    protected static boolean fieldInJson(JSONObject jsonObject, String fieldName) {
        try {
            if (jsonObject.get(fieldName) != null) {
                return true;
            }
        } catch (JSONException x) {
            return false;
        }
        return false;
    }

    /**
     * Check whether the specified field has the expected value in the given JSON.
     * 
     * @param jsonObject         - JSON object to check.
     * @param fieldName          - Name of the field to check.
     * @param expectedFieldValue - Expected value of the field.
     * @return True if the field has the expected value, false otherwise.
     */
    protected static boolean fieldValueInJson(JSONObject jsonObject, String fieldName, String expectedFieldValue) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return false;
        }
        try {
            JSONArray fieldValues = jsonObject.getJSONArray(fieldName);
            for (int i = 0; i < jsonObject.length(); i++) {
                if (fieldValues.getString(i).equals(expectedFieldValue)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

    /**
     * Calculate the desirability value for the given workflow, assuming that it
     * increases linearly with the number of tools that satisfy the benchmark.
     * 
     * @param count          number of tools that satisfy the benchmark
     * @param workflowLength length of the workflow
     * @return Desirability value for the given workflow.
     */
    static double normalDesirabilityDistribution(int count, int workflowLength) {
        return 1.0 * count / workflowLength;
    }

    /**
     * Calculate the desirability value for the given workflow, assuming that it is
     * desired that all tools satisfy the benchmark. Small desirability values are
     * assigned to workflows where only a subset of tools satisfy the benchmark.
     * 
     * @param count          number of tools that satisfy the benchmark
     * @param workflowLength length of the workflow
     * @return Desirability value for the given workflow.
     */
    static double strictDesirabilityDistribution(int count, int workflowLength) {
        if (count == workflowLength) {
            return 1;
        } else {
            return 1.0 * count / workflowLength / 10;
        }
    }

    static String ratioString(int count, int length) {
        return count + "/" + length;
    }
}

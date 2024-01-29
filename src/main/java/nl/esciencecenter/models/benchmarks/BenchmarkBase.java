package nl.esciencecenter.models.benchmarks;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
/**
 * Base information for a benchmark.
 */
public class BenchmarkBase {
    @NonNull
    private String benchmarkTitle;
    @NonNull
    private String benchmarkLongTitle;
    @NonNull
    private String benchmarkDescription;
    private String expectedField;
    private String expectedValue;

    public JSONObject getTitleJson() {
        JSONObject benchmarkJson = new JSONObject();
        benchmarkJson.put("benchmark_title", benchmarkTitle);
        benchmarkJson.put("benchmark_long_title", benchmarkLongTitle);
        benchmarkJson.put("benchmark_description", benchmarkDescription);
        return benchmarkJson;
    }

    static String ratioString(int count, int length) {
        return count + "/" + length;
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
}
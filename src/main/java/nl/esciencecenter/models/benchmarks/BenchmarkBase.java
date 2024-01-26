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

}
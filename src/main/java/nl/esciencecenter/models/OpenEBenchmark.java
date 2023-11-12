package nl.esciencecenter.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.esciencecenter.restape.ToolBenchmarkingAPIs;

@RequiredArgsConstructor
/**
 * Class representing the design-time benchmarks for a workflow obtained from
 * bio.tools API.
 */
public class OpenEBenchmark {

    @NonNull
    private BenchmarkBase benchmarkTitle;
    private String value;
    private double desirabilityValue;
    private List<WorkflowStepBench> workflow;

    public JSONObject getJson() {
        JSONObject benchmarkJson = this.benchmarkTitle.getTitleJson();

        benchmarkJson.put("value", value);
        benchmarkJson.put("desirability_value", desirabilityValue);
        benchmarkJson.put("workflow", workflow);
        return benchmarkJson;
    }
}

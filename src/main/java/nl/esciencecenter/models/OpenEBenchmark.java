package nl.esciencecenter.models;

import java.util.List;

import org.json.JSONObject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.uu.cs.ape.utils.APEUtils;

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
        String workflowString = "[";
        for (WorkflowStepBench step : workflow) {
            workflowString += step.toString() + ",";
        }
        workflowString = APEUtils.removeLastChar(workflowString) + "]";
        benchmarkJson.put("workflow", workflowString);
        return benchmarkJson;
    }
}

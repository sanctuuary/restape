package nl.esciencecenter.models.benchmarks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import com.oracle.truffle.regex.tregex.util.json.JsonObject;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import nl.esciencecenter.restape.ToolBenchmarkingAPIs;

@Data
@NoArgsConstructor
/**
 * Class representing the design-time benchmarks for a workflow step (tool).
 */
public class WorkflowStepBenchmark {
    /**
     * Description of the benchmark value used in the visualization.
     */
    @NonNull
    private String description;
    /**
     * Value of the benchmark for the workflow step (tool).
     */
    @NonNull
    private String value;
    /**
     * Desirability value (from 0 to 1.0) of the benchmark for the workflow step
     * (tool).
     */
    private double desirabilityValue;

    @Override
    public String toString() {
        return "{ description:" + description + ", value:" + value + ", desirability_value:"
                + desirabilityValue + "}";
    }

    /**
     * Generate a JSON object containing the tool benchmark information. The content can
     * be visualized using the Wokrkflomics web platform.
     * 
     * @return JSON object containing the benchmark information.
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("description", description);
        json.put("value", value);
        json.put("desirability_value", desirabilityValue);
        return json;
    }


}
package nl.esciencecenter.models;

import java.util.Collection;

import org.json.JSONObject;

import com.oracle.truffle.regex.tregex.util.json.JsonObject;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
/**
 * Class representing the design-time benchmarks for a workflow step.
 */
public class WorkflowStepBench {
    @NonNull
    private String description;
    @NonNull
    private String value;
    @NonNull
    private double desirabilityValue;

    @Override
    public String toString() {
        return "{ description:" + description + ", value:" + value + ", desirability_value:"
                + desirabilityValue + "}";
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("description", description);
        json.put("value", value);
        json.put("desirability_value", desirabilityValue);
        return json;
    }

}
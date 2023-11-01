package nl.esciencecenter.models;

import org.json.JSONObject;

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
    private String benchmarkDescription;
    @NonNull
    private String value;
    @NonNull
    private double desirabilityValue;

}
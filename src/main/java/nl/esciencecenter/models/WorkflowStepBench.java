package nl.esciencecenter.models;

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
    private double desirability_value;

}
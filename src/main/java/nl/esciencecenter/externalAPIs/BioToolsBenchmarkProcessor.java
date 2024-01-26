package nl.esciencecenter.externalAPIs;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.esciencecenter.models.benchmarks.Benchmark;
import nl.esciencecenter.models.benchmarks.BenchmarkBase;
import nl.esciencecenter.models.benchmarks.WorkflowStepBenchmark;
import nl.esciencecenter.restape.ToolBenchmarkingAPIs;


/**
 * Class {@link BioToolsBenchmarkProcessor} used to compute the design-time benchmarks for a workflow using the
 * bio.tools API. The bio.tools API is available at {@link https://bio.tools/api} and the schema of the JSON object returned by the API is  biotoolsSchema, available at {@link https://biotoolsschema.readthedocs.io/en/latest/biotoolsschema_elements.html}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BioToolsBenchmarkProcessor {

    /**
     * Benchmark for each tool in the workflow whether it is available in bio.tools
     * or not, by checking whether the JSON object returned by bio.tools API is
     * empty or not.
     * 
     * @param biotoolsAnnotations List of JSONObjects containing the bio.tools
     *                            annotations according to biotoolsSchema.
     * @param fieldName           Name of the field to be counted.
     * @return Benchmark object ({@link Benchmark}) containing the benchmark
     *         value and desirability value.
     */
    public static Benchmark benchmarkToolAvailability(List<JSONObject> biotoolsAnnotations,
            BenchmarkBase benchmarkInfo) {
        Benchmark benchmark = new Benchmark(benchmarkInfo);
        int workflowLength = biotoolsAnnotations.size();

        benchmark.setWorkflow(biotoolsAvailabilityOfTools(biotoolsAnnotations));
        int count = (int) benchmark.getWorkflow().stream().filter(tool -> tool.getDesirabilityValue() > 0).count();

        benchmark.setDesirabilityValue(BenchmarkUtils.strictDesirabilityDistribution(count, workflowLength));
        benchmark.setValue(BenchmarkUtils.ratioString(count, workflowLength));

        return benchmark;
    }

    /**
     * Benchmark for each tool in the workflow whether it contains a license
     * specified or not.
     * 
     * @param biotoolsAnnotations List of JSONObjects containing the bio.tools
     *                            annotations according to biotoolsSchema.
     * @param benchmarkInfo       Information about the benchmark that is being
     *                            computed.
     * @return
     */
    public static Benchmark benchmarkLicenseAvailability(List<JSONObject> biotoolsAnnotations,
            BenchmarkBase benchmarkInfo) {
        Benchmark benchmark = new Benchmark(benchmarkInfo);
        int workflowLength = biotoolsAnnotations.size();

        benchmark.setWorkflow(BenchmarkUtils.benchmarkFieldAvailability(biotoolsAnnotations,
                benchmarkInfo.getExpectedField()));
        int count = (int) benchmark.getWorkflow().stream().filter(tool -> tool.getDesirabilityValue() > 0).count();

        benchmark.setDesirabilityValue(BenchmarkUtils.strictDesirabilityDistribution(count, workflowLength));
        benchmark.setValue(BenchmarkUtils.ratioString(count, workflowLength));

        return benchmark;
    }

    /**
     * Benchmark for each tool in the workflow whether it supports the specified OS.
     * 
     * @param biotoolsAnnotations - List of JSONObjects containing the bio.tools
     *                            annotations according to biotoolsSchema.
     * @param benchmarkInfo       - Information about the OS benchmark that is being
     *                            computed.
     * @return Benchmark object ({@link Benchmark}) containing the benchmark
     *         value and desirability value.
     */
    public static Benchmark benchmarkOSSupport(List<JSONObject> biotoolsAnnotations,
            BenchmarkBase benchmarkInfo) {
        Benchmark benchmark = new Benchmark(benchmarkInfo);
        int workflowLength = biotoolsAnnotations.size();

        benchmark.setWorkflow(BenchmarkUtils.benchmarkFieldValue(biotoolsAnnotations,
                benchmarkInfo.getExpectedField(),
                benchmarkInfo.getExpectedValue()));
        int count = (int) benchmark.getWorkflow().stream().filter(tool -> tool.getDesirabilityValue() > 0).count();

        benchmark.setDesirabilityValue(BenchmarkUtils.normalDesirabilityDistribution(count, workflowLength));

        benchmark.setValue(BenchmarkUtils.ratioString(count, workflowLength));

        return benchmark;
    }

    /**
     * Benchmark for each tool in the workflow whether it is available in bio.tools
     * or not, by checking whether the JSON object returned by bio.tools API is
     * empty or not.
     * 
     * @param biotoolsAnnotations List of JSONObjects containing the bio.tools
     *                            annotations according to biotoolsSchema.
     * @return List of {@link WorkflowStepBenchmark} objects containing the
     *         benchmark
     *         value and desirability value for each tool in the workflow.
     */
    protected static List<WorkflowStepBenchmark> biotoolsAvailabilityOfTools(List<JSONObject> biotoolsAnnotations) {
        List<WorkflowStepBenchmark> biotoolsEntries = new ArrayList<>();

        biotoolsAnnotations.stream().forEach(toolAnnotation -> {
            WorkflowStepBenchmark biotoolsEntryBenchmark = new WorkflowStepBenchmark();
            biotoolsEntryBenchmark.setDescription(toolAnnotation.getString(ToolBenchmarkingAPIs.restAPEtoolID));
            if (biotoolsEmptyToolAnnotation(toolAnnotation)) {
                biotoolsEntryBenchmark.setDesirabilityValue(0);
                biotoolsEntryBenchmark.setValue("unavailable");
            } else {
                biotoolsEntryBenchmark.setDesirabilityValue(1);
                biotoolsEntryBenchmark.setValue("available");
            }
            biotoolsEntries.add(biotoolsEntryBenchmark);
        });

        return biotoolsEntries;
    }

    /**
     * Check whether the given JSONObject contains bio.tools annotations. According to biotoolsSchema, the JSON object must contain the field "biotoolsID" to be considered as a bio.tools annotation.
     * @param toolAnnotation - JSONObject that should contain the bio.tools annotation.
     * @return true if the JSONObject contains bio.tools annotation, false otherwise.
     */
    private static boolean biotoolsEmptyToolAnnotation(JSONObject toolAnnotation) {
        return !toolAnnotation.has("biotoolsID");
    }
}

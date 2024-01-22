package nl.esciencecenter.models.benchmarks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.esciencecenter.restape.LicenseType;
import nl.esciencecenter.restape.ToolBenchmarkingAPIs;
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

    public static OpenEBenchmark countLicenceOpenness(List<JSONObject> openEBenchAnnotations,
            BenchmarkBase benchmarkTitle) {
        OpenEBenchmark benchmark = new OpenEBenchmark(benchmarkTitle);
        int workflowLength = openEBenchAnnotations.size();

        benchmark.workflow = evaluateLicense(openEBenchAnnotations);
        int count = (int) benchmark.workflow.stream().filter(tool -> tool.getDesirabilityValue() > 0).count();

        benchmark.desirabilityValue = BenchmarkBase.strictDesirabilityDistribution(count, workflowLength);
        benchmark.value = BenchmarkBase.ratioString(count, workflowLength);

        return benchmark;
    }

    /**
     * Count the number of tools which have the given field name in the bio.tools
     * annotation JSON.
     * 
     * @param biotoolsAnnotations
     * @param fieldName
     * @return
     */
    private static List<WorkflowStepBench> evaluateLicense(List<JSONObject> biotoolsAnnotations) {
        List<WorkflowStepBench> biotoolsEntries = new ArrayList<>();

        biotoolsAnnotations.stream().forEach(toolAnnot -> {
            WorkflowStepBench biotoolsEntryBenchmark = new WorkflowStepBench();
            biotoolsEntryBenchmark.setDescription(toolAnnot.getString(ToolBenchmarkingAPIs.restAPEtoolID));
            LicenseType license = ToolBenchmarkingAPIs.isOSIFromOEBMetrics(toolAnnot);
            // set case for each license type
            switch (license) {
                case Unknown:
                    biotoolsEntryBenchmark.setDesirabilityValue(0);
                    biotoolsEntryBenchmark.setValue("unknown");
                    break;
                case Closed:
                    biotoolsEntryBenchmark.setDesirabilityValue(0.1);
                    biotoolsEntryBenchmark.setValue("closed");
                    break;
                case Open:
                    biotoolsEntryBenchmark.setDesirabilityValue(0.8);
                    biotoolsEntryBenchmark.setValue("open");
                    break;
                case OSI_Approved:
                    biotoolsEntryBenchmark.setDesirabilityValue(1);
                    biotoolsEntryBenchmark.setValue("osi");
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            biotoolsEntries.add(biotoolsEntryBenchmark);
        });

        return biotoolsEntries;
    }

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

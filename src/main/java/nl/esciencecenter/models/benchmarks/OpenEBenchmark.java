package nl.esciencecenter.models.benchmarks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.esciencecenter.restape.LicenseType;
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

    public static OpenEBenchmark countLicenceOpenness(List<JSONObject> openEBenchBiotoolsMetrics,
            BenchmarkBase benchmarkTitle) {
        OpenEBenchmark benchmark = new OpenEBenchmark(benchmarkTitle);
        int workflowLength = openEBenchBiotoolsMetrics.size();

        benchmark.workflow = evaluateLicenseBenchmark(openEBenchBiotoolsMetrics);
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
    private static List<WorkflowStepBench> evaluateLicenseBenchmark(List<JSONObject> biotoolsAnnotations) {
        List<WorkflowStepBench> biotoolsEntries = new ArrayList<>();

        biotoolsAnnotations.stream().forEach(toolAnnot -> {
            WorkflowStepBench biotoolsEntryBenchmark = new WorkflowStepBench();
            LicenseType license = ToolBenchmarkingAPIs.isOSIFromOEBMetrics(toolAnnot);
            // set case for each license type
            switch (license) {
                case Unknown:
                    biotoolsEntryBenchmark.setDesirabilityValue(0);
                    biotoolsEntryBenchmark.setValue("unknown");
                    biotoolsEntryBenchmark.setDescription("Unknown");
                    break;
                case Closed:
                    biotoolsEntryBenchmark.setDesirabilityValue(0.1);
                    biotoolsEntryBenchmark.setValue("closed");
                    biotoolsEntryBenchmark.setDescription("Closed");
                    break;
                case Open:
                    biotoolsEntryBenchmark.setDesirabilityValue(0.8);
                    biotoolsEntryBenchmark.setValue("open");
                    biotoolsEntryBenchmark.setDescription("Open");
                    break;
                case OSI_Approved:
                    biotoolsEntryBenchmark.setDesirabilityValue(1);
                    biotoolsEntryBenchmark.setValue("osi");
                    biotoolsEntryBenchmark.setDescription("OSI approved");
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            biotoolsEntries.add(biotoolsEntryBenchmark);
        });

        return biotoolsEntries;
    }

    public static OpenEBenchmark countCitationsBenchmark(List<JSONObject> openEBenchBiotoolsMetrics,
            BenchmarkBase benchmarkTitle) {
        OpenEBenchmark benchmark = new OpenEBenchmark(benchmarkTitle);

        benchmark.workflow = countCitationPerTool(openEBenchBiotoolsMetrics);
        List<Integer> counts = new ArrayList<>();
        benchmark.workflow.forEach(tool -> counts.add(Integer.parseInt(tool.getValue())));
        int median = findMedian(counts);

        benchmark.value = median + "";
        benchmark.desirabilityValue = computeCitationDesirability(median);

        return benchmark;
    }

    /**
     * Calculates the median of the given List of Integers.
     *
     * @param counts the List of Integer values
     * @return the median value as a double
     * @throws IllegalArgumentException if the input list is empty
     */
    private static int findMedian(List<Integer> counts) {
        if (counts == null || counts.isEmpty()) {
            throw new IllegalArgumentException("List of counts cannot be null or empty");
        }

        Collections.sort(counts);

        int size = counts.size();
        if (size % 2 == 1) {
            // If the size is odd, return the middle element
            return counts.get(size / 2);
        } else {
            // If the size is even, return the average of the two middle elements
            double leftMiddle = counts.get(size / 2 - 1);
            double rightMiddle = counts.get(size / 2);
            return (int) (leftMiddle + rightMiddle) / 2;
        }
    }

    private static List<WorkflowStepBench> countCitationPerTool(List<JSONObject> openEBenchBiotoolsMetrics) {
        List<WorkflowStepBench> biotoolsEntries = new ArrayList<>();
        openEBenchBiotoolsMetrics.stream().forEach(toolAnnot -> {
            WorkflowStepBench biotoolsEntryBenchmark = new WorkflowStepBench();
            int count = 0;
            try {
                JSONArray publications = toolAnnot.getJSONObject("project").getJSONArray("publications");
                for (int i = 0; i < publications.length(); i++) {
                    JSONObject publicationData = publications.getJSONObject(i);
                    count += publicationData.getJSONArray("entries").getJSONObject(0).getInt("cit_count");
                }
                // set case for each license type
                biotoolsEntryBenchmark.setDesirabilityValue(computeCitationDesirability(count));
                biotoolsEntryBenchmark.setValue(String.valueOf(count));
                biotoolsEntryBenchmark.setDescription(String.valueOf(count));
                biotoolsEntries.add(biotoolsEntryBenchmark);
            } catch (JSONException e) {
                e.printStackTrace();
                // set case for each license type
                biotoolsEntryBenchmark.setDesirabilityValue(0);
                biotoolsEntryBenchmark.setValue("Unknown");
                biotoolsEntryBenchmark.setDescription("Unknown");
                biotoolsEntries.add(biotoolsEntryBenchmark);
            }
        });

        return biotoolsEntries;
    }

    /*
     * Citation desirability is computed according to a predefined set of rules.
     * 
     */
    private static @NonNull double computeCitationDesirability(int count) {
        if (count == 0) {
            return 0;
        } else if (count < 10) {
            return 0.25;
        } else if (count < 30) {
            return 0.5;
        } else if (count < 50) {
            return 0.75;
        } else {
            return 1;
        }
    }

    public JSONObject getJson() {
        JSONObject benchmarkJson = this.benchmarkTitle.getTitleJson();

        benchmarkJson.put("value", value);
        benchmarkJson.put("desirability_value", desirabilityValue);
        JSONArray workflowJson = new JSONArray();
        for (WorkflowStepBench step : workflow) {
            workflowJson.put(step.toJSON());
        }
        benchmarkJson.put("steps", workflowJson);
        return benchmarkJson;
    }

}

package nl.esciencecenter.externalAPIs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import nl.esciencecenter.models.benchmarks.Benchmark;
import nl.esciencecenter.models.benchmarks.BenchmarkBase;
import nl.esciencecenter.models.benchmarks.WorkflowStepBenchmark;
import nl.esciencecenter.restape.LicenseType;

/**
 * Class {@link OpenEBenchBenchmarkProcessor} used to compute the design-time benchmarks for a workflow using the
 * OpenEBench API.<br><br>
 * The OpenEBench API does not provide a well structured API at the moment. The
 * current API interface is available at
 * {@link https://openebench.bsc.es/monitor}. Therefore, some of the methods in
 * this class are hardcoded to be able to utilize the current API interface.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenEBenchBenchmarkProcessor {

    /**
     * Benchmark for each tool in the workflow the license type according to the openness of the license. The licensed are characterized according to {@link LicenseType}.
     * 
     * @param openEBenchBiotoolsMetrics - List of JSONObjects containing the openEBench metrics for each tool in the workflow.
     * @param benchmarkTitle - Information about the benchmark that is being computed.
     * @return Benchmark object ({@link Benchmark}) containing the benchmark value and desirability value.
     */
    public static Benchmark benchmarkLicenses(List<JSONObject> openEBenchBiotoolsMetrics,
            BenchmarkBase benchmarkTitle) {
        Benchmark benchmark = new Benchmark(benchmarkTitle);
        int workflowLength = openEBenchBiotoolsMetrics.size();

        benchmark.setWorkflow(evaluateLicenseBenchmark(openEBenchBiotoolsMetrics));
        int count = (int) benchmark.getWorkflow().stream().filter(tool -> tool.getDesirabilityValue() > 0).count();

        benchmark.setDesirabilityValue(BenchmarkUtils.strictDesirabilityDistribution(count, workflowLength));
        benchmark.setValue(BenchmarkUtils.ratioString(count, workflowLength));

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
    private static List<WorkflowStepBenchmark> evaluateLicenseBenchmark(List<JSONObject> biotoolsAnnotations) {
        List<WorkflowStepBenchmark> biotoolsEntries = new ArrayList<>();

        biotoolsAnnotations.stream().forEach(toolAnnot -> {
            WorkflowStepBenchmark biotoolsEntryBenchmark = new WorkflowStepBenchmark();
            LicenseType license = isOSIFromOEBMetrics(toolAnnot);
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

    public static Benchmark countCitationsBenchmark(List<JSONObject> openEBenchBiotoolsMetrics,
            BenchmarkBase benchmarkTitle) {
        Benchmark benchmark = new Benchmark(benchmarkTitle);

        benchmark.setWorkflow(countCitationPerTool(openEBenchBiotoolsMetrics));
        List<Integer> counts = new ArrayList<>();
        benchmark.getWorkflow().forEach(tool -> counts.add(Integer.parseInt(tool.getValue())));
        int median = findMedian(counts);

        benchmark.setValue(median + "");
        benchmark.setDesirabilityValue(computeCitationDesirability(median));

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

    private static List<WorkflowStepBenchmark> countCitationPerTool(List<JSONObject> openEBenchBiotoolsMetrics) {
        List<WorkflowStepBenchmark> biotoolsEntries = new ArrayList<>();
        openEBenchBiotoolsMetrics.stream().forEach(toolAnnot -> {
            WorkflowStepBenchmark biotoolsEntryBenchmark = new WorkflowStepBenchmark();
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
        } else if (count < 100) {
            return 0.5;
        } else if (count < 200) {
            return 0.75;
        } else {
            return 1;
        }
    }

    /**
    * Parse the JSON object returned by OpenEBench API describing the tool metrics
    * and return whether the tool has an OSI approved license.
    * 
    * @param toolMetrics - JSON object returned by OpenEBench API describing the
    *                    tool metrics.
    * @return true if the tool has an OSI approved license, false otherwise.
    */
   public static LicenseType isOSIFromOEBMetrics(JSONObject toolMetrics) throws JSONException {
    JSONObject licenseJson;
    try {
       licenseJson = toolMetrics.getJSONObject("project").getJSONObject("license");
    } catch (JSONException e) {
       return LicenseType.Unknown;
    }

    boolean isOSI = licenseJson.getBoolean("osi");
    if (isOSI) {
       return LicenseType.OSI_Approved;
    } else if (licenseJson.getBoolean("open_source")) {
       return LicenseType.Open;
    } else {
       return LicenseType.Closed;
    }
 }

}

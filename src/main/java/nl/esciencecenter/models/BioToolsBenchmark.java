package nl.esciencecenter.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.esciencecenter.restape.ToolBenchmarkingAPIs;
import nl.uu.cs.ape.utils.APEUtils;

@RequiredArgsConstructor
/**
 * Class representing the design-time benchmarks for a workflow obtained from
 * bio.tools API.
 */
public class BioToolsBenchmark {

    @NonNull
    private BenchmarkBase benchmarkTitle;
    private String value;
    private double desirabilityValue;
    private List<WorkflowStepBench> workflow;

    private static boolean emptyToolAnnotation(JSONObject toolAnnot) {
        return !toolAnnot.has("biotoolsID");
    }

    private static String ratioString(int count, int length) {
        return count + "/" + length;
    }

    private static double normalDistribution(int count, int workflowLength) {
        return 1.0 * count / workflowLength;
    }

    private static double strictDistribution(int count, int workflowLength) {
        if (count == workflowLength) {
            return 1;
        } else {
            return 1.0 * count / workflowLength / 10;
        }
    }

    public static BioToolsBenchmark countEntries(List<JSONObject> biotoolsAnnotations, BenchmarkBase benchmarkTitle) {
        BioToolsBenchmark benchmark = new BioToolsBenchmark(benchmarkTitle);
        int workflowLength = biotoolsAnnotations.size();

        benchmark.workflow = countEntries(biotoolsAnnotations);
        int count = (int) benchmark.workflow.stream().filter(tool -> tool.getDesirabilityValue() > 0).count();

        benchmark.desirabilityValue = strictDistribution(count, workflowLength);
        benchmark.value = ratioString(count, workflowLength);

        return benchmark;
    }

    public static BioToolsBenchmark countLicencedEntries(List<JSONObject> biotoolsAnnotations,
            BenchmarkBase benchmarkTitle) {
        BioToolsBenchmark benchmark = new BioToolsBenchmark(benchmarkTitle);
        int workflowLength = biotoolsAnnotations.size();

        benchmark.workflow = countField(biotoolsAnnotations, benchmarkTitle.getExpectedField());
        int count = (int) benchmark.workflow.stream().filter(tool -> tool.getDesirabilityValue() > 0).count();

        benchmark.desirabilityValue = strictDistribution(count, workflowLength);
        benchmark.value = ratioString(count, workflowLength);

        return benchmark;
    }

    public static BioToolsBenchmark countOSEntries(List<JSONObject> biotoolsAnnotations,
            BenchmarkBase benchmarkTitle) {
        BioToolsBenchmark benchmark = new BioToolsBenchmark(benchmarkTitle);
        int workflowLength = biotoolsAnnotations.size();

        benchmark.workflow = countArrayFieldVal(biotoolsAnnotations, benchmarkTitle.getExpectedField(),
                benchmarkTitle.getExpectedValue());
        int count = (int) benchmark.workflow.stream().filter(tool -> tool.getDesirabilityValue() > 0).count();

        benchmark.desirabilityValue = normalDistribution(count, workflowLength);

        benchmark.value = ratioString(count, workflowLength);

        return benchmark;
    }

    /**
     * Count entries that exist in the bio.tools annotation JSON.
     * 
     * @param biotoolsAnnotations
     * @return
     */
    private static List<WorkflowStepBench> countEntries(List<JSONObject> biotoolsAnnotations) {
        List<WorkflowStepBench> biotoolsEntries = new ArrayList<>();

        biotoolsAnnotations.stream().forEach(toolAnnot -> {
            WorkflowStepBench biotoolsEntryBenchmark = new WorkflowStepBench();
            biotoolsEntryBenchmark.setDescription(toolAnnot.getString(ToolBenchmarkingAPIs.restAPEtoolID));
            if (emptyToolAnnotation(toolAnnot)) {
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
     * Count the number of tools which have the given field name in the bio.tools
     * annotation JSON.
     * 
     * @param biotoolsAnnotations
     * @param fieldName
     * @return
     */
    private static List<WorkflowStepBench> countField(List<JSONObject> biotoolsAnnotations, String fieldName) {
        List<WorkflowStepBench> biotoolsEntries = new ArrayList<>();

        biotoolsAnnotations.stream().forEach(toolAnnot -> {
            WorkflowStepBench biotoolsEntryBenchmark = new WorkflowStepBench();
            biotoolsEntryBenchmark.setDescription(toolAnnot.getString(ToolBenchmarkingAPIs.restAPEtoolID));
            if (!inAvailable(toolAnnot, fieldName)) {
                biotoolsEntryBenchmark.setDesirabilityValue(0);
                biotoolsEntryBenchmark.setValue("not available");
            } else {
                biotoolsEntryBenchmark.setDesirabilityValue(1);
                biotoolsEntryBenchmark.setValue("available");
            }
            biotoolsEntries.add(biotoolsEntryBenchmark);
        });

        return biotoolsEntries;
    }

    /**
     * Count the number of tools which have the given field value in the array for
     * the given field name.
     * 
     * @param biotoolsAnnotations
     * @param fieldName
     * @param fieldValue
     * @return
     */
    private static List<WorkflowStepBench> countArrayFieldVal(List<JSONObject> biotoolsAnnotations, String fieldName,
            String fieldValue) {
        List<WorkflowStepBench> biotoolsEntries = new ArrayList<>();

        biotoolsAnnotations.stream().forEach(toolAnnot -> {
            WorkflowStepBench biotoolsEntryBenchmark = new WorkflowStepBench();
            biotoolsEntryBenchmark.setDescription(toolAnnot.getString(ToolBenchmarkingAPIs.restAPEtoolID));
            if (!inStringArray(toolAnnot, fieldName, fieldValue)) {
                biotoolsEntryBenchmark.setDesirabilityValue(0);
                biotoolsEntryBenchmark.setValue("not supported");
            } else {
                biotoolsEntryBenchmark.setDesirabilityValue(1);
                biotoolsEntryBenchmark.setValue("supported");
            }
            biotoolsEntries.add(biotoolsEntryBenchmark);
        });

        return biotoolsEntries;
    }

    /**
     * Count the number of tools which have the given field name in the biotools
     * annotation JSON.
     * 
     * @param biotoolsAnnotations
     * @param fieldName
     * @return
     */
    private static int countExistanceOfFields(List<JSONObject> biotoolsAnnotations, String fieldName) {
        // for each tool in the workflow, get the biotools metadata from bio.tool API
        long count = biotoolsAnnotations.stream().filter(tool -> inAvailable(tool, fieldName)).count();
        return (int) count;
    }

    /**
     * Check whether the given value is in the set of given values.
     */
    private static boolean inStringArray(JSONObject biotoolJson, String fieldName, String expectedFieldValue) {
        if (biotoolJson == null || biotoolJson.isEmpty()) {
            return false;
        }
        try {
            JSONArray fieldValues = biotoolJson.getJSONArray(fieldName);
            for (int i = 0; i < biotoolJson.length(); i++) {
                if (fieldValues.getString(i).equals(expectedFieldValue)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

    private static boolean inAvailable(JSONObject biotoolJson, String fieldName) {
        try {
            if (biotoolJson.get(fieldName) != null) {
                return true;
            }
        } catch (JSONException x) {
            return false;
        }
        return false;
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

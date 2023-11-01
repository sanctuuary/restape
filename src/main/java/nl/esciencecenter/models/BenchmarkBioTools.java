package nl.esciencecenter.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BenchmarkBioTools {

    @NonNull
    private Benchmark benchmarkTitle;
    private String value;
    private String desirabilityValue;
    private JSONArray workflow;

    public static BenchmarkBioTools countEntries(List<JSONObject> biotoolsAnnotations, Benchmark benchmarkTitle) {
        BenchmarkBioTools benchmark = new BenchmarkBioTools(benchmarkTitle);
        int workflowLength = biotoolsAnnotations.size();

        List<JSONObject> biotoolsEntries = computeEntries(biotoolsAnnotations);
        int count = (int) biotoolsEntries.stream().filter(tool -> tool.getLong("desirability_value") > 0).count();

        double desirability = strictDistribution(count, workflowLength);

        benchmark.value = ratioString(count, workflowLength);
        benchmark.desirabilityValue = Double.toString(desirability);
        benchmark.workflow = new JSONArray(biotoolsEntries);
        return benchmark;
    }

    private static List<JSONObject> computeEntries(List<JSONObject> biotoolsAnnotations) {
        List<JSONObject> biotoolsEntries = new ArrayList<>();

        biotoolsAnnotations.stream().forEach(toolAnnot -> {
            JSONObject biotoolsEntry = new JSONObject();
            biotoolsEntry.put("description", toolAnnot.getString("toolID"));
            if (emptyToolAnnotation(toolAnnot)) {
                biotoolsEntry.put("desirability_value", 0);
                biotoolsEntry.put("value", "unavailable");
            } else {
                biotoolsEntry.put("desirability_value", 1);
                biotoolsEntry.put("value", "available");
            }

            biotoolsEntries.add(biotoolsEntry);
        });

        return biotoolsEntries;
    }

    private static boolean emptyToolAnnotation(JSONObject toolAnnot) {
        return !toolAnnot.has("biotoolsID");
    }

    private static String ratioString(int count, int length) {
        return count + "/" + length;
    }

    public static BenchmarkBioTools countLinuxEntries(List<JSONObject> biotoolsAnnotations, Benchmark benchmarkTitle) {
        BenchmarkBioTools benchmark = new BenchmarkBioTools(benchmarkTitle);
        int workflowLength = biotoolsAnnotations.size();

        int count = countArrayFields(biotoolsAnnotations, "operatingSystem", "Linux");
        double desirability = normalDistribution(count, workflowLength);

        benchmark.value = ratioString(count, workflowLength);
        benchmark.desirabilityValue = Double.toString(desirability);

        return benchmark;
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

    public static BenchmarkBioTools countMacOSEntries(List<JSONObject> biotoolsAnnotations, Benchmark benchmarkTitle) {
        BenchmarkBioTools benchmark = new BenchmarkBioTools(benchmarkTitle);
        int workflowLength = biotoolsAnnotations.size();

        int count = countArrayFields(biotoolsAnnotations, "operatingSystem", "Mac");
        double desirability = normalDistribution(count, workflowLength);

        benchmark.value = ratioString(count, workflowLength);
        benchmark.desirabilityValue = Double.toString(desirability);

        return benchmark;
    }

    public static BenchmarkBioTools countWindowsEntries(List<JSONObject> biotoolsAnnotations,
            Benchmark benchmarkTitle) {
        BenchmarkBioTools benchmark = new BenchmarkBioTools(benchmarkTitle);
        int workflowLength = biotoolsAnnotations.size();

        int count = countArrayFields(biotoolsAnnotations, "operatingSystem", "Windows");
        double desirability = normalDistribution(count, workflowLength);

        benchmark.value = ratioString(count, workflowLength);
        benchmark.desirabilityValue = Double.toString(desirability);

        return benchmark;
    }

    public static BenchmarkBioTools countLicencedEntries(List<JSONObject> biotoolsAnnotations,
            Benchmark benchmarkTitle) {
        BenchmarkBioTools benchmark = new BenchmarkBioTools(benchmarkTitle);
        int workflowLength = biotoolsAnnotations.size();

        int count = countExistanceOfFields(biotoolsAnnotations, "license");
        double desirability = strictDistribution(count, workflowLength);

        benchmark.value = ratioString(count, workflowLength);
        benchmark.desirabilityValue = Double.toString(desirability);

        return benchmark;
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
    private static int countArrayFields(List<JSONObject> biotoolsAnnotations, String fieldName, String fieldValue) {

        // for each tool in the workflow, get the biotools metadata from bio.tool API
        long count = biotoolsAnnotations.stream().filter(tool -> inStringArray(tool, fieldName, fieldValue)).count();
        return (int) count;
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
        benchmarkJson.put("workflow", workflow);
        return benchmarkJson;
    }
}

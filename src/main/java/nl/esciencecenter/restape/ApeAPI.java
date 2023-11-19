package nl.esciencecenter.restape;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.esciencecenter.models.BenchmarkBase;
import nl.esciencecenter.models.BioToolsBenchmark;
import nl.uu.cs.ape.APE;
import nl.uu.cs.ape.configuration.APECoreConfig;
import nl.uu.cs.ape.configuration.APERunConfig;
import nl.uu.cs.ape.constraints.ConstraintTemplate;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllPredicates;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.solver.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.solver.solutionStructure.SolutionsList;
import nl.uu.cs.ape.utils.APEUtils;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.oracle.truffle.regex.tregex.util.json.JsonObject;

import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.engine.Format;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApeAPI {

    /**
     * Setups an instance of the APE engine.
     * 
     * @param configFileURL - URL of the configuration file
     * @return - instance of the APE engine
     * @throws IOException                  - if the configuration file cannot be
     *                                      found
     * @throws OWLOntologyCreationException - if the ontology cannot be created
     */
    public static APE setupApe(String configFileURL) throws IOException, OWLOntologyCreationException {

        APECoreConfig apeConfiguration = new APECoreConfig(configFileURL);
        return new APE(apeConfiguration);
    }

    /**
     * Get the data (types and formats) available in the domain, structured in
     * subset relation.
     * 
     * @param configFileURL
     * @return
     * @throws OWLOntologyCreationException
     * @throws IOException
     */
    public static JSONArray getData(String configFileURL) throws OWLOntologyCreationException, IOException {
        APE apeFramework = setupApe(configFileURL);
        return generateTypeJSON(apeFramework.getDomainSetup().getAllTypes());
    }

    /**
     * Get the tools available in the domain, structured in subset relation.
     * 
     * @param configFileURL - URL of the configuration file
     * @return - json object with the tools
     * @throws OWLOntologyCreationException - if the ontology cannot be created
     * @throws IOException                  - if the configuration file cannot be
     *                                      found
     */
    public static JSONObject getTools(String configFileURL) throws OWLOntologyCreationException, IOException {
        APE apeFramework = setupApe(configFileURL);
        return generateToolJSON(apeFramework.getDomainSetup().getAllModules());
    }

    /**
     * Generate the json object comprising all the types and formats available in
     * the domain.
     * 
     * @param allTypes - all data available in the domain
     * @return - json object with the data
     */
    private static JSONArray generateTypeJSON(AllTypes allTypes) {
        JSONArray arrayTypes = new JSONArray();
        for (TaxonomyPredicate type : allTypes.getDataTaxonomyDimensions()) {
            arrayTypes.put(getPredicates(type, allTypes));
        }
        return arrayTypes;
    }

    /**
     * Generate the json object comprising all the tools available in the domain.
     * 
     * @param allTypes - all tools available in the domain
     * @return - json object with the tools
     */
    private static JSONObject generateToolJSON(AllModules allModules) {
        return getPredicates(allModules.getRootModule(), allModules);
    }

    /**
     * Get the json object representing predicates.
     * 
     * @param currType - current predicate
     * @param allPred  - all predicates
     * @return - json object representing the predicates
     */
    private static JSONObject getPredicates(TaxonomyPredicate currType, AllPredicates allPred) {
        JSONObject objType = new JSONObject();
        JSONArray arrayTypes = new JSONArray();
        for (TaxonomyPredicate newType : APEUtils.safe(currType.getSubPredicates())) {
            arrayTypes.put(getPredicates(newType, allPred));
        }
        objType.put("id", currType.getPredicateID());
        objType.put("label", currType.getPredicateLabel());
        objType.put("root", currType.getRootNodeID());
        if (arrayTypes.length() > 0) {
            objType.put("subsets", arrayTypes);
        }
        return objType;
    }

    /**
     * Return the json object representing all available constraint templates within
     * APE.
     * 
     * @return - json object with all available constraint templates
     * @throws IOException                  - if the configuration file cannot be
     *                                      found
     * @throws OWLOntologyCreationException - if the ontology cannot be created
     */
    public static JSONArray getConstraints(String configFileURL) throws OWLOntologyCreationException, IOException {
        APE apeFramework = setupApe(configFileURL);
        Collection<ConstraintTemplate> constraints = apeFramework.getConstraintTemplates();
        JSONArray arrayConstraints = new JSONArray();
        constraints.forEach(constraint -> arrayConstraints.put(constraint.toJSON()));

        return arrayConstraints;
    }

    /**
     * Execute the synthesis of workflows using the APE framework.
     * 
     * @param configJson - configuration of the synthesis run
     * @param benchmark  - boolean to indicate if the workflows should be
     *                   benchmarked
     * @return - JSONArray with the metadata results of the synthesis, each element
     *         describes a workflow solution (name, length, runID, path to a CWL
     *         file, etc.).
     * @throws OWLOntologyCreationException
     * @throws IOException
     */
    public static JSONArray runSynthesis(JSONObject configJson, boolean benchmark)
            throws OWLOntologyCreationException, IOException {

        // Define the synthesis run ID
        String runID = RestApeUtils.generateUniqueString(configJson.toString());

        SolutionsList candidateSolutions = executeSynthesis(configJson, runID);

        // Write solutions (as CWL files and figures) to the file system.
        APE.writeCWLWorkflows(candidateSolutions);
        APE.writeTavernaDesignGraphs(candidateSolutions, Format.PNG);

        // benchmark workflows if required
        if (benchmark) {
            ToolBenchmarkingAPIs.computeBenchmarks(candidateSolutions, runID);
        }

        return workflowMetadataToJson(candidateSolutions, runID, benchmark);
    }

    /**
     * 
     * Method to execute the synthesis of workflows using the APE framework.
     * 
     * @param configJson - configuration of the synthesis run
     * @runID - ID of the synthesis run
     * @return - SolutionsList object, which contains the results of the synthesis
     *         as well as information about the synthesis run.
     * @throws IOException
     * @throws OWLOntologyCreationException
     */
    private static SolutionsList executeSynthesis(JSONObject configJson, String runID)
            throws OWLOntologyCreationException, IOException {

        String solutionPath = RestApeUtils.createDirectory(runID);

        APE apeFramework = null;

        // set up the APE framework
        apeFramework = new APE(configJson);

        APERunConfig runConfig = new APERunConfig(configJson, apeFramework.getDomainSetup());

        runConfig.setSolutionPath(solutionPath);
        int maxSol = runConfig.getMaxNoSolutions();
        runConfig.setNoCWL(maxSol);
        runConfig.setNoGraphs(maxSol);
        runConfig.setDebugMode(true);
        // run the synthesis and retrieve the solutions
        return apeFramework.runSynthesis(runConfig);
    }

    /**
     * Generate metadata in JSON format that describes the candidate workflows.
     * 
     * @param candidateSolutions - SolutionsList object, which contains the results
     *                           of the synthesis as well as information about the
     *                           synthesis run.
     * @runID - ID of the synthesis run
     * @return - JSONArray with the results of the synthesis, each element describes
     *         a workflow solution (name, length, runID, path to a CWL file, etc.).
     */
    private static JSONArray workflowMetadataToJson(SolutionsList candidateSolutions, String runID, boolean benchmark) {
        JSONArray generatedSolutionsJson = new JSONArray();

        if (candidateSolutions.isEmpty()) {
            return new JSONArray();
        } else {
            // Generate objects that return the solutions in JSON format
            int noSolutions = candidateSolutions.getNumberOfSolutions();
            for (int i = 0; i < noSolutions; i++) {
                SolutionWorkflow sol = candidateSolutions.get(i);
                JSONObject solutionJson = new JSONObject();
                solutionJson.put("name", sol.getFileName());
                solutionJson.put("workflow_length", sol.getSolutionLength());
                solutionJson.put("run_id", runID);
                // Add reference to the generated cwl file and figure
                solutionJson.put("cwl_name", sol.getFileName() + ".cwl");
                solutionJson.put("figure_name", sol.getFileName() + ".png");
                if (benchmark) {
                    solutionJson.put("benchmark_file", sol.getFileName() + ".json");
                }
                generatedSolutionsJson.put(solutionJson);
            }
            return generatedSolutionsJson;
        }
    }

}

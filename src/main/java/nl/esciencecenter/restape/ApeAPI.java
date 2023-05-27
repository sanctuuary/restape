package nl.esciencecenter.restape;

import java.io.IOException;
import java.util.Collection;

import nl.uu.cs.ape.APE;
import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.configuration.APECoreConfig;
import nl.uu.cs.ape.configuration.APERunConfig;
import nl.uu.cs.ape.constraints.ConstraintTemplate;
import nl.uu.cs.ape.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.core.solutionStructure.SolutionsList;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllPredicates;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.utils.APEUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import guru.nidi.graphviz.attribute.Rank.RankDir;

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
        constraints.forEach(constraint -> {
            arrayConstraints.put(constraint.toJSON());
        });

        return arrayConstraints;
    }

    /**
     * 
     * Method to run the synthesis of workflows using the APE framework.
     * 
     * @param configJson - configuration of the synthesis run
     * @return - JSONArray with the results of the synthesis, each element describes
     *         a workflow
     */
    public static JSONArray runSynthesis(JSONObject configJson) {
        JSONArray generatedSolutions = new JSONArray();
        APE apeFramework = null;
        try {

            // set up the APE framework
            apeFramework = new APE(configJson);

        } catch (APEConfigException | JSONException | IOException | OWLOntologyCreationException e) {
            return new JSONArray("[\"Error in setting up the APE framework:" +  e.getMessage().replace("\n", " ") + "\"]");
        }

        String runID = RestApeUtils.generateUniqueString(configJson.toString());
        String solutionPath = RestApeUtils.createDirectory(runID);
        System.out.println("Solution path: " + solutionPath);

        SolutionsList solutions;
        try {

            APERunConfig runConfig = new APERunConfig(configJson, apeFramework.getDomainSetup());

            runConfig.setSolutionPath(solutionPath);
            int solutionsNo = 10;
            runConfig.setMaxNoSolutions(solutionsNo);
            runConfig.setNoGraphs(solutionsNo);
            runConfig.setNoCWL(solutionsNo);
            // run the synthesis and retrieve the solutions
            solutions = apeFramework.runSynthesis(runConfig);

        } catch (APEConfigException e) {
            return new JSONArray("[\"Error in synthesis execution. APE configuration error:" +  e.getMessage().replace("\n", " ") + "\"]");
                } catch (JSONException e) {
            return new JSONArray("[\"Error in synthesis execution. Bad JSON formatting (APE configuration or constriants JSON). "
                                    +  e.getMessage().replace("\n", " ") + "\"]");
        } catch (IOException e) {
            return new JSONArray("[\"Error in synthesis execution." +  e.getMessage().replace("\n", " ") + "\"]");
        }

        /*
         * Writing solutions to the specified file in human readable format
         */
        if (solutions.isEmpty()) {
            return new JSONArray("The given problem is UNSAT");
        } else {
                // Write solutions to the file system.
                APE.writeDataFlowGraphs(solutions, RankDir.TOP_TO_BOTTOM);
                APE.writeCWLWorkflows(solutions);

                // Generate objects that return the solutions in JSON format
                int noSolutions = solutions.getNumberOfSolutions();
                for (int i = 0; i < noSolutions; i++) {
                    SolutionWorkflow sol = solutions.get(i);
                    JSONObject solJson = new JSONObject();
                    solJson.put("name", sol.getFileName());
                    solJson.put("workflow_length", sol.getSolutionLength());
                    solJson.put("cwl_name", sol.getFileName() + ".cwl");
                    solJson.put("figure_name", sol.getFileName() + ".png");
                    solJson.put("run_id", runID);

                    generatedSolutions.put(solJson);
                }
                return generatedSolutions;
        }
    }

}

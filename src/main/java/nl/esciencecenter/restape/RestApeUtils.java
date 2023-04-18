package nl.esciencecenter.restape;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import nl.uu.cs.ape.APE;
import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.configuration.APECoreConfig;
import nl.uu.cs.ape.configuration.APERunConfig;
import nl.uu.cs.ape.core.solutionStructure.SolutionsList;
import nl.uu.cs.ape.io.APEFiles;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllPredicates;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.utils.APEUtils;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import guru.nidi.graphviz.attribute.Rank.RankDir;

public class RestApeUtils {

    private static String allSolutionsDirName = "apeOutputs";

    /** Private constructor to hide the implicit constructor. */
    private RestApeUtils() {
    }

    /**
     * Setups an instance of the APE engine.
     * 
     * @param configFileURL
     * @return
     * @throws IOException
     * @throws OWLOntologyCreationException
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
     * @param configFileURL
     * @return
     * @throws OWLOntologyCreationException
     * @throws IOException
     */
    public static JSONObject getTools(String configFileURL) throws OWLOntologyCreationException, IOException {
        APE apeFramework = setupApe(configFileURL);
        return generateToolJSON(apeFramework.getDomainSetup().getAllModules());
    }

    private static JSONArray generateTypeJSON(AllTypes allTypes) {
        JSONArray arrayTypes = new JSONArray();
        for (TaxonomyPredicate type : allTypes.getDataTaxonomyDimensions()) {
            arrayTypes.put(getPredicates(type, allTypes));
        }
        return arrayTypes;
    }

    private static JSONObject generateToolJSON(AllModules allModules) {
        return getPredicates(allModules.getRootModule(), allModules);
    }

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
     * Method to run the synthesis of workflows using the APE framework.
     * 
     * @param configJson - configuration of the synthesis run
     * @param userId     - user id
     * @return - JSON object with the results of the synthesis
     */
    public static String runSynthesis(JSONObject configJson, String userId) {
        APE apeFramework = null;
        int solutionsNo = 10;
        try {

            // set up the APE framework
            apeFramework = new APE(configJson);

        } catch (APEConfigException | JSONException | IOException | OWLOntologyCreationException e) {
            return new JSONObject().put("error", "Error in setting up the APE framework:" + e.getMessage()).toString();
        }

        String currTokenString = generateStringToken(userId);
        String solutionPath = createDirectory(currTokenString);

        SolutionsList solutions;
        try {

            APERunConfig runConfig = new APERunConfig(configJson, apeFramework.getDomainSetup());

            runConfig.setSolutionPath(solutionPath);

            if (solutionsNo > 0) {
                runConfig.setMaxNoSolutions(solutionsNo);
                runConfig.setNoGraphs(solutionsNo);
                runConfig.setNoCWL(solutionsNo);
            }
            // run the synthesis and retrieve the solutions
            solutions = apeFramework.runSynthesis(runConfig);

        } catch (APEConfigException e) {
            return new JSONObject()
                    .put("error", "Error in synthesis execution. APE configuration error:" + e.getMessage())
                    .toString();
        } catch (JSONException e) {
            return new JSONObject()
                    .put("error",
                            "Error in synthesis execution. Bad JSON formatting (APE configuration or constriants JSON). "
                                    + e.getMessage())
                    .toString();
        } catch (IOException e) {
            return new JSONObject().put("error", "Error in synthesis execution." + e.getMessage()).toString();
        }

        /*
         * Writing solutions to the specified file in human readable format
         */
        if (solutions.isEmpty()) {
            return new JSONObject().put("flag", "UNSAT").toString();
        } else {
            try {
                APE.writeSolutionToFile(solutions);
                APE.writeDataFlowGraphs(solutions, RankDir.TOP_TO_BOTTOM);
                // APE.writeControlFlowGraphs(solutions, RankDir.LEFT_TO_RIGHT);
                APE.writeExecutableWorkflows(solutions);
                APE.writeCWLWorkflows(solutions);
                APE.writeExecutableCWLWorkflows(solutions, apeFramework.getConfig());

                return new JSONObject().put("flag", "SAT").toString();
            } catch (IOException e) {
                return new JSONObject()
                        .put("error", "Error in writing the solutions. to the file system." + e.getMessage())
                        .toString();
            }
        }
    }

    /**
     * Create a directory in the file system.
     * 
     * @param dirName - name of the directory
     * @return true if the directory was created, false otherwise.
     */
    private static String createDirectory(String dirName) {
        String desiredPath = getSolutionDirectory(dirName);
        File dir = new File(desiredPath);
        if (!dir.exists()) {
            return dir.mkdir() ? desiredPath : "";

        }
        return desiredPath;
    }

    /**
     * Get the path to the directory where the solutions will be stored.
     * 
     * @param dirName - name of the directory
     * @return Path to the directory.
     */
    private static String getSolutionDirectory(String dirName) {
        String currentPathStr = System.getProperty("user.dir");
        Path desiredPath = Paths.get(currentPathStr, allSolutionsDirName, dirName);
        return desiredPath.toString();
    }

    /**
     * Generate a random string token for the user, by concatenating a random
     * string, user id and the timestamp.
     * 
     * @param userID - user id
     * @return Random string token.
     */
    private static String generateStringToken(String userID) {
        return generateRandomString(10) + userID + System.currentTimeMillis();
    }

    /**
     * Generate a random string of a given length.
     * 
     * @param length - length of the string
     * @return Random string
     */
    private static String generateRandomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }
}

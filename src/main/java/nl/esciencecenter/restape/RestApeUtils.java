package nl.esciencecenter.restape;

import java.io.IOException;
import java.util.function.Consumer;

import nl.uu.cs.ape.APE;
import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.configuration.APECoreConfig;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllPredicates;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.utils.APEUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class RestApeUtils {

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

}

package nl.esciencecenter.models.documentation;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class represents a single element of the taxonomy.
 * TODO: This class is not used at the moment, but it is a good idea to use it
 * in the future.
 * 
 */
public class TaxonomyElem {
    public String constraintID;
    public String description;
    public Map[] parameters;

    public TaxonomyElem() {
    }
}

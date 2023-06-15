package nl.esciencecenter.models.documentation;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class represents a single constraint template.
 * TODO: This class is not used at the moment, but it is a good idea to use it
 * in the future.
 * 
 */
public class ConstraintElem {
    public String id;
    public String label;
    public TaxonomyElem[] subsets;

    public ConstraintElem() {
    }

}

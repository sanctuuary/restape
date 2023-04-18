package nl.esciencecenter.models;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class represents a single element of the taxonomy.
 * TODO: This class is not used at the moment, but it is a good idea to use it
 * in the future.
 * 
 * @author Vedran
 *
 */
public class TaxonomyElem {
    public String id;
    public String label;
    public TaxonomyElem[] subsets;

    public TaxonomyElem() {
    }

}

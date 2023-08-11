package nl.esciencecenter.models.documentation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * This class represents a single element of the taxonomy.
 * TODO: This class is not used at the moment, but it is a good idea to use it
 * in the future.
 * 
 */
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyElem {
    public String id;
    public String label;
    public String root;
    public TaxonomyElem[] subsets;
}

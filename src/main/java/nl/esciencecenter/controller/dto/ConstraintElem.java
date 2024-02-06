package nl.esciencecenter.controller.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * This class represents a single constraint template.
 * TODO: This class is not used at the moment, but it is a good idea to use it
 * in the future.
 * 
 */
@NoArgsConstructor
@AllArgsConstructor
public class ConstraintElem {
    public String id;
    public String label;
    public ConstraintParam[] parameters;

}

class ConstraintParam {
    public String dimension1Root;
    public String dimension2Root;
}

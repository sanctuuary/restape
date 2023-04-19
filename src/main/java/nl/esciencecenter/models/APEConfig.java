package nl.esciencecenter.models;

import nl.uu.cs.ape.APE;
import nl.uu.cs.ape.configuration.APECoreConfig;
import nl.uu.cs.ape.models.enums.ConfigEnum;

public class APEConfig {
    public String tool_annotations_path;
    public String ontology_path;
    public String ontologyPrefixIRI;
    public String toolsTaxonomyRoot;
    public String[] dataDimensionsTaxonomyRoots;
    public boolean strict_tool_annotations;
    public String cwl_annotations_path;
    // public String solutions_dir_path; <- this is locally specified
    public SolutionLength solution_length;
    public int timeout_sec; 
    public int solutions; 
    public int number_of_execution_scripts; 
    public int number_of_generated_graphs; 
    public int number_of_cwl_files; 
    public boolean tool_seq_repeat;
    public APETaxType[] inputs;
    public APETaxType[] outputs;
    public boolean debug_mode;
    public ConfigEnum use_workflow_input = ConfigEnum.ALL;
    public ConfigEnum use_all_generated_data = ConfigEnum.ONE;


    /**
     * This class represents the solution length configuration.
     */
    public class SolutionLength {
        public int min;
        public int max;
    }   

    /**
     * This class represents the APE taxonomy type.
     */
    public class APETaxType {
        public String[] data_0006;
        public String[] format_1915;
    }  


  //"constraints_path": "./GeoGMT/E0/constraints_e0.json",

}

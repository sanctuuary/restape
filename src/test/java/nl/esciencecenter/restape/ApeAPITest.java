package nl.esciencecenter.restape;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.boot.test.context.SpringBootTest;

import nl.uu.cs.ape.utils.APEFiles;

@SpringBootTest
class ApeAPITest {
    
    /**
     * Test the runSynthesis method with POST, with an UNSAT configuration file.
     * No workflow solutions should be returned.
     * 
     * @throws IOException                  if the file cannot be read.
     * @throws OWLOntologyCreationException if the ontology cannot be created.
     */
    @Test
    void runSynthesisFail() throws IOException, OWLOntologyCreationException {
        String configPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json";
        String content = FileUtils.readFileToString(APEFiles.readPathToFile(configPath),
                StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        jsonObject.put("solution_length", new JSONObject().put("min", 1).put("max", 1));
        List<APEWorkflowMetadata> result = ApeAPI.runSynthesis(jsonObject, false);
        assertTrue(result.isEmpty(), "The encoding should be UNSAT.");
    }

    /**
     * Test the runSynthesis method with POST, with a SAT configuration file.
     * Workflow solutions should be returned.
     * 
     * @throws IOException                  if the file cannot be read.
     * @throws OWLOntologyCreationException if the ontology cannot be created.
     */
    @Test
    void runSynthesisPass() throws IOException, OWLOntologyCreationException {
        String configPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json";
        String content = FileUtils.readFileToString(APEFiles.readPathToFile(configPath),
                StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        jsonObject.put("solutions", "1");
        List<APEWorkflowMetadata> result = ApeAPI.runSynthesis(jsonObject, false);
        assertFalse(result.isEmpty(), "The encoding should be SAT.");
    }

    /**
     * Test the runSynthesis method with POST, with a SAT configuration file.
     * Workflow solutions should be returned.
     * 
     * @throws IOException                  if the file cannot be read.
     * @throws OWLOntologyCreationException if the ontology cannot be created.
     */
    @Test
    void runSynthesisAndBenchmarkPass() throws IOException, OWLOntologyCreationException {
        String configPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json";
        String content = FileUtils.readFileToString(APEFiles.readPathToFile(configPath),
                StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        jsonObject.put("solutions", "1");
        List<APEWorkflowMetadata> result = ApeAPI.runSynthesis(jsonObject, true);
        assertFalse(result.isEmpty(), "The encoding should be SAT.");
    }
}

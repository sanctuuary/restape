package nl.esciencecenter.restape;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.boot.test.context.SpringBootTest;

import nl.uu.cs.ape.utils.APEFiles;

@SpringBootTest
class ApeAPITest {
    
    private static final String CONFIG_PATH = "https://raw.githubusercontent.com/Workflomics/tools-and-domains/refs/heads/main/domains/proteomics/config.json";

    /**
     * Test setupApe method to ensure that APE initializes correctly.
     */
    @Test
    void setupApeTest() throws IOException, OWLOntologyCreationException {
        assertNotNull(ApeAPI.setupApe(CONFIG_PATH), "APE instance should be initialized.");
    }

    /**
     * Test getData method to ensure it returns a valid JSON array.
     */
    @Test
    void getDataTest() throws IOException, OWLOntologyCreationException {
        JSONArray data = ApeAPI.getData(CONFIG_PATH);
        assertNotNull(data, "Data should not be null.");
        assertFalse(data.isEmpty(), "Data should not be empty.");
    }

    /**
     * Test getTools method to ensure it returns a valid JSON object.
     */
    @Test
    void getToolsTest() throws IOException, OWLOntologyCreationException {
        JSONObject tools = ApeAPI.getTools(CONFIG_PATH);
        assertNotNull(tools, "Tools should not be null.");
        assertTrue(tools.has("id"), "Tools JSON should have an ID.");
    }

    /**
     * Test getConstraints method to ensure it retrieves constraints correctly.
     */
    @Test
    void getConstraintsTest() throws IOException, OWLOntologyCreationException {
        JSONArray constraints = ApeAPI.getConstraints(CONFIG_PATH);
        assertNotNull(constraints, "Constraints should not be null.");
        assertFalse(constraints.isEmpty(), "Constraints should not be empty.");
    }

    /**
     * Test getDomainConstraints method to verify retrieval of domain-specific constraints.
     */
    @Test
    void getDomainConstraintsTest() throws IOException, OWLOntologyCreationException {
        JSONArray domainConstraints = ApeAPI.getDomainConstraints(CONFIG_PATH);
        assertNotNull(domainConstraints, "Domain constraints should not be null.");
        assertFalse(domainConstraints.isEmpty(), "Domain constraints should not be empty.");
    }

    /**
     * Test runSynthesis method with an UNSAT configuration.
     */
    @Test
    void runSynthesisFail() throws IOException, OWLOntologyCreationException {
        String content = FileUtils.readFileToString(APEFiles.readPathToFile(CONFIG_PATH), StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        jsonObject.put("solution_length", new JSONObject().put("min", 1).put("max", 1));
        List<APEWorkflowMetadata> result = ApeAPI.runSynthesis(jsonObject, false);
        assertTrue(result.isEmpty(), "The encoding should be UNSAT.");
    }

    /**
     * Test runSynthesis method with a SAT configuration.
     */
    @Test
    void runSynthesisPass() throws IOException, OWLOntologyCreationException {
        String content = FileUtils.readFileToString(APEFiles.readPathToFile(CONFIG_PATH), StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        jsonObject.put("solutions", "1");
        List<APEWorkflowMetadata> result = ApeAPI.runSynthesis(jsonObject, false);
        assertFalse(result.isEmpty(), "The encoding should be SAT.");
    }

    /**
     * Test runSynthesis method with benchmarking enabled.
     */
    @Test
    void runSynthesisAndBenchmarkPass() throws IOException, OWLOntologyCreationException {
        String content = FileUtils.readFileToString(APEFiles.readPathToFile(CONFIG_PATH), StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        jsonObject.put("solutions", "1");
        List<APEWorkflowMetadata> result = ApeAPI.runSynthesis(jsonObject, true);
        assertFalse(result.isEmpty(), "The encoding should be SAT.");
    }
}

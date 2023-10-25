package nl.esciencecenter.restape;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import nl.uu.cs.ape.io.APEFiles;

@SpringBootTest
@AutoConfigureMockMvc
public class RestApeControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * Test the getGreetings method.
     * 
     * @throws Exception
     */
    @Test
    public void getGreetings() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Welcome to the RESTful APE API!")));
    }

    /**
     * Test the getData method without a config_path parameter.
     * 
     * @throws Exception
     */
    @Test
    public void getDataFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/get_data").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test the getData method with a config_path parameter.
     * 
     * @throws Exception
     */
    @Test
    public void getDataTest() throws Exception {
        String path = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        mvc.perform(MockMvcRequestBuilders.get("/get_data?config_path=" + path).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Test the getTools method without a config_path parameter.
     * 
     * @throws Exception
     */
    @Test
    public void getToolsFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/get_tools").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test the getTools method with a config_path parameter.
     * 
     * @throws Exception
     */
    @Test
    public void getToolsTest() throws Exception {
        String path = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        mvc.perform(MockMvcRequestBuilders.get("/get_tools?config_path=" + path).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Test the runSynthesis method with GET instead of POST.
     * 
     * @throws Exception
     */
    @Test
    public void runSynthesisGetFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/run_synthesis").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test the runSynthesis method with POST, but without a configuration file.
     * 
     * @throws Exception
     */
    @Test
    public void runSynthesisPostFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/run_synthesis").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test the runSynthesis method with POST, with an UNSAT configuration file.
     * No workflow solutions should be returned.
     * 
     * @throws IOException                  if the file cannot be read.
     * @throws OWLOntologyCreationException if the ontology cannot be created.
     */
    @Test
    public void runSynthesisFail() throws IOException, OWLOntologyCreationException {
        String configPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config_unsat.json";
        String content = FileUtils.readFileToString(APEFiles.readPathToFile(configPath),
                StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        JSONArray result = ApeAPI.runSynthesis(jsonObject, false);
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
    public void runSynthesisPass() throws IOException, OWLOntologyCreationException {
        String configPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json";
        String content = FileUtils.readFileToString(APEFiles.readPathToFile(configPath),
                StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        JSONArray result = ApeAPI.runSynthesis(jsonObject, false);
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
    public void runSynthesisAndBenchmarkPass() throws IOException, OWLOntologyCreationException {
        String configPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json";
        String content = FileUtils.readFileToString(APEFiles.readPathToFile(configPath),
                StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(content);
        JSONArray result = ApeAPI.runSynthesis(jsonObject, true);
        assertFalse(result.isEmpty(), "The encoding should be SAT.");
    }

}
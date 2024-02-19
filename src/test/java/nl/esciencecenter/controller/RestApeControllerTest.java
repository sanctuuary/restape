package nl.esciencecenter.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import nl.esciencecenter.controller.dto.CWLZip;
import nl.esciencecenter.restape.ApeAPI;
import nl.uu.cs.ape.utils.APEFiles;

@SpringBootTest
@AutoConfigureMockMvc
class RestApeControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * Test the getGreetings method.
     * 
     * @throws Exception
     */
    @Test
    void testGetGreetings() throws Exception {
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
    void testGetDataFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/data_taxonomy").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test the getData method with a config_path parameter.
     * 
     * @throws Exception
     */
    @Test
    void testGetData() throws Exception {
        String path = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json";

        mvc.perform(MockMvcRequestBuilders.get("/data_taxonomy?config_path=" + path).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Test the getTools method without a config_path parameter.
     * 
     * @throws Exception
     */
    @Test
    void testGetToolsFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/tools_taxonomy").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test the getTools method with a config_path parameter.
     * 
     * @throws Exception
     */
    @Test
    void testGetTools() throws Exception {
        String path = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json";

        mvc.perform(
                MockMvcRequestBuilders.get("/tools_taxonomy?config_path=" + path).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Test the runSynthesis method with GET instead of POST.
     * 
     * @throws Exception
     */
    @Test
    void testRunSynthesisGestFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/run_synthesis").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test the runSynthesis method with POST, but without a configuration file.
     * 
     * @throws Exception
     */
    @Test
    void testRunSynthesisFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/run_synthesis").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test the runSynthesis method with POST and a valid configuration file.
     * 
     * @throws Exception
     */
    @Test
    void testRunSynthesisPass() throws Exception {

        String configPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json";
        String jsonContent = FileUtils.readFileToString(APEFiles.readPathToFile(configPath),
                StandardCharsets.UTF_8);

        mvc.perform(MockMvcRequestBuilders.post("/run_synthesis")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                .andExpect(status().isOk());
    }

    @Test
    void testPostZipCWLs() throws Exception {

            String path = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json";
            String content = FileUtils.readFileToString(APEFiles.readPathToFile(path),
                            StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(content);
            jsonObject.put("solutions", "1");
            JSONArray result = ApeAPI.runSynthesis(jsonObject, false);
            assertFalse(result.isEmpty(), "The encoding should be SAT.");
            String runID = result.getJSONObject(0).getString("run_id");
            String cwlFile = result.getJSONObject(0).getString("cwl_name");

            String jsonContent = "{\"run_id\": \"" + runID + "\", \"workflows\": [\"" + cwlFile + "\"]}";

            mvc.perform(MockMvcRequestBuilders.post("/cwl_zip")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType("application/zip"));
    }
    
}
package nl.esciencecenter.restape;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.print.attribute.standard.Media;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class RestApeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getGreetings() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Welcome to the RestApe API!")));
    }

    @Test
    public void getDataFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/get_data").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getDataTest() throws Exception {
        String path = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        mvc.perform(MockMvcRequestBuilders.get("/get_data?config_path=" + path).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getToolsFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/get_tools").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getToolsTest() throws Exception {
        String path = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        mvc.perform(MockMvcRequestBuilders.get("/get_tools?config_path=" + path).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void runSynthesisFail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/run_synthesis").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
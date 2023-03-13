package nl.esciencecenter.restape;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(content().string(equalTo("You are using RestAPE RESTful API!")));
    }

    @Test
    public void getDomainAnnotations() throws Exception {
        String onto = "http://onto.owl";
        String tools = "http://onto.ow";
        mvc.perform(MockMvcRequestBuilders.get("/parse_domain?ontologyURL=" + onto + "&toolAnnotationsURL=" + tools)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(onto + "\n" + tools)));
    }

}
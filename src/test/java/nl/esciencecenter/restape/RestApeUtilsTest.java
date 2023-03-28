package nl.esciencecenter.restape;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.boot.test.context.SpringBootTest;

import nl.uu.cs.ape.configuration.APEConfigException;

@SpringBootTest
public class RestApeUtilsTest {

    @Test
    public void loadURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> RestApeUtils.setupApe(urlPath));

    }

    @Test
    public void loadLocalPath() {

        String localPath = "/Users/vedran/git/APE_repo/APE_UseCases/GeoGMT/E0/config.json";
        assertDoesNotThrow(() -> RestApeUtils.setupApe(localPath));

    }

    @Test
    public void getTypesFromURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> RestApeUtils.getData(urlPath));

    }

    @Test
    public void getToolsFromURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> RestApeUtils.getTools(urlPath));

    }

}

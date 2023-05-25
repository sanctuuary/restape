package nl.esciencecenter.restape;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nl.uu.cs.ape.io.APEFiles;
import nl.uu.cs.ape.utils.APEUtils;

@SpringBootTest
public class RestApeUtilsTest {

    @Test
    public void loadURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> ApeAPI.setupApe(urlPath));

    }

    @Test
    public void loadLocalPath() {

        assertDoesNotThrow(() -> {
            File config = APEFiles.readPathToFile(
                    "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json");
            ApeAPI.setupApe(config.getAbsolutePath());
        });

    }

    @Test
    public void getTypesFromURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> ApeAPI.getData(urlPath));

    }

    @Test
    public void getToolsFromURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> ApeAPI.getTools(urlPath));

    }

}

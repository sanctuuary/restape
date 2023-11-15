package nl.esciencecenter.restape;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nl.uu.cs.ape.utils.APEFiles;

@SpringBootTest
class RestApeUtilsTest {

    @Test
    void loadURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> ApeAPI.setupApe(urlPath));

    }

    @Test
    void loadLocalPath() {

        assertDoesNotThrow(() -> {
            File config = APEFiles.readPathToFile(
                    "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json");
            ApeAPI.setupApe(config.getAbsolutePath());
        });

    }

    @Test
    void getTypesFromURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> ApeAPI.getData(urlPath));

    }

    @Test
    void getToolsFromURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> ApeAPI.getTools(urlPath));

    }

}

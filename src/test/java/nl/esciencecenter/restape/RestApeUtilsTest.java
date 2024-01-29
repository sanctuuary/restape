package nl.esciencecenter.restape;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import nl.uu.cs.ape.utils.APEFiles;

@SpringBootTest
class RestApeUtilsTest {

    /**
     * Test whether the APE API can be setup with a URL path.
     */
    @Test
    void loadURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> ApeAPI.setupApe(urlPath));

    }

    /**
     * Test whether the APE API can be setup with a local path.
     */
    @Test
    void loadLocalPath() {

        assertDoesNotThrow(() -> {
            File config = APEFiles.readPathToFile(
                    "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json");
            ApeAPI.setupApe(config.getAbsolutePath());
        });

    }

    /**
     * Test whether the APE API retrieves data types within a domain configured in
     * a URL path.
     */
    @Test
    void getTypesFromURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> ApeAPI.getData(urlPath));

    }

    /**
     * Test whether the APE API retrieves tools within a domain configured in
     * a local path.
     */
    @Test
    void getToolsFromURLPath() {
        String urlPath = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/MassSpectometry/config.json";

        assertDoesNotThrow(() -> ApeAPI.getTools(urlPath));

    }

}

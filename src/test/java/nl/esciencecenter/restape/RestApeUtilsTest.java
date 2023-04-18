package nl.esciencecenter.restape;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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

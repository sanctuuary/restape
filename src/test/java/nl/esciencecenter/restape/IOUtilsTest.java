package nl.esciencecenter.restape;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import nl.esciencecenter.controller.dto.CWLZip;
import nl.uu.cs.ape.utils.APEFiles;

@SpringBootTest
class IOUtilsTest {
    

    @Test
    void testZipFilesForLocalExecution() throws Exception {

            String path = "https://raw.githubusercontent.com/Workflomics/domain-annotations/main/WombatP_tools/config.json";
            String content = FileUtils.readFileToString(APEFiles.readPathToFile(path),
                            StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(content);
            jsonObject.put("solutions", "1");
            JSONArray result = ApeAPI.runSynthesis(jsonObject, false);
            assertFalse(result.isEmpty(), "The encoding should be SAT.");
            String runID = result.getJSONObject(0).getString("run_id");
            String cwlFile = result.getJSONObject(0).getString("cwl_name");

            CWLZip cwlZip = new CWLZip();
            cwlZip.setRunID(runID);
            cwlZip.setWorkflows(List.of(cwlFile));

            IOUtils.zipFilesForLocalExecution(cwlZip);
    }
}

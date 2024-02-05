package nl.esciencecenter.models.documentation;

import java.util.Map;

import lombok.Getter;
import nl.esciencecenter.restape.RestApeUtils;

public class CWLFileInfo {
    @Getter
    private String runID;
    @Getter
    private String fileName;



    public CWLFileInfo(Map<String, Object> cwlInfoJson) throws ClassCastException {
        this.runID = (String) cwlInfoJson.get("run_id");
        this.fileName = (String) cwlInfoJson.get("file_name");
    }

    /**
     * Verify the structure of the CWLZip object. If the structure is not valid, an
     * exception is thrown.
     * Structure is valid if:
     * <ul>
     * <li>runID is a valid UUID</li>
     * <li>file name is a valid CWL file name</li>
     * </ul>
     * 
     * @throws IllegalArgumentException If the structure is not valid.
     */
    public void verifyStructure() throws IllegalArgumentException {
        if (!RestApeUtils.isValidRunID(runID)) {
            throw new IllegalArgumentException("The runID format is not valid.");
        } else if (!RestApeUtils.isValidAPEFileName(fileName, "cwl")) {
            throw new IllegalArgumentException("The CWL file name format is invalid.");
        }
    }
}

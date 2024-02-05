package nl.esciencecenter.models.documentation;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import nl.esciencecenter.restape.RestApeUtils;

public class CWLZip {
    @Getter
    private String runID;
    private List<String> workflows;

    /**
     * Create a CWLZip object from a JSON object.
     * 
     * @param cwlFilesJson JSON object containing the CWL files.
     */
    public CWLZip(Map<String, Object> cwlFilesJson) throws ClassCastException{
        this.runID = (String) cwlFilesJson.get("run_id");
        this.workflows = (List<String>) cwlFilesJson.get("workflows");
    }

    /**
     * Verify the structure of the CWLZip object. If the structure is not valid, an
     * exception is thrown.
     * Structure is valid if:
     * <ul>
     * <li>runID is a valid UUID</li>
     * <li>workflows is a list of valid CWL file names</li>
     * </ul>
     * 
     * @throws IllegalArgumentException If the structure is not valid.
     */
    public void verifyStructure() throws IllegalArgumentException {
        if (!RestApeUtils.isValidRunID(runID)) {
            throw new IllegalArgumentException("The runID format is not valid.");
        } else if (!verifyWorkflows()) {
            throw new IllegalArgumentException("The CWL file name format (under workflows) is invalid.");
        }
    }

    /**
     * Check whether all the workflows are valid CWL file names.
     * 
     * @return true if all the workflows are valid CWL file names, false otherwise.
     */
    private boolean verifyWorkflows() {
        return workflows.stream().allMatch(workflowName -> RestApeUtils.isValidAPEFileName(workflowName, "cwl"));
    }

    /**
     * Get the paths to the CWL files.
     * 
     * @return List of paths to the CWL files.
     */
    public List<Path> getCWLPaths() {
        return workflows.stream()
                .map(fileName -> RestApeUtils.calculatePath(runID, "CWL", fileName))
                .collect(Collectors.toList());
    }

}

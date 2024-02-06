package nl.esciencecenter.controller.dto;

import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.esciencecenter.restape.RestApeUtils;

@Getter
@NoArgsConstructor
public class BenchmarkFileInfo {
    @JsonProperty("run_id")
    private String runID;

    @JsonProperty("file_name")
    private String fileName;
    /**
     * The extension of the benchmark file.
     */
    static final String extension = "json";


    /**
     * Set the runID in case it is valid, i.e., corresponds to runID formatting defined within RESTful APE. If the runID is not valid, an exception is thrown.
     * 
     * @throws IllegalArgumentException If the structure is not valid.
     */
    public void setRunID(String runID) throws IllegalArgumentException {
        if (!RestApeUtils.isValidRunID(runID)) {
            throw new IllegalArgumentException("The runID format '" + runID + "' is invalid.");
        }
        this.runID = runID;
    }
    
    /**
     * Set the file name in case it is valid, i.e., corresponds to file name formatting defined within APE and includes the supported design-time benchmark extension. 
     * If the file name is not valid, an exception is thrown.
     * 
     * @throws IllegalArgumentException If the structure is not valid.
     */
    public void setFileName(String fileName) throws IllegalArgumentException {
        if (!RestApeUtils.isValidAPEFileName(fileName, extension)) {
            throw new IllegalArgumentException("The benchmark file_name format '" + fileName + "' is invalid.");
        }
        this.fileName = fileName;
    }

    /**
     * Get the path to the benchmark file. If the file does not exist or is not accessible, an exception is thrown.
     * 
     * @return Path to the benchmark file.
     */
    public Path calculatePath() {
        Path path = RestApeUtils.calculatePath(runID, "CWL", fileName);
            if(Files.notExists(path)) {
                throw new IllegalArgumentException("The specified benchmark file does not exist: " + path.toString());
            } else if (!Files.isReadable(path)) {
                throw new IllegalArgumentException("The benchmark file cannot be accessed: " + path.toString());
            }
        return path;
    }

}

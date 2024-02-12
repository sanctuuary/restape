package nl.esciencecenter.controller.dto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.esciencecenter.restape.RestApeUtils;
import nl.uu.cs.ape.APE;

@Getter
@NoArgsConstructor
public class ImgFileInfo {
    @JsonProperty("run_id")
    private String runID;
    @JsonProperty("file_name")
    private String fileName;
    private ImageFormat format;


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
     * Set the file name in case it is valid, i.e., corresponds to file name formatting defined within {@link APE} and does not include an extension. 
     * If the file name is not valid, an exception is thrown.
     * 
     * @throws IllegalArgumentException If the structure is not valid.
     */
    public void setFileName(String fileName) throws IllegalArgumentException {
        if (!RestApeUtils.isValidAPEFileNameNoExtension(fileName)) {
            throw new IllegalArgumentException("The image file_name format '" + fileName + "' is invalid.");
        }
        this.fileName = fileName;
    }

    /**
     * Set the format of the image file. If the format is not valid, an exception is thrown.
     * 
     * @param format - format of the image file.
     * @throws IllegalArgumentException If the format is not valid.
     */
    public void setFormat(String format) throws IllegalArgumentException {
        try {
            this.format = ImageFormat.valueOf(format);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The image format '" + format + "'' is not valid or supported.");
        }
    }

    /**
     * Get the path to the image file.
     * 
     * @return Path to the image file.
     */
    public Path calculatePath() {
        Path path = RestApeUtils.calculatePath(runID, "Figures", fileName + "." + format.toString());
        try {
            Files.probeContentType(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("The image file does not exist.");
        } catch (SecurityException e) {
            throw new IllegalArgumentException("The image file cannot be accessed.");
        }
        return path;
    }

    /**
     * Enum to represent the image formats supported.
     */
    private enum ImageFormat {
        png, svg
    }
}

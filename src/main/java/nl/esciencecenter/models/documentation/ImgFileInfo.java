package nl.esciencecenter.models.documentation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import lombok.Getter;
import nl.esciencecenter.restape.RestApeUtils;

public class ImgFileInfo {
    @Getter
    private String runID;
    @Getter
    private String fileName;
    @Getter
    private ImageFormat format;



    public ImgFileInfo(Map<String, String> imgInfoJson) throws IllegalArgumentException{
        this.runID = imgInfoJson.get("run_id");
        this.fileName = imgInfoJson.get("file_name");
        try{
        this.format = ImageFormat.valueOf(imgInfoJson.get("format"));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The format is not valid (svg and png are supported).");
        }
    }

    /**
     * Verify the structure of the file object. If the structure is not valid, an
     * exception is thrown.
     * Structure is valid if:
     * <ul>
     * <li>runID is a valid UUID</li>
     * <li>file name is a valid image file name (with extension png or svg)</li>
     * </ul>
     * 
     * @throws IllegalArgumentException If the structure is not valid.
     */
    public void verifyContent() throws IllegalArgumentException {
        if (!RestApeUtils.isValidRunID(runID)) {
            throw new IllegalArgumentException("The runID format is invalid.");
        }

        if (!RestApeUtils.isValidAPEFileName(fileName)) {
            throw new IllegalArgumentException("The image file name format is invalid.");
        }

        try {
            Files.probeContentType(calculatePath());
        } catch (IOException e) {
            throw new IllegalArgumentException("The image file does not exist.");
        } catch (SecurityException e) {
            throw new IllegalArgumentException("The image file cannot be accessed.");
        }

    }

    /**
     * Get the path to the image file.
     * 
     * @return Path to the image file.
     */
    public Path calculatePath() {
        return RestApeUtils.calculatePath(runID, "Figures", fileName + "." + format.toString());
    }


    /**
     * Enum to represent the image formats supported.
     */
    private enum ImageFormat {
        png, svg
    }
}

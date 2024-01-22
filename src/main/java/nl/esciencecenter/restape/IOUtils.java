package nl.esciencecenter.restape;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IOUtils {

    /**
     * Get the CWL content of the file at the given path.
     * 
     * @param filePath - path to the CWL file
     * @return CWL content of the file representing a workflow
     * @throws IOException - if the file cannot be read
     */
    public static String getLocalCwlFile(Path filePath) throws IOException {
        return FileUtils.readFileToString(filePath.toFile(), StandardCharsets.UTF_8);
    }

    /**
     * Get the JSON content of the file at the given path.
     * 
     * @param filePath - path to the benchmarking JSON file
     * @return CWL content of the file representing a workflow
     * @throws IOException - if the file cannot be read
     */
    public static String getLocalBenchmarkFile(Path filePath) throws IOException {
        return FileUtils.readFileToString(filePath.toFile(), StandardCharsets.UTF_8);
    }

}

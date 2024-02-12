package nl.esciencecenter.restape;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.nio.file.NoSuchFileException;

import org.apache.commons.io.FileUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The {@code IOUtils} class provides static methods to read the input files.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IOUtils {

    /**
     * Get the CWL content of the file at the given path.
     * 
     * @param filePath - path to the CWL file
     * @return CWL content of the file representing a workflow
     * @throws IOException - if the file cannot be read
     */
    public static String getLocalCwlFile(Path filePath) throws IOException, NoSuchFileException {
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

    /**
         * Zip the provided CWL files as well as the CWL input file (`inputs.yml`).
         * 
         * @param cwlFilePaths    List of CWL file names (with extensions).
         * @param locationDirPath Path to the directory where the zip file will be
         *                        created.
         * @return Path to the created zip file.
         * @throws IOException Error is thrown if the zip file cannot be created or
         *                     written to.
         */
        public static Path zipFiles(List<Path> cwlFilePaths, Path locationDirPath) throws IOException {
                Path zipPath = locationDirPath.resolve("workflows.zip");
                try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
                                ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                        for (Path file : cwlFilePaths) {
                                zipOut.putNextEntry(new ZipEntry(file.getFileName().toString()));
                                Files.copy(file, zipOut);
                                zipOut.closeEntry();
                        }
                }
                return zipPath;
        }

}

package nl.esciencecenter.restape;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONObject;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.Getter;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestApeUtils {

    @Getter(lazy = true)
    private static final String solutionPath = getSolutionsDir();
    private static final String allSolutionsDirName = "apeOutputs";
    private static final int hashLength = 10;
    private static final int currentTimestampLength = 13;
    private static final int runIDLength = hashLength + currentTimestampLength;

    /**
     * Create a directory in the file system.
     * 
     * @param dirName - name of the directory
     * @return true if the directory was created, false otherwise.
     */
    static String createDirectory(String dirName) {
        String desiredPath = getSolutionDirectory(dirName);

        File dir = new File(desiredPath);

        if (!dir.exists()) {
            return dir.mkdir() ? desiredPath : "";

        }
        return desiredPath;
    }

    /**
     * Get the path to the directory where the solutions for the given run will be
     * stored.
     * 
     * @param currRunDir - name of the folder where current synthesis run is stored
     * @return Path to the directory.
     */
    private static String getSolutionDirectory(String currRunDir) {

        Path desiredPath = Paths.get(getSolutionPath(), currRunDir);
        return desiredPath.toString();
    }

    /**
     * Get the path to the directory where the solutions will be stored.
     * 
     * @return Path to the directory.
     */
    private static String getSolutionsDir() {
        String currentPathStr = System.getProperty("user.dir");
        Path dirPath = Paths.get(currentPathStr, allSolutionsDirName);
        File dir = dirPath.toFile();
        if (!dir.exists()) {
            dir.mkdir();
        }

        return dirPath.toString();
    }

    /**
     * Generate an unique string from a text by concatenating hash of the text with
     * the current timestamp.
     * 
     * @param text - text to be used in the process
     * @return Unique string.
     */
    static String generateRunID(String text) {
        return generateStringHash(text, hashLength) + System.currentTimeMillis();
    }

     /**
     * Verify if the runID is in valid format, by checking its length and format.
     * 
     * @param runID - runID to be verified
     * @return true if the runID is valid, false otherwise.
     */
    public static boolean verifyRunID(String runID) {
        return runID != null && runID.length() == runIDLength && runID.matches("[a-f0-9]+");
    }
    
    /**
     * Verify if the file name is in valid format, by checking its extension and format. The name should start with `candidate_solution_` followed by a number and end with the specified extension.
     * 
     * @param fileName - file name to be verified
     * @param extension - extension of the file
     * @return true if the file name is valid, false otherwise.
     */
    public static boolean verifyAPEGeneratedFileName(String fileName, String extension) {
        return fileName != null && fileName.matches("candidate_solution_\\d+\\." + extension);
    }


    /**
     * Generate a hashed string of a given length.
     * 
     * @param text   - text to be hashed
     * @param length - length of the string
     * @return Hashed string of a given length.
     */
    public static String generateStringHash(String text, int length) {
        try {
            // Calculate the hash value of the text using MD5 algorithm
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            byte[] hashBytes = md.digest();

            // Convert the hash bytes to a string representation
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String hashString = sb.toString();

            // Take the first 10 characters of the hash string to create the unique string
            String uniqueString = hashString.substring(0, length);

            return uniqueString;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating unique string", e);
        }
    }

    public static Path calculatePath(String runID, String fileSubDir, String fileName) {
        return Paths.get(getSolutionPath(), runID, fileSubDir, fileName);
    }

    /**
     * Combine multiple JSON objects into one.
     * 
     * @param jsonObjects - JSON objects to be combined
     * @return Combined JSON object.
     */
    public static JSONObject combineJSONObjects(JSONObject... jsonObjects) {
        JSONObject combinedJson = new JSONObject();
        for (JSONObject jsonObject : jsonObjects) {
            for (String key : jsonObject.keySet()) {
                combinedJson.put(key, jsonObject.get(key));
            }
        }
        return combinedJson;
    }

}

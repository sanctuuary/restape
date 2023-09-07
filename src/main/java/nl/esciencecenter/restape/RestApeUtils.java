package nl.esciencecenter.restape;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Getter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestApeUtils {

    @Getter(lazy = true)
    private final static String solutionPath = getSolutionsDir();
    private static String allSolutionsDirName = "apeOutputs";

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
    static String generateUniqueString(String text) {
        return generateStringHash(text, 10) + System.currentTimeMillis();
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

}

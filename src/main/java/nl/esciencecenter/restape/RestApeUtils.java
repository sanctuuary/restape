package nl.esciencecenter.restape;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.RandomStringUtils;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestApeUtils {

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
     * Get the path to the directory where the solutions will be stored.
     * 
     * @param dirName - name of the directory
     * @return Path to the directory.
     */
    private static String getSolutionDirectory(String dirName) {
        String currentPathStr = System.getProperty("user.dir");
        Path desiredPath = Paths.get(currentPathStr, allSolutionsDirName, dirName);
        return desiredPath.toString();
    }

    /**
     * Generate a random string token for the user, by concatenating a random
     * string, user id and the timestamp.
     * 
     * @param userID - user id
     * @return Random string token.
     */
    static String generateStringToken(String userID) {
        return generateRandomString(10) + userID + System.currentTimeMillis();
    }

    /**
     * Generate a random string of a given length.
     * 
     * @param length - length of the string
     * @return Random string
     */
    private static String generateRandomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

}

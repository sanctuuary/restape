package nl.esciencecenter.restape;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import nl.uu.cs.ape.APE;

import org.apache.commons.io.FileUtils;

public class ApeUtils {

    public void getDomainAnnotations(String ontologyURL, String toolAnnotationsURL) throws IOException {

        File ontologyFile = readFileFromURL(ontologyURL, "ontology");
        File toolAnnotationsFile = readFileFromURL(toolAnnotationsURL, "tools");

        APE apeFramework = null;
    }

    private File readFileFromURL(String file_url, String file_name) throws IOException {
        File loadedFile = new File(file_name);
        FileUtils.copyURLToFile(
                new URL(file_url),
                loadedFile,
                1000,
                1000);
        return loadedFile;
    }

}

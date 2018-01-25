import java.io.*;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.TreeSet;

/**
 * Parses mvn dependency:tree outputs to look similar to python dependency output
 * Only outputs top level dependencies. Sorts and de-duplicates.
 * Can handle multiple maven dependency trees at once.
 */
public class DependencyParser {

    private static final String INPUT_FILE = "src/main/resources/input.txt";

    public static void main(String[] args) throws IOException {
         String fileString = readFile(INPUT_FILE, Charset.defaultCharset());
         fileString = parseFile(fileString);
         System.out.println(fileString);
    }

    /**
     * Parse the mvn dependency:tree output to look similar to the python dependency output
     */
    private static String parseFile(String fileString) {
        fileString = fileString.replaceAll("\\[INFO] ", "");
        String[] lines = fileString.split("\n");
        StringBuilder sb = new StringBuilder(fileString.length()/2);
        Set<String> uniqueLines = new TreeSet<>();
        for (String line : lines) {
            if (line.startsWith("+-") && line.endsWith("compile")) {
                line = line.substring(3, line.length()-8);
                uniqueLines.add(line.replace("jar:", "jar == "));
            }
        }
        for (String line : uniqueLines) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * https://stackoverflow.com/a/326531/6442902
     */
    private static String readFile(String file, Charset cs)
            throws IOException {
        // No real need to close the BufferedReader/InputStreamReader
        // as they're only wrapping the stream
        try (FileInputStream stream = new FileInputStream(file)) {
            Reader reader = new BufferedReader(new InputStreamReader(stream, cs));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            return builder.toString();
        }
    }
}

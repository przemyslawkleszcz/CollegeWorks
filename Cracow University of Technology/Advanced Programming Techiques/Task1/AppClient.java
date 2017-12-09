import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
public class AppClient {

    /**
     * @param args command line arguments
     */
    public static void main(String[] args) {
        String path = args[0];
        String testString = args[1];

        try (BufferedReader reader = getFileBufferedReader(path)) {
            int resultLine = getLowestDistance(reader, testString);
            System.out.println(resultLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedReader getFileBufferedReader(String path)
            throws FileNotFoundException {

        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        return br;
    }

    private static int getLowestDistance(BufferedReader reader, String testString)
            throws IOException {

        int shortestDistance = -1;
        int shortestDistanceLine = -1;

        int i = 1;
        String line;
        Levenshtein levehnstein = new Levenshtein();
        while ((line = reader.readLine()) != null) {
            int currentDistance = levehnstein.getDistance(testString, line);
            if (currentDistance == 0) {
                shortestDistanceLine = i;
                break;
            }

            if (currentDistance < shortestDistance || shortestDistance < 0) {
                shortestDistance = currentDistance;
                shortestDistanceLine = i;
            }

            i++;
        }

        return shortestDistanceLine;
    }
}

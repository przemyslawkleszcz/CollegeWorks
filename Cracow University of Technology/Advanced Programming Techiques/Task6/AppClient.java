import graphbean.IGraphRemote;
import graphbean.ISearchRemote;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
public class AppClient {
    private static ISearchRemote search;
    private static IGraphRemote graph;

    /**
     * Program entry
     * @param args program arguments
     */
    public static void main(String[] args) throws Exception {
        try {
            String path = args[0];
            prepareConnections();
            prepareGraph(path);
            Map<Integer, ArrayList<Integer>> map = graph.obtainGraph();
            int result = search.getNumOfConsComp(map);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void prepareConnections() 
            throws NamingException{
        Context cont = new InitialContext();
        String n = "java:global/124624/Graph!graphbean.IGraphRemote";
        graph = (IGraphRemote) cont.lookup(n);   
        cont = new InitialContext();
        n = "java:global/124624/Search!graphbean.ISearchRemote";
        search = (ISearchRemote) cont.lookup(n);
    }

    private static BufferedReader getBufferedReader(String file)
            throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    private static void prepareGraph(String file)
            throws IOException {

        graph.reInitialize();
        try (BufferedReader br = getBufferedReader(file)) {
            String newLine;
            while ((newLine = br.readLine()) != null) {
                String[] line = newLine.trim().replaceAll(" +", " ").split(" ");
                if (line.length >= 2) {
                    for (String part : line)
                        graph.appendVertex(Integer.valueOf(part));
                }
                
                for (int i = 0; i < line.length; i += 2)
                    graph.appendEdge(Integer.valueOf(line[i]), 
                            Integer.valueOf(line[i + 1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
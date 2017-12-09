package graphbean;

import java.util.ArrayList;
import java.util.Map;
import javax.ejb.Remote;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@Remote
public interface ISearchRemote {
    /**
     * Returns number of consistent graph components
     * @param graph graph structure
     * @return number of consistent graph components
     */
    public int getNumOfConsComp(Map<Integer, ArrayList<Integer>> graph);
}

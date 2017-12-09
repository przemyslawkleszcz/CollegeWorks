package graphbean;

import java.util.ArrayList;
import java.util.Map;
import javax.ejb.Remote;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@Remote
public interface IGraphRemote {
    /**
     * Returns graph structure
     * @return graph
     */
    public Map<Integer, ArrayList<Integer>> obtainGraph();
    
    /**
     * Adds edge to a structure
     * @param first first vertex
     * @param second second vertex
     */
    public void appendEdge(Integer first, Integer second);
    
    /**
     * Adds vertex to a structure
     * @param vertex
     * @return false if graph contains vertex otherwise true
     */
    public boolean appendVertex(Integer vertex);
    
    /**
     * Reinitializes graph structure
     */
    public void reInitialize();
}

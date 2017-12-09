package graphbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@Stateless
public class Graph implements IGraphRemote, Serializable  {
    private static final long serialVersionUID = 1L;
    private Map<Integer, ArrayList<Integer>> col = new HashMap<>();
    
    /**
     * Returns graph structure
     * @return graph
     */
    @Override
    public Map<Integer, ArrayList<Integer>> obtainGraph() {
        return col;
    }
    
    /**
     * Adds edge to a structure
     * @param first first vertex
     * @param second second vertex
     */
    @Override
    public void appendEdge(Integer first, Integer second) {
        if (col.containsKey(first) && col.containsKey(second)) {
            col.get(first).add(second);
            col.get(second).add(first);   
        }
    }

    /**
     * Adds vertex to a structure
     * @param vertex
     * @return false if graph contains vertex otherwise true
     */
    @Override
    public boolean appendVertex(Integer vertex) {
        if (col.containsKey(vertex))
            return false;
        col.put(vertex, new ArrayList<Integer>());
        return true;
    }

    /**
     * Reinitializes graph structure
     */
    @Override
    public void reInitialize() {
        col = new HashMap<>();
    }
}

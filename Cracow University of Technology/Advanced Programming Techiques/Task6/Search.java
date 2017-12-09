package graphbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@Stateless
public class Search implements ISearchRemote {
      Map<Integer, Boolean> checked = new HashMap<>();
     /**
     * Returns number of consistent graph components
     * @param graph graph structure
     * @return number of consistent graph components
     */
    @Override
    public int getNumOfConsComp(Map<Integer, ArrayList<Integer>> graph) {
        int numOfComp = 0;
        Iterator<Integer> iter1;
        
        for (iter1  = graph.keySet().iterator(); iter1.hasNext(); ) {
            int present = iter1.next();
            checked.put(present, false);
        }
        Iterator<Map.Entry<Integer, Boolean>>
                iter2 = checked.entrySet().iterator();
        
        while(iter2.hasNext()) {
            Map.Entry<Integer, Boolean> next = iter2.next();
            if(next.getValue() == false) {
                try {
                    dfs(next.getKey(), checked, graph);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                numOfComp++;
            }
        }
        
        return numOfComp;
    }
    
        private List<Integer> getClosestMembers(Integer node, Map<Integer,
                ArrayList<Integer>> graph) throws Exception {
        List<Integer> closest = graph.get(node);
        return Collections.unmodifiableList(closest);
    }
        
    private void dfs(int v, Map<Integer, Boolean> checked,
        Map<Integer, ArrayList<Integer>> graph) throws Exception {
        checked.put(v, true);
        Iterator<Integer> i;
        for (i = getClosestMembers(v, graph).iterator(); i.hasNext(); ) {
            int current = i.next();
            if (!checked.get(current)) {
                dfs(current, checked,graph);
            }
        }
    }
}

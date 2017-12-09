import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.Map;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@WebServlet(urlPatterns = {"/Highway"}, name = "servlet124624")
public class Highway extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        calculate(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        calculate(request, response);
    }

    protected void calculate(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {
        String connString = request.getParameter("conn");
        if (connString != null) {
            Graph<Integer> graph = loadData(connString);
            PlanarityTest test = new PlanarityTest();
            boolean isPlanar = test.isPlanar(graph);
            PrintWriter out = response.getWriter();
            out.print(isPlanar ? 1 : 0);
        }
    }

    private Graph<Integer> loadData(String conn) {
        Graph<Integer> graph = new Graph<>();
        try {
            Connection connection = DriverManager.getConnection(conn);
            Statement query = connection.createStatement();
            ResultSet result = query.executeQuery("select i, j, id from hdata");

            int n = 0;
            while (true) {
                if (!result.next())
                    break;

                int i = result.getInt("i");
                int j = result.getInt("j");

                int max = Math.max(i, j);
                if (max > n)
                    n = max;

                graph.addEdge(i, j);
            }

            for (int i = 1; i < n; i++) {
                graph.addEdge(i, i + 1);
            }

            graph.addEdge(1, n);
            return graph;
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}

class PlanarityTest {

    private static final Set<String> setNotInterlaced = ctreateNis();

    private static Set<String> ctreateNis() {
        Set<String> set = new HashSet<>();
        String[] strings = {"xbyb", "bybx", "ybxb", "bxby"};
        Collections.addAll(set, strings);
        return set;
    }

    boolean isPlanar(Graph<Integer> graph) {
        Graph<Integer> cycle = (new GraphProceeder<>(graph)).findCycle();
        return isPlanar(graph, cycle);
    }

    @SuppressWarnings("unchecked")
    private static <T> boolean isPlanar(Graph<T> graph, Graph<T> cycle) {
        if (graph.countEdges() > 3 * graph.countVertices() - 6) {
            return false;
        }
        Set<Graph<T>> pieces = Graph.divideToPieces(graph, cycle);
        for (Graph<T> piece : pieces) {
            if (!Graph.isPath(piece)) {

                T begin = null;
                for (T v : cycle.getVertices()) {
                    if (piece.hasVertex(v)) {
                        begin = v;
                        break;
                    }
                }

                Graph<T> cycleSection = new Graph<>(cycle);
                T previous = begin;
                T current = cycle.getBorderers(previous).iterator().next();

                cycleSection.deleteEdge(previous, current);
                while (!piece.hasVertex(current)) {
                    for (T v : cycle.getBorderers(current)) {
                        if (!v.equals(previous)) {
                            previous = current;
                            current = v;
                            break;
                        }
                    }

                    cycleSection.deleteEdge(previous, current);
                }
                T end = current;

                GraphProceeder<T> proceeder = new GraphProceeder<>(piece);
                Graph<T> piecePath = proceeder.getPath(begin, end, cycle.getVertices());
                Graph<T> pp = Graph.addGraphs(cycle, piece);
                Graph<T> cp = Graph.addGraphs(cycleSection, piecePath);

                boolean planar = isPlanar(pp, cp);
                if (!planar)
                    return false;
            }
        }

        Graph<Integer> interlacement = new Graph<>();
        Object[] arrayOfPieces = pieces.toArray();

        for (int i = 0; i < arrayOfPieces.length; i++) {
            Graph<T> x = (Graph<T>) arrayOfPieces[i];
            for (int j = i + 1; j < arrayOfPieces.length; j++) {
                Graph<T> y = (Graph<T>) arrayOfPieces[j];

                char endChar = ' ';
                String syms = "";
                int bCount = 0;

                GraphProceeder<T> proceeder = new GraphProceeder<>(cycle);
                for (int k = 0; k < cycle.countVertices(); k++) {
                    T v = proceeder.goThroughCycle();
                    if (x.hasVertex(v) && y.hasVertex(v)) {
                        bCount++;
                        syms += 'b';
                        endChar = 'b';
                    } else if (x.hasVertex(v) && endChar != 'x') {
                        syms += 'x';
                        endChar = 'x';
                    } else if (y.hasVertex(v) && endChar != 'y') {
                        syms += 'y';
                        endChar = 'y';
                    }
                }

                boolean interlaced = false;
                if ((endChar == 'x' || endChar == 'y') && syms.charAt(0) == endChar)
                    syms = syms.substring(1);
                if (syms.length() > 4 || bCount > 2)
                    interlaced = true;
                else if (syms.length() == 4 && !setNotInterlaced.contains(syms))
                    interlaced = true;
                if (interlaced) {
                    interlacement.addEdge(i, j);
                }
            }
        }

        return Graph.isBipartite(interlacement);
    }
}

class Graph<T> {

    private HashMap<T, Set<T>> adjDisposition = new HashMap<>();

    Graph() {
    }

    Graph(Graph<T> source) {
        for (T v : source.getVertices()) {
            for (T u : source.getBorderers(v)) {
                this.addEdge(v, u);
            }
        }
    }

    private void addVertex(T v) {
        if (!adjDisposition.containsKey(v)) {
            adjDisposition.put(v, new HashSet<T>());
        }
    }

    void addEdge(T v1, T v2) {
        addVertex(v1);
        addVertex(v2);
        adjDisposition.get(v1).add(v2);
        adjDisposition.get(v2).add(v1);
    }

    void deleteEdge(T v1, T v2) {
        if (hasEdge(v1, v2) && hasEdge(v2, v1)) {
            adjDisposition.get(v1).remove(v2);
            adjDisposition.get(v2).remove(v1);
            if (adjDisposition.get(v1).size() == 0) adjDisposition.remove(v1);
            if (adjDisposition.get(v2).size() == 0) adjDisposition.remove(v2);
        }
    }

    int countVertices() {
        return adjDisposition.size();
    }

    int countEdges() {
        int count = 0;
        for (Set<T> edges : adjDisposition.values()) {
            count += edges.size();
        }
        return count / 2;
    }

    Set<T> getBorderers(T v) {
        return adjDisposition.get(v);
    }

    private int getDegree(T v) {
        if (adjDisposition.containsKey(v)) {
            return adjDisposition.get(v).size();
        } else {
            return -1;
        }
    }

    Set<T> getVertices() {
        return adjDisposition.keySet();
    }

    boolean hasVertex(T v) {
        return adjDisposition.containsKey(v);
    }

    boolean hasEdge(T v1, T v2) {
        return adjDisposition.containsKey(v1) && adjDisposition.get(v1).contains(v2);
    }

    static <T> boolean isPath(Graph<T> graph) {
        int endPoints = 0;
        for (T v : graph.getVertices()) {
            int degree = graph.getDegree(v);
            if (degree == 1) {
                endPoints++;
            } else if (degree != 2) {
                return false;
            }
        }

        return endPoints == 2;
    }

    static <T> boolean isBipartite(Graph<T> graph) {
        return (new GraphProceeder<>(graph)).isBipartite();
    }

    static <T> Set<Graph<T>> divideToPieces(Graph<T> graph, Graph<T> cycle) {
        return (new GraphProceeder<>(graph)).divideToPieces(cycle);
    }

    static <T> Graph<T> addGraphs(Graph<T> g1, Graph<T> g2) {
        Graph<T> newGraph = new Graph<>();
        for (T v : g1.getVertices()) {
            for (T u : g1.getBorderers(v)) {
                newGraph.addEdge(v, u);
            }
        }
        for (T v : g2.getVertices()) {
            for (T u : g2.getBorderers(v)) {
                newGraph.addEdge(v, u);
            }
        }
        return newGraph;
    }
}

class GraphProceeder<T> {
    private Graph<T> graph;
    private Set<T> searched = new HashSet<>();
    private Map<T, Integer> coloring = null;
    private Graph<T> result = null;
    private T goal = null;
    private T next = null;
    private T prev = null;

    GraphProceeder(Graph<T> graph) {
        this.graph = graph;
    }

    boolean isBipartite() {
        if (graph.countVertices() == 0)
            return true;
        coloring = new HashMap<>();
        return isBipartite(graph.getVertices().iterator().next(), true);
    }

    private boolean isBipartite(T v, boolean color) {
        if (coloring.containsKey(v)) {
            return coloring.get(v).equals(color ? 1 : 0);
        } else {
            coloring.put(v, color ? 1 : 0);
            boolean bipartite = true;
            for (T n : graph.getBorderers(v)) {
                bipartite = bipartite && isBipartite(n, !color);
            }

            return bipartite;
        }
    }

    T goThroughCycle() {
        if (next == null) {
            prev = graph.getVertices().iterator().next();
            next = graph.getBorderers(prev).iterator().next();
        } else {
            for (T n : graph.getBorderers(next)) {
                if (!n.equals(prev)) {
                    prev = next;
                    next = n;
                    break;
                }
            }
        }
        return prev;
    }

    Graph<T> getPath(T start, T end, Collection<T> banned) {
        searched.clear();
        searched.addAll(banned);
        result = new Graph<>();
        goal = end;
        boolean pathFound = getPath(start);
        return pathFound ? result : null;
    }

    private boolean getPath(T v) {
        searched.add(v);
        for (T n : graph.getBorderers(v)) {
            if (n.equals(goal)) {
                result.addEdge(v, n);
                return true;
            } else if (!searched.contains(n)) {
                result.addEdge(v, n);
                boolean pathFound = getPath(n);
                if (pathFound) return true;
                result.deleteEdge(v, n);
            }
        }

        return false;
    }

    Graph<T> findCycle() {
        searched.clear();
        result = new Graph<>();
        goal = graph.getVertices().iterator().next();
        return findCycle(goal);
    }

    private Graph<T> findCycle(T v) {
        searched.add(v);
        for (T n : graph.getBorderers(v)) {
            if (n.equals(goal) && result.countVertices() > 2) {
                result.addEdge(v, n);
                return result;
            } else if (!searched.contains(n)) {
                result.addEdge(v, n);
                Graph<T> completedCycle = findCycle(n);
                if (completedCycle != null) return completedCycle;
                result.deleteEdge(v, n);
            }
        }

        return null;
    }

    Set<Graph<T>> divideToPieces(Graph<T> cycle) {
        searched.clear();
        Set<Graph<T>> pieces = new HashSet<>();
        for (T v : cycle.getVertices()) {
            searched.add(v);
            for (T n : graph.getBorderers(v)) {
                if (!searched.contains(n) && !cycle.hasEdge(n, v)) {
                    result = new Graph<>();
                    result.addEdge(v, n);
                    makePiece(cycle, n);
                    pieces.add(result);
                }
            }
        }

        return pieces;
    }

    private void makePiece(Graph<T> cycle, T v) {
        if (cycle.hasVertex(v))
            return;
        searched.add(v);
        for (T n : graph.getBorderers(v)) {
            if (!result.hasEdge(n, v)) {
                result.addEdge(v, n);
                makePiece(cycle, n);
            }
        }
    }
}

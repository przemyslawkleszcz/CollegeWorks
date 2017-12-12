import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import graphbean.IPlateRemote;
import graphbean.ICostRemote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
public class AppClient {

    private static IPlateRemote plate;
    private static ICostRemote cost;

    /**
     * Program entry
     * @param args program arguments
     */
    public static void main(String[] args) throws Exception {
        Context cont = new InitialContext();
        DataSource ds = (DataSource) cont.lookup(args[0]);
        
        Connection conn = ds.getConnection();
        String table = args[1];
        prepareConnections();
        plate.reInitialize();
        getData(conn, table);
        double result = cost.calculateMinCost();
        System.out.print(result);
    }
    
    private static void getData(Connection conn, String table) {
        try {
            String statement = "SELECT x,y FROM " + table;
            PreparedStatement q = conn.prepareStatement(statement);
            q.execute();
            ResultSet result = q.getResultSet();
            while (result.next()) {
                double x = result.getDouble("x");
                double y = result.getDouble("y");
                plate.addCost(x, y);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void prepareConnections()
            throws NamingException {
        Context cont = new InitialContext();
        String n = "java:global/124624/Plate!graphbean.IPlateRemote";
        plate = (IPlateRemote) cont.lookup(n);
        cont = new InitialContext();
        n = "java:global/124624/Cost!graphbean.ICostRemote";
        cost = (ICostRemote) cont.lookup(n);
    }
}

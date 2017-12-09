import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import pl.jrj.dsm.IDSManagerRemote;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@WebServlet(urlPatterns = {"/Solver"}, name = "servlet124624")
public class Solver extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private ISolidRemote solid;

    /**
     * @param req request object
     * @param resp response object
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        process(req, resp);
    }

    /**
     * @param req request object
     * @param resp response object
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String table = req.getParameter("t");
        if (table != null) {
            try {
                Connection conn = getConnection();
                List<Solid.Point> data = getData(conn, table);
                double area = solid.calculate(data);
                resp.getWriter().write(String.valueOf(area));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Connection getConnection()
            throws SQLException {
        try {
            Context cont = new InitialContext();
            String n = "java:global/ejb-project/DSManager!pl.jrj.dsm.IDSManagerRemote";
            IDSManagerRemote man = (IDSManagerRemote) cont.lookup(n);
            cont = new InitialContext();
            DataSource ds = (DataSource) cont.lookup(man.getDS());
            return ds.getConnection();
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Solid.Point> getData(Connection conn, String table) {
        try {
            String statement = "SELECT x,y,z FROM " + table;
            PreparedStatement q = conn.prepareStatement(statement);
            q.execute();
            ResultSet result = q.getResultSet();
            List<Solid.Point> list = new ArrayList<>();
            while (result.next()) {
                double x = result.getDouble("x");
                double y = result.getDouble("y");
                double z = result.getDouble("z");
                list.add(new Solid.Point(x, y, z));
            }

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

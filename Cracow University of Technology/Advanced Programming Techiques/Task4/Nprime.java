import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@WebServlet(urlPatterns = {"/Nprime"}, name = "servlet124624")
public class Nprime extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private NprimeRemote nPrimeRemote;

    private void process(HttpServletRequest request,
                         HttpServletResponse response, boolean isPost)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        String n = request.getParameter("n");
        if (n != null) {
            int number = Integer.parseInt(n);
            if (!isPost)
                number *= (-1);

            PrintWriter out = response.getWriter();
            out.println(nPrimeRemote.prime(number));
        }
    }

    /**
     * @param request  request object
     * @param response response object
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        process(request, response, false);
    }

    /**
     * @param request  request object
     * @param response response object
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        process(request, response, true);
    }
}
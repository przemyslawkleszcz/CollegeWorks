import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import pl.jrj.mdb.IMdbManager;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Path("control")
public class RESTcontrol {
    static boolean isStarted = false;
    static Long sessionId = null;
    static Integer errors = 0;
    static Integer counter = 0;

    static Pattern regex = Pattern.compile("^0b([10]+)$",
            Pattern.CASE_INSENSITIVE);

    /**
     * start message handler
     * @return empty string
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("start")
    public String control_start() {
        if (isStarted) {
            errors++;
        } else {
            sessionId = register();
            isStarted = true;
        }
        return "";
    }

    /**
     * stop message handler
     * @return empty string
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("stop")
    public String control_stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            errors++;
        }

        return "";
    }

    /**
     * res message handler
     * @return sessionId mod counter
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("res")
    public String control_res() {
        if (sessionId == null) {
            return "0";
        } else if (counter == 0) {
            return "0";
        } else {
            Long result = sessionId % counter;
            return result.toString();
        }
    }

    /**
     * err message handler
     * @return sessionId mod errors
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("err")
    public String control_err() {
        if (sessionId == null) {
            return "0";
        } else if (errors == 0) {
            return "0";
        } else {
            Long result = sessionId % errors;
            return result.toString();
        }
    }

    /**
     * clr message handler
     * @return empty string
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("clr")
    public String control_clr() {
        errors = 0;
        counter = 0;
        return "";
    }

    /**
     * icr message handler
     * @return empty string
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("icr")
    public String control_icr() {
        if (isStarted) {
            counter++;
        } else {
            errors++;
        }

        return "";
    }

    /**
     * dcr message handler
     * @return empty string
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("dcr")
    public String control_dcr() {
        if (isStarted) {
            counter--;
        } else {
            errors++;
        }

        return "";
    }

    /**
     * icr/n message handler
     * @param n number
     * @return empty string
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("icr/{n : .*}")
    public String control_icr_n(@PathParam("n") String n) {
        if (isStarted) {
            try {
                Matcher regx = regex.matcher(n);
                if (regx.find()) {
                    counter += Integer.parseInt(regx.group(1), 2);
                } else {
                    counter += Integer.decode(n);
                }
            } catch (Exception e) {
                try {
                    counter += Integer.parseInt(n);
                } catch (Exception ex) {
                    errors++;
                }
            }

        } else {
            errors++;
        }

        return "";
    }

    /**
     * dcr/n message handler
     * @param n number
     * @return empty string
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("dcr/{n : .*}")
    public String control_dcr_n(@PathParam("n") String n) {
        if (isStarted) {
            try {
                Matcher regx = regex.matcher(n);
                if (regx.find()) {
                    counter -= Integer.parseInt(regx.group(1), 2);
                } else {
                    counter -= Integer.decode(n);
                }
            } catch (Exception e) {
                try {
                    counter -= Integer.parseInt(n);
                } catch (Exception ex) {
                    errors++;
                }
            }
        } else {
            errors++;
        }
        return "";
    }

    private Long register() {
        try {
            pl.jrj.mdb.IMdbManager man = (IMdbManager) new InitialContext()
                    .lookup("java:global/mdb-project"
                            + "/MdbManager!pl.jrj.mdb.IMdbManager");
            String val = man.sessionId("124624");
            return Long.parseLong(val);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return null;
    }
}

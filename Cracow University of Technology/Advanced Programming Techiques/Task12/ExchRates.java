package rest;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Formatter;
import java.util.Locale;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import pl.jrj.mdb.IMdbManager;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Path("exchangeRate")
public class ExchRates {

    private static XmlHandler handler;
    private final String url = "http://www.nbp.pl/kursy/xml/a016z180123.xml";

    /**
     * Request handler
     * @param currency currency
     * @return result
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{currency: [A-Za-z]{3}}")
    public String getCurrencyRatio(
            @PathParam("currency") String currency) {
        createHandler();
        String referenceCurrency = getCurrencyId();
        Double result = handler.calculate(referenceCurrency, currency);
        result = round(result, 4);
        String val = new Formatter(Locale.US).format("%.4f", result).toString();
        return val;
    }

    /**
     * Rounds value to given decimal numbers
     * @param value value
     * @param places decimal places
     * @return result
     */
    public static double round(double value, int places) {
        if (places < 0) 
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void createHandler() {
        if (handler != null) 
            return;

        try (InputStream str = new URL(url).openStream()) {
            InputSource source = new InputSource(str);
            SAXParserFactory obj = SAXParserFactory.newInstance();
            SAXParser sax = obj.newSAXParser();
            XmlHandler localHandler = new XmlHandler();
            sax.parse(source, localHandler);
            handler = localHandler;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getCurrencyId() {
        try {
            pl.jrj.mdb.IMdbManager man = (IMdbManager) new InitialContext()
                    .lookup("java:global/mdb-project"
                            + "/MdbManager!pl.jrj.mdb.IMdbManager");
            return man.currencyId();
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return null;
    }
}

package rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Path("pl")
public class GUService {

    private static final String URI = "http://cuda.iti.pk.edu.pl/";

    private static StreetsHandler streetsHandler;
    private static ProvincesHandler provincesHandler;
    private final String idStreets
            = "ULIC_Urzedowy_2018-01-18.zip";
    private final String idTerritories
            = "TERC_Urzedowy_2018-01-18.zip";

    /**
     * Request handler for street
     *
     * @param street street name
     * @return result
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{street}")
    public String getByStreet(@PathParam("street") String street) {
        String result = getResult(null, null, street);
        return result;
    }

    /**
     * Request handler for province and street
     *
     * @param province province code
     * @param street street name
     * @return result
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{province: [\\d+]{2}}/{street}")
    public String getByProvince(@PathParam("province") String province,
            @PathParam("street") String street) {
        String result = getResult(province, null, street);
        return result;
    }

    /**
     * Request handler for province, county and street
     *
     * @param province province code
     * @param county county code
     * @param street street name
     * @return result
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{province: [\\d+]{2}}/{county: [\\d+]{2}}/{street}")
    public String getByCounty(@PathParam("province") String province,
            @PathParam("county") String county,
            @PathParam("street") String street) {
        String result = getResult(province, county, street);
        return result;
    }

    /**
     * Request handler for non separated province,county and street
     *
     * @param provinceCounty non separated province and county codes
     * @param street street name
     * @return result
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{provinceCounty: [\\d+]{4}}/{street}")
    public String getByProvinceCounty(
            @PathParam("provinceCounty") String provinceCounty,
            @PathParam("street") String street) {
        String province = provinceCounty.substring(0, 2);
        String county = provinceCounty.substring(2, 4);
        String result = getResult(province, county, street);
        return result;
    }

    /**
     * Request handler for province short name and street
     *
     * @param province province short name
     * @param street street name
     * @return result
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{province: [A-Za-z]{5}}/{street}")
    public String getByProvinceText(@PathParam("province") String province,
            @PathParam("street") String street) {
        createHandlers();
        String provinceCode = provincesHandler.getCodeByShortName(province);
        String result = getResult(provinceCode, null, street);
        return result;
    }

    /**
     * Request handler for province short name, county and street
     *
     * @param province province short name
     * @param county county code
     * @param street street name
     * @return result
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{province: [A-Za-z]{5}}/{county: [\\d+]{2}}/{street}")
    public String getByCountyText(@PathParam("province") String province,
            @PathParam("county") String county,
            @PathParam("street") String street) {
        createHandlers();
        String provinceCode = provincesHandler.getCodeByShortName(province);
        String result = getResult(provinceCode, county, street);
        return result;
    }

    /**
     * Request handler for non separated province short name, county and street
     *
     * @param provinceCounty
     * @param street street name
     * @return result
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{provinceCounty: [a-zA-Z0-9]{7}}/{street}")
    public String getByProvinceCountyText(
            @PathParam("provinceCounty") String provinceCounty,
            @PathParam("street") String street) {
        createHandlers();
        String province = provinceCounty.substring(0, 5);
        String county = provinceCounty.substring(5, 7);
        String provinceCode = provincesHandler.getCodeByShortName(province);
        String result = getResult(provinceCode, county, street);
        return result;
    }

    private String getResult(String province, String county, String street) {
        createHandlers();
        return streetsHandler.searchedCount(province, county, street);
    }

    private void createHandlers() {
        try {
            if (streetsHandler == null) {
                try (ZipInputStream stream = getZipInputStream(idStreets)) {
                    streetsHandler
                            = createHandler(stream, StreetsHandler.class);
                }
            }
            if (provincesHandler == null) {
                try (ZipInputStream stream = getZipInputStream(idTerritories)) {
                    provincesHandler
                            = createHandler(stream, ProvincesHandler.class);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ZipInputStream getZipInputStream(String id)
            throws MalformedURLException, IOException {

        String link = URI + id;
        InputStream inputStream = new URL(link).openStream();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream,
                StandardCharsets.UTF_8);
        
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            String name = entry.getName();
            if (name.contains(".xml")) {
                return zipInputStream;
            }
        }

        return null;
    }

    private <T extends DefaultHandler> T createHandler(ZipInputStream stream,
            Class<T> type)
            throws InstantiationException, IllegalAccessException {

        SAXParserFactory fab = SAXParserFactory.newInstance();
        try {
            SAXParser parser = fab.newSAXParser();
            DefaultHandler handler = type.newInstance();
            InputSource input = new InputSource(stream);
            parser.parse(input, handler);
            return type.cast(handler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}

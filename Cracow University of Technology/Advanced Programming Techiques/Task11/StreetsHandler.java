package rest;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
public class StreetsHandler extends org.xml.sax.helpers.DefaultHandler {

    private boolean inName = false;
    private boolean inCounty = false;
    private boolean inProvince = false;

    private List<Street> streetList = null;
    private Street street = null;

    /**
     * Returns searched count
     * @param province province code
     * @param county county code
     * @param street street name
     * @return searched count
     */
    public String searchedCount(String province, String county, 
            String street) {
        
        Integer counter = 0;
        for (Street item : streetList) {
            boolean isConfirmed = true;
            if (province != null) {
                isConfirmed = item.getProvince().equals(province);
                if (!isConfirmed) {
                    continue;
                }
            }
            if (county != null) {
                isConfirmed = item.getCounty().equals(county);
                if (!isConfirmed) {
                    continue;
                }
            }
            if (street != null) {
                isConfirmed = item.getName().contains(street);
                if (isConfirmed) {
                    counter++;
                }
            }
        }

        return counter.toString();
    }

    /**
     * Sax start element method
     * @param uri The name space URI, or the empty string if the
     *        element has no name space URI or if name space
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if name space processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @param attributes The attributes attached to the element.  If
     *        there are no attributes, it shall be an empty
     *        Attributes object.
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void startElement(String uri, String localName,
            String qName, org.xml.sax.Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("ULIC")) {
            streetList = new ArrayList<>();
        } else if (qName.equalsIgnoreCase("row")) {
            this.street = new Street();
        } else if (qName.equalsIgnoreCase("NAZWA_1")) {
            inName = true;
        } else if (qName.equalsIgnoreCase("POW")) {
            inCounty = true;
        } else if (qName.equalsIgnoreCase("WOJ")) {
            inProvince = true;
        }
    }

    /**
     * Sax characters method
     * @param ch The characters.
     * @param start The start position in the character array.
     * @param length The number of characters to use from the character array.
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {

        if (inName) {
            this.street.setName(new String(ch, start, length));
            inName = false;
        } else if (inCounty) {
            this.street.setCounty(new String(ch, start, length));
            inCounty = false;
        } else if (inProvince) {
            this.street.setProvince(new String(ch, start, length));
            inProvince = false;
        }
    }

    /**
     * Sax end element method
     * @param uri The name space URI, or the empty string if the
     *        element has no name space URI or if name space
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if name space processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("row")) {
            streetList.add(this.street);
        }
    }
}

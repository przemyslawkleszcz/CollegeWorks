package rest;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
public class ProvincesHandler extends org.xml.sax.helpers.DefaultHandler {

    private boolean inCode = false;
    private boolean inName = false;
    private boolean inType = false;

    private List<Province> provincesList = null;
    private Province province = null;

    /**
     * Returns province code by short name
     * @param shortName province short name
     * @return province code
     */
    public String getCodeByShortName(String shortName) {
        for (Province item : provincesList) {
            if (item.getShortName().equalsIgnoreCase(shortName)) {
                return item.getCode();
            }
        }

        return null;
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

        if (qName.equalsIgnoreCase("teryt")) {
            provincesList = new ArrayList<>();
        } else if (qName.equalsIgnoreCase("row")) {
            province = new Province();
        } else if (qName.equalsIgnoreCase("WOJ")) {
            inCode = true;
        } else if (qName.equalsIgnoreCase("NAZWA")) {
            inName = true;
        } else if (qName.equalsIgnoreCase("NAZWA_DOD")) {
            inType = true;
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

        if (inCode) {
            province.setCode(new String(ch, start, length));
            inCode = false;
        } else if (inName) {
            String shortName = new String(ch, start, 5);
            province.setShortName(shortName);
            inName = false;
        } else if (inType) {
            province.setType(new String(ch, start, length));
            inType = false;
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

        if (qName.equalsIgnoreCase("row") && province.getType()
                .startsWith("wojew")) {
            String normalized = province.getShortName();

            String missingCharacterCapital = Character.toString((char)0x0141);
            String missingCharacterSmall = Character.toString((char)0x0142);
            
            normalized = Normalizer
                    .normalize(normalized, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .replace(missingCharacterCapital, "L")
                    .replace(missingCharacterSmall, "l");
                    
            province.setShortName(normalized);
            provincesList.add(province);
        }
    }
}

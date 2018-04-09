package rest;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
public class XmlHandler extends org.xml.sax.helpers.DefaultHandler {

    private List<Currency> currencies = null;
    private Currency currency = null;

    private boolean inName = false;
    private boolean inConverter = false;
    private boolean inCurrencyCode = false;
    private boolean inAverageCourse = false;

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

        if (qName.equalsIgnoreCase("tabela_kursow")) {
            currencies = new ArrayList<>();
        } else if (qName.equalsIgnoreCase("pozycja")) {
            this.currency = new Currency();
        } else if (qName.equalsIgnoreCase("nazwa_waluty")) {
            inName = true;
        } else if (qName.equalsIgnoreCase("przelicznik")) {
            inConverter = true;
        } else if (qName.equalsIgnoreCase("kod_waluty")) {
            inCurrencyCode = true;
        } else if (qName.equalsIgnoreCase("kurs_sredni")) {
            inAverageCourse = true;
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
            this.currency.setName(new String(ch, start, length));
            inName = false;
        } else if (inConverter) {
            Integer value = Integer.parseInt(new String(ch, start, length));
            this.currency.setConverter(value);
            inConverter = false;
        } else if (inCurrencyCode) {
            this.currency.setCurrencyCode(new String(ch, start, length));
            inCurrencyCode = false;
        } else if (inAverageCourse) {
            String text = new String(ch, start, length);
            text = text.replace(",", ".");
            Double value = Double.parseDouble(text);
            this.currency.setAverageCourse(value);
            inAverageCourse = false;
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

        if (qName.equalsIgnoreCase("pozycja")) {
            currencies.add(this.currency);
        }
    }
    
    /**
     * Returns currencies ratio
     * @param refCurrency reference currency
     * @param currency currency
     * @return ratio
     */
    public Double calculate(String refCurrency, String currency) {
        Currency currencyObj = getCurrency(currency);
        Double courseInZl = currencyObj.getAverageCourse();
        if(refCurrency.equalsIgnoreCase("PLN"))
            return courseInZl;
        
        Currency refCurrencyObj = getCurrency(refCurrency);
        return courseInZl / refCurrencyObj.getAverageCourse();
    }
    
    private Currency getCurrency(String currency) {
        for (Currency value : currencies) {
            if(value.getCurrencyCode().equalsIgnoreCase(currency))
                return value;
        }
        
        return null;
    }
}

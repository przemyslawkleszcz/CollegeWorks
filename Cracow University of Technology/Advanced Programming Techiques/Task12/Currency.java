package rest;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
public class Currency {

    private String name;
    private int converter;
    private String currencyCode;
    private double averageCourse;

    /**
     * Returns name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns converter
     * @return converter
     */
    public int getConverter() {
        return converter;
    }

    /**
     * Sets converter
     * @param converter converter
     */
    public void setConverter(int converter) {
        this.converter = converter;
    }

    /**
     * Returns currency code
     * @return currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets currencyCode
     * @param currencyCode currency code
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Returns average course
     * @return average course
     */
    public double getAverageCourse() {
        return averageCourse;
    }

    /**
     * Sets averageCourse
     * @param averageCourse average course
     */
    public void setAverageCourse(double averageCourse) {
        this.averageCourse = averageCourse;
    }
}

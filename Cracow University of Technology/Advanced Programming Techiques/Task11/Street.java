package rest;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
public class Street {
    
    private String name;
    private String county;
    private String province;

    Street() {
    }

    /**
     * Returns street name
     * @return street name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets street name
     * @param name street name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns county code
     * @return county code
     */
    public String getCounty() {
        return county;
    }

    /**
     * Sets county code
     * @param county county code
     */
    public void setCounty(String county) {
        this.county = county;
    }
    
    /**
     * Returns province code
     * @return province code
     */
    public String getProvince() {
        return province;
    }

    /**
     * Sets province code
     * @param province province code
     */
    public void setProvince(String province) {
        this.province = province;
    }
}

package rest;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
public class Province {

    private String code;
    private String shortName;
    private String type;

    Province() {
    }

    /**
     * Returns province code
     * @return province code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets province code
     * @param code province code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns province short name
     * @return province short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets province short name
     * @param shortName 
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    
    /**
     * Returns territory type
     * @return territory type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets territory type
     * @param type territory type
     */
    public void setType(String type) {
        this.type = type;
    }
}

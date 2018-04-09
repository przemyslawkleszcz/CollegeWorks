package pl.jrj.mdb;

import javax.ejb.Remote;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Remote
public interface IMdbManager {
    /**
     * Returns currency id
     * @return currency id
     */
    public String currencyId();         
}

package pl.jrj.mdb;

import javax.ejb.Remote;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@Remote
public interface IMdbManager {
    /**
     * Returns sessionId
     * @param album student album id
     * @return sessionId
     */
    public String sessionId(String album);
}

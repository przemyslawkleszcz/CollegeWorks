package pl.jrj.mdb;

import javax.ejb.Remote;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Remote
public interface IMdbManager {
    /**
     * return session id
     * @param album student's album id
     * @return session id
     */
    public String sessionId(String album);
}

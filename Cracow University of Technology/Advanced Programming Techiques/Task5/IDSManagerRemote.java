package pl.jrj.dsm;
import javax.ejb.Remote;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Remote
public interface IDSManagerRemote {
    /**
     * @return datasource name
     */
    public String getDS();
}

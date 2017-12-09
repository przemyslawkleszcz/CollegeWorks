import javax.ejb.Remote;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Remote
public interface NprimeRemote {
    int prime(int n);
}
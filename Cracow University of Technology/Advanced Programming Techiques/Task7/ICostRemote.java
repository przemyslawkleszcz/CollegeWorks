package graphbean;

import javax.ejb.Remote;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@Remote
public interface ICostRemote {
    /**
     * Calculates minimum cost
     * @return minimum cost
     */
    public double calculateMinCost();
}

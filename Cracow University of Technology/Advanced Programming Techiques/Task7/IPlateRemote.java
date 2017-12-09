package graphbean;

import javax.ejb.Remote;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@Remote
public interface IPlateRemote {
    
    /**
     * Initializes structure
     */
    public void reInitialize();
    
    /**
     * Adds cost to structure
     * @param x cost x
     * @param y cost y
     */
    public void addCost(double x, double y);
    
    /**
     * Returns max from list X
     * @return max
     */
    public double searchForMaxX();
    
    /**
     * Returns max from list Y
     * @return max
     */
    public double searchForMaxY();
    
    /**
     * Removes value from list X
     * @param min min value of X
     */
    public void removeX(double min);
    
    /**
     * Removes value from list Y
     * @param min min value of Y
     */
    public void removeY(double min);
    
    /**
     * Checks if list Y is empty
     * @return true if list is empty otherwise false
     */
    public boolean isEmptyY();
    
    /**
     * Checks if list X is empty
     * @return true if list is empty otherwise false
     */
    public boolean isEmptyX();
}

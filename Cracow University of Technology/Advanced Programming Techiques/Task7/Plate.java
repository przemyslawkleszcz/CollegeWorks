package graphbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import javax.ejb.Singleton;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@Stateless
public class Plate implements IPlateRemote, Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Double> costsX;
    private List<Double> costsY;
    
    /**
     * Initializes structure
     */
    @Override
    public void reInitialize() {
        costsX = new ArrayList<>();
        costsY = new ArrayList<>();
    }

    /**
     * Adds cost to structure
     * @param x cost x
     * @param y cost y
     */
    @Override
    public void addCost(double x, double y) {
        if (x > 0) {
            costsX.add(x);
        } else if (y > 0) {
            costsY.add(y);
        }
    }
    
    /**
     * Returns max from list X
     * @return max
     */
    @Override
    public double searchForMaxX() {
        return searchForMax(costsX);
    }

    /**
     * Returns max from list Y
     * @return max
     */
    @Override
    public double searchForMaxY() {
        return searchForMax(costsY);
    }
    
    /**
     * Removes value from list X
     * @param min min value of X
     */
    @Override
    public void removeX(double min){
        remove(costsX, min);
    }
    
    /**
     * Removes value from list Y
     * @param min min value of Y
     */
    @Override
    public void removeY(double min){
        remove(costsY, min);
    }
    
    
    /**
     * Checks if list X is empty
     * @return true if list is empty otherwise false
     */
    @Override
    public boolean isEmptyX() {
        return costsX.isEmpty();
    }
    
    /**
     * Checks if list Y is empty
     * @return true if list is empty otherwise false
     */
    @Override
    public boolean isEmptyY() {
        return costsY.isEmpty();
    }
    
    private void remove(List<Double> list, double min){
        list.remove(list.indexOf(min));
    }
    
    private double searchForMax(List<Double> list) {
        double max;

        try {
            max = Collections.max(list);
        } catch (NoSuchElementException ex) {
            max = -1;
        }

        return max;
    }
}

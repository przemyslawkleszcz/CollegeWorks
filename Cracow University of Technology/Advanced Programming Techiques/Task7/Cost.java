package graphbean;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@Stateless
public class Cost implements ICostRemote {
    private int cutsX;
    private int cutsY;
    private double sumOfCosts;
    
    private IPlateRemote plate;

    /**
     * Calculates minimum cost
     * @return minimum cost
     */
    @Override
    public double calculateMinCost() {
        sumOfCosts = 0;
        cutsX = 1;
        cutsY = 1;
        
        try {
            prepareConnections();
        } catch (NamingException ex) {
            Logger.getLogger(Cost.class.getName()).log(Level.SEVERE, null, ex);
        }

        boolean isListXEmpty = plate.isEmptyX();
        boolean isListYEmpty = plate.isEmptyY();
        while (!isListXEmpty || !isListYEmpty) {
            deleteGreaterElement();
            isListXEmpty = plate.isEmptyX();
            isListYEmpty = plate.isEmptyY();
        }
        
        return sumOfCosts;
    }

    private void deleteGreaterElement() {
        double maxX = plate.searchForMaxX();
        double maxY = plate.searchForMaxY();
        if (maxX > maxY) {
            plate.removeX(maxX);
            sumOfCosts += maxX * cutsY;
            cutsX++;
        } else {
            plate.removeY(maxY);
            sumOfCosts += maxY * cutsX;
            cutsY++;
        }
    }
    
    private void prepareConnections()
            throws NamingException {
        Context cont = new InitialContext();
        String n = "java:global/124624/Plate!graphbean.IPlateRemote";
        plate = (IPlateRemote) cont.lookup(n);
    }
}

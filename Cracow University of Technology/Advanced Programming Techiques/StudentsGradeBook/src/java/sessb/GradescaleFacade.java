/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessb;

import entities.Gradescale;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author student
 */
@Stateless
public class GradescaleFacade extends AbstractFacade<Gradescale> {

    @PersistenceContext(unitName = "DziennikPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GradescaleFacade() {
        super(Gradescale.class);
    }
    
}

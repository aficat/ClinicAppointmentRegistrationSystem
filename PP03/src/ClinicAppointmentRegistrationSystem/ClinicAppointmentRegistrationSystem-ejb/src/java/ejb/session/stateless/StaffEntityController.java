package ejb.session.stateless;

import entity.StaffEntity;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginException;
import util.exception.StaffNotFoundException;

@Stateless
@Local(StaffEntityControllerLocal.class)
@Remote(StaffEntityControllerRemote.class)

public class StaffEntityController implements StaffEntityControllerLocal, StaffEntityControllerRemote {

    @PersistenceContext(unitName = "ClinicAppointmentRegistrationSystem-ejbPU")
    private EntityManager entityManager;

    
    
    public StaffEntityController() {
    }

    
      @Override
    public StaffEntity createNewStaff(StaffEntity newStaffEntity)
    {
        entityManager.persist(newStaffEntity);
        entityManager.flush();
        
        return newStaffEntity;
    }
    
       
    
    @Override
    public StaffEntity staffLogin(String username, String password) throws InvalidLoginException {
        try {
            StaffEntity staffEntity = retrieveStaffByUsername(username);

            if (staffEntity.getPassword().equals(password)) {
                return staffEntity;
            }
            else
            {
                throw new InvalidLoginException("Username does not exist or invalid password!");
            }
        }
        
        catch(StaffNotFoundException ex)
        {
            throw new InvalidLoginException("Username does not exist or invalid password!");
        }
    }

    @Override
    public StaffEntity retrieveStaffByUsername(String username) throws StaffNotFoundException
    {
        Query query = entityManager.createQuery("SELECT s FROM StaffEntity s WHERE s.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try
        {
            return (StaffEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new StaffNotFoundException("Staff Username " + username + " does not exist!");
        }
    }

}

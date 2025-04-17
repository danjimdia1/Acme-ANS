
package acme.features.authenticated.technician;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.realms.technician.Technician;

public interface AuthenticatedTechnicianRepository extends AbstractRepository {

	@Query("SELECT t FROM Technician t WHERE t.userAccount.id = :id")
	Technician findTechnicianByUserAccountId(int id);

	@Query("SELECT t FROM Technician t")
	List<Technician> findAllTechnicians();

	@Query("SELECT t.licenseNumber FROM Technician t")
	List<String> findAllLicenseNumbers();

	@Query("SELECT ua FROM UserAccount ua WHERE ua.id = :id")
	UserAccount findUserAccountById(int id);
}

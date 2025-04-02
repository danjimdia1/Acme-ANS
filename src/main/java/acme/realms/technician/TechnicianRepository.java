
package acme.realms.technician;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TechnicianRepository extends AbstractRepository {

	@Query("select tch from Technician tch where tch.licenseNumber = :licenseNumber")
	Technician findTechnicianByLicenseNumber(String licenseNumber);
}

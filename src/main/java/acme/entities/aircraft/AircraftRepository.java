
package acme.entities.aircraft;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;

public interface AircraftRepository extends AbstractRepository {

	@Query("SELECT a FROM Aircraft a WHERE a.registrationNumber = :registrationNumber AND a.id != :aircraftId")
	Optional<Aircraft> findByRegistrationNumberAndNotAircraftId(String registrationNumber, int aircraftId);
}

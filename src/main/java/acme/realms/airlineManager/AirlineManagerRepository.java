
package acme.realms.airlineManager;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirlineManagerRepository extends AbstractRepository {

	@Query("SELECT a FROM AirlineManager a WHERE a.identifier = :identifier AND a.id != :airlineManagerId")
	Optional<AirlineManager> findByIdentifier(String identifier, int airlineManagerId);

}


package acme.realms.airlineManager;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirlineManagerRepository extends AbstractRepository {

	@Query("SELECT a FROM AirlineManager a WHERE a.identifier = :identifier AND a.id != :airlineManagerId")
	Optional<AirlineManager> findByIdentifier(@Param("identifier") String identifier, @Param("airlineManagerId") int airlineManagerId);

}


package acme.entities.airport;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirportRepository extends AbstractRepository {

	@Query("SELECT a FROM Airport a WHERE a.iataCode = :iata AND a.id != :airportId")
	Optional<Airport> findByCode(String iata, int airportId);

}

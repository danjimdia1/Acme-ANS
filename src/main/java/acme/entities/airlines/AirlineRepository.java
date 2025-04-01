
package acme.entities.airlines;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;

public interface AirlineRepository extends AbstractRepository {

	@Query("SELECT a FROM Airline a WHERE a.IATA = :iataCode AND a.id != :airlineId")
	Optional<Airline> findByIATA(String iataCode, int airlineId);

}

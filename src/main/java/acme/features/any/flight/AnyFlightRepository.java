
package acme.features.any.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;

@Repository
public interface AnyFlightRepository extends AbstractRepository {

	@Query("SELECT f FROM Flight f WHERE f.draftMode = false")
	Collection<Flight> findAllPublishedFlights();

	@Query("SELECT f FROM Flight f WHERE f.id = :id AND f.draftMode = false")
	Flight findPublishedFlightById(int id);

}

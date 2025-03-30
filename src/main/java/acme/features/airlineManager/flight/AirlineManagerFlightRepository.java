
package acme.features.airlineManager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface AirlineManagerFlightRepository extends AbstractRepository {

	@Query("SELECT f FROM Flight f WHERE f.manager.id = :managerId")
	Collection<Flight> findFlightsByManagerId(int managerId);

	@Query("SELECT f FROM Flight f WHERE f.id = :masterId")
	Flight findFlightById(int masterId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(int flightId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId AND l.draftMode = false")
	Collection<Leg> findPublishedLegsByFlightId(int flightId);

}

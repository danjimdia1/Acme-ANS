
package acme.features.any.leg;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface AnyLegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :id")
	Collection<Leg> findAllLegsByFlightId(int id);

	@Query("SELECT f FROM Flight f WHERE f.id = :id AND f.draftMode = false")
	Flight findPublishedFlightById(int id);

	@Query("SELECT l FROM Leg l WHERE l.id = :id AND l.draftMode = false")
	Leg findPublishedLegById(int id);
}

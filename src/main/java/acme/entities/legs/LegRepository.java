
package acme.entities.legs;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.flightNumber = :flightNumber AND l.id != :legId")
	Optional<Leg> findByFlightNumber(String flightNumber, int legId);

	@Query("SELECT COUNT(l) from Leg l WHERE l.flight.id = :flightId")
	Integer numberOfLavoyers(int flightId);

	@Query("SELECT l.scheduledDeparture FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.scheduledDeparture ASC")
	List<Date> findFirstScheduledDeparture(int flightId);

	@Query("SELECT l.scheduledArrival FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.scheduledArrival DESC")
	List<Date> findLastScheduledArrival(int flightId);

	@Query("SELECT l.departureAirport.city FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.scheduledDeparture ASC")
	List<String> findFirstOriginCity(int flightId);

	@Query("SELECT l.arrivalAirport.city FROM Leg l WHERE l.flight.id = :flightId ORDER BY l.scheduledArrival DESC")
	List<String> findLastDestinationCity(int flightId);
}

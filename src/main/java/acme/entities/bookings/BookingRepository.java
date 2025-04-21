
package acme.entities.bookings;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface BookingRepository extends AbstractRepository {

	@Query("SELECT b FROM Booking b WHERE b.locatorCode = :locatorCode AND b.id != :bookingId")
	Optional<Booking> findByLocatorCodeAnNotBookingId(String locatorCode, int bookingId);

	@Query("SELECT COUNT(br) FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Integer getNumberPassengersOfBooking(Integer bookingId);

	@Query("SELECT b.flight FROM Booking b WHERE b.id = :bookingId")
	Optional<Flight> findFlightByBookingId(Integer bookingId);

	@Query("select f from Flight f where f.id = :flightId")
	Optional<Flight> findFlightById(int flightId);

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select l from Leg l where l.flight.id = :flightId")
	List<Leg> findLegsByFlightId(int flightId);
}


package acme.features.customer.booking;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookingrecords.BookingRecord;
import acme.entities.bookings.Booking;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.passengers.Passenger;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> getAllBookingOf(int customerId);

	@Query("select b from Booking b where b.id = :id")
	Booking getBookingById(int id);

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select f from Flight f where f.id = :flightId")
	Flight findFlightById(int flightId);

	@Query("select b from Booking b where b.locatorCode= :locatorCode")
	Booking findBookingByLocator(String locatorCode);

	@Query("select l from Leg l where l.flight.id = :flightId")
	List<Leg> findLegsByFlightId(int flightId);

	@Query("select b.flight from Booking b where b.id = :bookingId")
	Optional<Flight> findFlightByBookingId(Integer bookingId);

	@Query("select distinct b.locatorCode from Booking b")
	Set<String> findAllLocatorCodes();

	@Query("select b.locatorCode from Booking b where b.id = :bookingId")
	String getLocatorCodeByBookingId(int bookingId);

	@Query("select distinct br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("select f from Flight f where f.draftMode = false")
	Collection<Flight> findValidFlights();

	@Query("select br from BookingRecord br where br.booking.id = :bookingId")
	Collection<BookingRecord> findBookingRecordByBookingId(int bookingId);

	@Query("select b.lastNibble from Booking b where b.id = :bookingId")
	Integer findLastNibbleByBookingId(int bookingId);

}

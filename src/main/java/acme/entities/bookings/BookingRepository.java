
package acme.entities.bookings;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface BookingRepository extends AbstractRepository {

	@Query("SELECT b FROM Booking b WHERE b.locatorCode = :locatorCode AND b.id != :bookingId")
	Optional<Booking> findByLocatorCodeAnNotBookingId(String locatorCode, int bookingId);

	@Query("SELECT COUNT(br) FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Integer getNumberPassengersOfBooking(Integer bookingId);
}

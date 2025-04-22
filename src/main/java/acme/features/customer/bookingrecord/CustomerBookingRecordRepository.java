
package acme.features.customer.bookingrecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id=:bookingId")
	Booking getBookingById(int bookingId);

	@Query("select p from Passenger p where p.customer.id=:customerId")
	Collection<Passenger> getAllPassengersOf(int customerId);

	@Query("SELECT p FROM Passenger p WHERE p.id =:passengerId")
	Passenger findPassengerById(int passengerId);

	@Query("select p from Passenger p where p.customer.id=:customerId and p.draftMode = false")
	Collection<Passenger> getAllPassengersNotDraftOf(int customerId);

	@Query("select br.passenger from BookingRecord br where br.booking.id=:bookingId")
	Collection<Passenger> getPassengersInBooking(int bookingId);

}

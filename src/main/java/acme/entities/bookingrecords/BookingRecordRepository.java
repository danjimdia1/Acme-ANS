
package acme.entities.bookingrecords;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.passengers.Passenger;

@Repository
public interface BookingRecordRepository extends AbstractRepository {

	@Query("SELECT p FROM Passenger p WHERE p.draftMode = false AND p.customer.id = :customerId")
	Collection<Passenger> findUndraftedPassengerByCustomerId(int customerId);

	@Query("SELECT br.passenger FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

}

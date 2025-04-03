
package acme.features.customer.bookingrecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookingrecords.BookingRecord;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("select br from BookingRecord br")
	Collection<BookingRecord> findAllBookingRecords();

	@Query("select br from BookingRecord br where br.id = :bookingRecordId")
	BookingRecord findBookingRecordById(int bookingRecordId);

	@Query("select br from BookingRecord br where br.booking.customer.id = :customerId")
	Collection<BookingRecord> findBookingRecordByCustomerId(int customerId);

	@Query("select br from BookingRecord br where br.booking.id = :bookingId")
	Collection<BookingRecord> findBookingRecordByBookingId(int bookingId);

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(int bookingId);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findBookingsByCustomerId(int customerId);

	@Query("select p from Passenger p where p.id= :bookingId")
	Passenger findPassengerById(int bookingId);

	@Query("select p from Passenger p where p.customer.id = :customerId")
	Collection<Passenger> findPassengersByCustomerId(int customerId);

	@Query("SELECT p FROM Passenger p JOIN Booking b ON b.customer.id = p.customer.id LEFT JOIN BookingRecord br ON br.passenger.id = p.id AND br.booking.id = b.id WHERE p.customer.id = :customerId AND b.id = :bookingId AND br.id IS NULL")
	Collection<Passenger> findAvailablePassengersByBookingId(int customerId, int bookingId);
}

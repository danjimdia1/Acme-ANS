
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.flights.Flight;
import acme.entities.passengers.Passenger;
import acme.realms.customer.Customer;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("select p from Passenger p")
	Collection<Flight> findAllPassengers();

	@Query("select p from Passenger p where p.id = :passengerId")
	Passenger findPassengerById(@Param("passengerId") int passengerId);

	@Query("select c from Customer c where c.id = :customerId")
	Customer findCustomerById(@Param("customerId") int customerId);

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(@Param("bookingId") int bookingId);

	@Query("select p from Passenger p where p.customer.id = :customerId")
	Collection<Passenger> findPassengersByCustomerId(@Param("customerId") int customerId);

	@Query("select distinct br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(@Param("bookingId") int bookingId);

	@Query("SELECT COUNT(p) > 0 FROM Passenger p WHERE p.passportNumber = :passportNumber AND p.customer.id = :customerId AND p.id != :passengerId")
	boolean existsAnotherPassengerWithSamePassport(@Param("passportNumber") String passportNumber, @Param("customerId") int customerId, @Param("passengerId") int passengerId);

}

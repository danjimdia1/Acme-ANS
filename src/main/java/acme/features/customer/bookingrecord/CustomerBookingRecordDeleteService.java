
package acme.features.customer.bookingrecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookingrecords.BookingRecord;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingRecordDeleteService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	protected CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		Integer bookingId = super.getRequest().getData("bookingId", Integer.class);
		Booking booking = this.repository.getBookingById(bookingId);
		boolean authorized = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && booking.isDraftMode();

		if (authorized && "POST".equals(super.getRequest().getMethod())) {
			Integer passengerId = super.getRequest().getData("passenger", Integer.class);
			if (passengerId != null && passengerId != 0) {
				Passenger passenger = this.repository.findPassengerById(passengerId);
				Collection<Passenger> passengers = this.repository.getPassengersInBooking(bookingId);
				authorized = passenger != null && passengers.contains(passenger);
			}
		}

		super.getResponse().setAuthorised(authorized);
	}

	@Override
	public void load() {
		Integer bookingId = super.getRequest().getData("bookingId", Integer.class);
		Booking booking = this.repository.getBookingById(bookingId);
		BookingRecord record;

		if ("POST".equals(super.getRequest().getMethod())) {
			Integer passengerId = super.getRequest().getData("passenger", Integer.class);
			if (passengerId != null && passengerId != 0)
				record = this.repository.findBookingRecordByPassengerBooking(passengerId, bookingId);
			else {
				record = new BookingRecord();
				record.setPassenger(null);
				record.setBooking(booking);
			}
		} else {
			record = new BookingRecord();
			record.setBooking(booking);
		}

		super.getBuffer().addData(record);
	}

	@Override
	public void bind(final BookingRecord object) {
		// No binding needed for delete
	}

	@Override
	public void validate(final BookingRecord object) {
		boolean valid = object.getPassenger() != null;
		super.state(valid, "passenger", "customer.booking-record.delete.passenger.not-null");
	}

	@Override
	public void perform(final BookingRecord object) {
		this.repository.delete(object);
	}

	@Override
	public void unbind(final BookingRecord object) {
		Integer bookingId = super.getRequest().getData("bookingId", Integer.class);
		Booking booking = this.repository.getBookingById(bookingId);
		Collection<Passenger> passengers = this.repository.getPassengersInBooking(bookingId);
		SelectChoices passengerChoices = SelectChoices.from(passengers, "fullName", object.getPassenger());

		Dataset dataset = super.unbindObject(object, "passenger", "booking");
		dataset.put("passengers", passengerChoices);
		dataset.put("locatorCode", booking.getLocatorCode());
		dataset.put("label", booking.getFlight().getLabel());

		super.getResponse().addData(dataset);
	}
}

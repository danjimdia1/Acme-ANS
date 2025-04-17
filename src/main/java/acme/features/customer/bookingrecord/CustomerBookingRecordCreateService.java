
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
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int bookingId;
		Booking booking;
		Customer customer;
		boolean status = false;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		if (booking != null && booking.getCustomer().equals(customer))
			status = true;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		BookingRecord bookingRecord;
		Booking booking;
		int bookingId;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bookingId);

		bookingRecord = new BookingRecord();
		bookingRecord.setBooking(booking);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		int passengerId;
		Passenger passenger;

		passengerId = super.getRequest().getData("passenger", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		Collection<Passenger> availablePassengers = this.repository.findAvailablePassengersByBookingId(bookingRecord.getBooking().getCustomer().getId(), bookingRecord.getBooking().getId());

		if (!availablePassengers.contains(passenger))
			passenger = null;
		bookingRecord.setPassenger(passenger);
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {
		Booking booking = bookingRecord.getBooking();
		boolean notPublished = booking == null || booking.isDraftMode();
		super.state(notPublished, "booking", "acme.validation.bookingRecord.invalid-booking-publish.message");

		Customer customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		Collection<Passenger> availablePassengers = this.repository.findAvailablePassengersByBookingId(customer.getId(), booking.getId());

		boolean isPassengerValid = bookingRecord.getPassenger() != null && availablePassengers.contains(bookingRecord.getPassenger());
		super.state(isPassengerValid, "passenger", "acme.validation.bookingRecord.invalid-passenger.message");

	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.repository.save(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Collection<Passenger> availablePassengers;
		SelectChoices choises;
		Dataset dataset;
		Customer customer;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		availablePassengers = this.repository.findAvailablePassengersByBookingId(customer.getId(), bookingRecord.getBooking().getId());
		choises = SelectChoices.from(availablePassengers, "fullName", bookingRecord.getPassenger());

		dataset = super.unbindObject(bookingRecord);
		dataset.put("booking", bookingRecord.getBooking());
		dataset.put("bookingId", bookingRecord.getBooking().getId());
		dataset.put("passengers", choises);
		dataset.put("passenger", choises.getSelected() != null && choises.getSelected().getKey() != null ? choises.getSelected().getKey() : "0");

		if (availablePassengers.isEmpty())
			dataset.put("noPassengersMessage", "No hay más pasajeros disponibles");

		super.getResponse().addData(dataset);

	}

}

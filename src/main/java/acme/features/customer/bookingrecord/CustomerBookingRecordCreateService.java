
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

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.repository.getBookingById(bookingId);
		Customer customer = booking == null ? null : booking.getCustomer();

		boolean status = booking != null && super.getRequest().getPrincipal().hasRealm(customer) && booking.isDraftMode();

		if (super.getRequest().getMethod().equals("POST")) {
			Integer passengerId = super.getRequest().getData("passenger", Integer.class);
			Passenger passenger = null;
			if (passengerId != null && passengerId != 0)
				passenger = this.repository.findPassengerById(passengerId);

			boolean invalidPassenger = passenger == null || passenger.isDraftMode() || passenger.getCustomer().getId() != customer.getId();
			if (invalidPassenger)
				status = false;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.repository.getBookingById(bookingId);
		BookingRecord bookingRecord = new BookingRecord();
		bookingRecord.setBooking(booking);
		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		super.bindObject(bookingRecord, "passenger");
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {

	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.repository.save(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		assert bookingRecord != null;
		Dataset dataset;

		dataset = super.unbindObject(bookingRecord, "passenger", "booking");
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		int bookingId = super.getRequest().getData("bookingId", int.class);
		Collection<Passenger> addedPassengers = this.repository.getPassengersInBooking(bookingId);

		Collection<Passenger> passengers = this.repository.getAllPassengersNotDraftOf(customerId).stream().filter(p -> !addedPassengers.contains(p)).toList();
		SelectChoices passengerChoices = SelectChoices.from(passengers, "fullName", bookingRecord.getPassenger());
		dataset.put("passengers", passengerChoices);

		super.getResponse().addData(dataset);

	}

}

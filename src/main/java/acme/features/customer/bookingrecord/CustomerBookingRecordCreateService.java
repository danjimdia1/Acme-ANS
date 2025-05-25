
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
		Passenger passenger;
		Integer bookingId;
		Booking booking;
		Customer customer;
		Collection<Passenger> passengers;

		bookingId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.getBookingById(bookingId);
		customer = booking == null ? null : booking.getCustomer();
		passengers = this.repository.getAllPassengersOf(customer.getId()).stream().filter(x -> !x.isDraftMode()).toList();

		boolean status = booking != null && super.getRequest().getPrincipal().hasRealm(customer) && booking.isDraftMode();

		if (status && super.getRequest().getMethod().equals("POST")) {
			Integer passengerId = super.getRequest().getData("passenger", Integer.class);
			if (passengerId != null) {
				if (passengerId != 0) {
					passenger = this.repository.findPassengerById(passengerId);
					status = passengers.contains(passenger) && passenger != null && !passenger.isDraftMode();
				} else
					status = true;
				super.getResponse().setAuthorised(status);
			} else
				status = false;
			super.getResponse().setAuthorised(status);
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		BookingRecord bookingRecord;
		Booking booking;
		booking = this.repository.getBookingById(super.getRequest().getData("bookingId", Integer.class));

		bookingRecord = new BookingRecord();
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
		Dataset dataset;
		SelectChoices passengerChoices;
		Collection<Passenger> passengers;
		Collection<Passenger> passengersInBooking;
		Integer customerId;
		Integer bookingId;
		Booking booking;

		if (super.getRequest().hasData("bookingId", Integer.class)) {
			bookingId = super.getRequest().getData("bookingId", Integer.class);
			booking = this.repository.getBookingById(bookingId);
		} else
			booking = this.repository.getBookingByLocatorCode(super.getRequest().getData("locatorCode", String.class));

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		passengersInBooking = this.repository.getPassengersInBooking(booking.getId());
		passengers = this.repository.getAllPassengersNotDraftOf(customerId).stream().filter(x -> !passengersInBooking.contains(x)).toList();
		passengerChoices = SelectChoices.from(passengers, "fullName", bookingRecord.getPassenger());

		dataset = super.unbindObject(bookingRecord, "passenger", "booking");
		dataset.put("passengers", passengerChoices);
		dataset.put("label", bookingRecord.getBooking().getFlight().getLabel());

		dataset.put("booking", booking);

		super.getResponse().addData(dataset);
	}

}

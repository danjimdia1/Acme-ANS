
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
public class CustomerBookingRecordPublishService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int bookingRecordId;
		BookingRecord bookingRecord;
		Customer customer;

		bookingRecordId = super.getRequest().getData("id", int.class);
		bookingRecord = this.repository.findBookingRecordById(bookingRecordId);
		customer = bookingRecord == null ? null : bookingRecord.getBooking().getCustomer();
		status = bookingRecord != null && super.getRequest().getPrincipal().hasRealm(customer);

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		BookingRecord bookingRecord;
		int bookingRecordId;

		bookingRecordId = super.getRequest().getData("id", int.class);
		bookingRecord = this.repository.findBookingRecordById(bookingRecordId);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		int bId;
		int pId;
		Booking booking;
		Passenger passenger;

		bId = super.getRequest().getData("booking", int.class);
		pId = super.getRequest().getData("passenger", int.class);
		booking = this.repository.findBookingById(bId);
		passenger = this.repository.findPassengerById(pId);

		super.bindObject(bookingRecord, "booking", "passenger");

		bookingRecord.setBooking(booking);
		bookingRecord.setPassenger(passenger);

	}

	@Override
	public void validate(final BookingRecord bookingRecord) {
		boolean notPublishedBooking = true;
		boolean notPublishedPassenger = true;
		Booking booking = bookingRecord.getBooking();
		if (booking != null && booking.isDraftMode()) {
			notPublishedBooking = false;
			super.state(notPublishedBooking, "booking", "acme.validation.booking.invalid-booking-bookingRecord-publish.message");
		}
		Passenger passenger = bookingRecord.getPassenger();
		if (passenger != null && passenger.isDraftMode()) {
			notPublishedPassenger = false;
			super.state(notPublishedPassenger, "passenger", "acme.validation.booking.invalid-passenger-bookingRecord-publish.message");
		}
	}

	@Override
	public void perform(final BookingRecord BookingRecord) {
		this.repository.save(BookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Collection<Booking> bookings;
		Collection<Passenger> passengers;
		SelectChoices books;
		SelectChoices passs;
		Dataset dataset;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		bookings = this.repository.findBookingsByCustomerId(customerId);
		passengers = this.repository.findPassengersByCustomerId(customerId);
		books = SelectChoices.from(bookings, "locatorCode", bookingRecord.getBooking());
		passs = SelectChoices.from(passengers, "passport", bookingRecord.getPassenger());
		dataset = super.unbindObject(bookingRecord, "booking", "passenger", "draftMode");
		dataset.put("booking", books.getSelected().getKey());
		dataset.put("bookings", books);
		dataset.put("passenger", passs.getSelected().getKey());
		dataset.put("passengers", passs);

		super.getResponse().addData(dataset);

	}

}

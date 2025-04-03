
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
public class CustomerBookingRecordShowService extends AbstractGuiService<Customer, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int bookingRecordId;
		BookingRecord bookingRecord;
		Customer customer;
		boolean status;

		bookingRecordId = super.getRequest().getData("id", int.class);
		bookingRecord = this.repository.findBookingRecordById(bookingRecordId);
		customer = bookingRecord == null ? null : bookingRecord.getBooking().getCustomer();
		status = bookingRecord != null && super.getRequest().getPrincipal().hasRealm(customer);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int bookingRecordId;
		BookingRecord bookingRecord;

		bookingRecordId = super.getRequest().getData("id", int.class);
		bookingRecord = this.repository.findBookingRecordById(bookingRecordId);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {
		;
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Collection<Booking> bookings;
		Collection<Passenger> passengers;
		SelectChoices classes;
		SelectChoices choises;
		Dataset dataset;
		Customer customer;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		bookings = this.repository.findBookingsByCustomerId(customer.getId());
		passengers = this.repository.findPassengersByCustomerId(customer.getId());
		classes = SelectChoices.from(bookings, "locatorCode", bookingRecord.getBooking());
		choises = SelectChoices.from(passengers, "fullName", bookingRecord.getPassenger());
		dataset = super.unbindObject(bookingRecord, "booking", "passenger", "draftMode");
		dataset.put("bookings", classes);
		dataset.put("booking", classes.getSelected().getKey());
		dataset.put("passengers", choises);
		dataset.put("passenger", choises.getSelected().getKey());

		super.getResponse().addData(dataset);

	}
}


package acme.features.customer.passenger;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookingrecords.BookingRecord;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;
import acme.realms.customer.Customer;

@GuiService
public class CustomerPassengerCreateService extends AbstractGuiService<Customer, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int bookingId;
		Booking booking;
		Customer customer;
		boolean status = false;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		if (super.getRequest().getData().isEmpty() || super.getRequest().hasData("fullName"))
			status = true;
		else {
			bookingId = super.getRequest().getData("bookingId", int.class);
			booking = this.repository.findBookingById(bookingId);

			if (booking != null && booking.getCustomer().equals(customer))
				status = true;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Passenger passenger;
		Customer customer;
		Booking booking = null;
		int bookingId;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		passenger = new Passenger();
		passenger.setCustomer(customer);
		passenger.setDraftMode(true);

		if (super.getRequest().hasData("bookingId")) {
			bookingId = super.getRequest().getData("bookingId", int.class);
			booking = this.repository.findBookingById(bookingId);
			super.getResponse().addGlobal("bookingId", bookingId);
		}

		super.getBuffer().addData(passenger);
		super.getResponse().addGlobal("booking", booking);

	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");
	}

	@Override
	public void validate(final Passenger passenger) {
		int passengerId;
		boolean existsDuplicate;
		Date dateOfBirthValue;
		boolean isDateOfBirthPast;
		Customer customer;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		passengerId = super.getRequest().getData("id", int.class);
		existsDuplicate = this.repository.existsAnotherPassengerWithSamePassport(passenger.getPassportNumber(), customer.getId(), passengerId);
		super.state(!existsDuplicate, "passportNumber", "acme.validation.passenger.duplicate-passport");

		dateOfBirthValue = super.getRequest().getData("dateOfBirth", Date.class);
		isDateOfBirthPast = dateOfBirthValue != null && dateOfBirthValue.before(MomentHelper.getCurrentMoment());
		super.state(isDateOfBirthPast, "dateOfBirth", "acme.validation.passenger.dateOfBirth.message");

	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.save(passenger);

		if (super.getRequest().hasData("bookingId")) {
			int bookingId = super.getRequest().getData("bookingId", int.class);
			Booking booking = this.repository.findBookingById(bookingId);

			if (booking != null) {
				BookingRecord bookingRecord = new BookingRecord();
				bookingRecord.setBooking(booking);
				bookingRecord.setPassenger(passenger);
				this.repository.save(bookingRecord);
			}
		}
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "draftMode");
		super.getResponse().addData(dataset);

	}

}

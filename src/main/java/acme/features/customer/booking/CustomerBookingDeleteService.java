
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookingrecords.BookingRecord;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.entities.flights.Flight;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingDeleteService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		Integer bookingId = super.getRequest().getData("id", int.class);

		Booking booking = this.repository.getBookingById(bookingId);
		if (booking == null) {
			super.getResponse().setAuthorised(false);
			return;
		}

		Customer customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		boolean status = super.getRequest().getPrincipal().hasRealm(customer) && booking.getCustomer().equals(customer) && booking.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int id;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.getBookingById(id);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		;
	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		for (final BookingRecord bookingRecord : this.repository.findBookingRecordByBookingId(booking.getId()))
			this.repository.delete(bookingRecord);
		this.repository.delete(booking);

	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;

		SelectChoices classChoices;
		SelectChoices flightChoices;
		Collection<Flight> fligths;

		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		fligths = this.repository.findAllFlights().stream().filter(f -> !f.isDraftMode() && f.getScheduledDeparture().after(MomentHelper.getCurrentMoment())).toList();
		flightChoices = SelectChoices.from(fligths, "tag", booking.getFlight());

		dataset = super.unbindObject(booking, "locatorCode", "travelClass", "lastNibble", "draftMode");
		dataset.put("classes", classChoices);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}

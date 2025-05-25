
package acme.features.customer.booking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.entities.flights.Flight;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		int bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.getBookingById(bookingId);
		Customer customer = booking == null ? null : booking.getCustomer();
		boolean status = booking != null && super.getRequest().getPrincipal().hasRealm(customer);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.getBookingById(bookingId);
		super.getBuffer().addData(booking);
	}

	@Override
	public void validate(final Booking booking) {
	}

	@Override
	public void unbind(final Booking booking) {
		List<Flight> valid = this.repository.findValidFlights().stream().filter(f -> f.getScheduledDeparture().after(MomentHelper.getCurrentMoment())).toList();

		Flight current = booking.getFlight();
		Flight selected = current != null && valid.contains(current) ? current : null;

		SelectChoices flightChoices = SelectChoices.from(valid, "label", selected);
		SelectChoices classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Dataset dataset = super.unbindObject(booking, "locatorCode", "travelClass", "purchaseMoment", "price", "lastNibble", "draftMode");

		dataset.put("flight", flightChoices.getSelected() != null ? flightChoices.getSelected().getKey() : "0");
		dataset.put("flights", flightChoices);
		dataset.put("classes", classChoices);
		dataset.put("bookingId", booking.getId());

		super.getResponse().addData(dataset);
	}

}

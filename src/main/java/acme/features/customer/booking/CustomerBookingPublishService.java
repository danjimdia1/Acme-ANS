
package acme.features.customer.booking;

import java.util.Collection;
import java.util.stream.Collectors;

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
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int bookingId;
		Booking booking;
		Customer customer;
		boolean status;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.getBookingById(bookingId);
		customer = booking == null ? null : booking.getCustomer();
		status = booking != null && super.getRequest().getPrincipal().hasRealm(customer) && booking.isDraftMode();

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.getBookingById(bookingId);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "flight", "locatorCode", "travelClass", "lastNibble");
	}

	@Override
	public void validate(final Booking booking) {
		// Falta que tenga al menos un pasajero y que esten todos publicados

		boolean hasFlight = booking.getFlight() != null;
		super.state(hasFlight, "flight", "acme.validation.booking.flight.message");

		boolean hasLastNibble = booking.getLastNibble() != null && !booking.getLastNibble().trim().isEmpty();
		super.state(hasLastNibble, "lastNibble", "acme.validation.lastNibble.message");
	}

	@Override
	public void perform(final Booking booking) {
		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Collection<Flight> flights;
		SelectChoices choices;
		SelectChoices classChoices;
		Dataset dataset;

		flights = this.repository.findAllFlights().stream().filter(flight -> flight.getScheduledDeparture() != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
			&& this.repository.findLegsByFlightId(flight.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());

		boolean flightStillValid = flights.contains(booking.getFlight());
		if (!flightStillValid)
			booking.setFlight(null);

		choices = SelectChoices.from(flights, "label", booking.getFlight());
		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastNibble", "draftMode");

		dataset.put("flight", booking.getFlight() != null && choices.getSelected() != null ? choices.getSelected().getKey() : "0");
		dataset.put("flights", choices);
		dataset.put("classes", classChoices);
		dataset.put("bookingId", booking.getId());

		super.getResponse().addData(dataset);

	}

}

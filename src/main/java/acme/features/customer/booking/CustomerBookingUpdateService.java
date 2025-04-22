
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
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Customer customer;
		Booking booking;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.getBookingById(bookingId);
		customer = booking == null ? null : booking.getCustomer();

		status = super.getRequest().getPrincipal().hasRealm(customer) && booking != null && booking.isDraftMode();

		if (super.getRequest().getMethod().equals("POST")) {
			Integer flightId = super.getRequest().getData("flight", Integer.class);
			Flight flight = null;

			if (flightId != null && flightId != 0)
				flight = this.repository.findFlightById(flightId);

			boolean invalidFlight = flightId != null && flightId != 0 && flight == null;
			if (invalidFlight)
				status = false;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		super.getBuffer().addData(this.repository.getBookingById(super.getRequest().getData("id", int.class)));
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "flight", "locatorCode", "travelClass", "lastNibble");
	}

	@Override
	public void validate(final Booking booking) {
		if (booking.getFlight() == null)
			super.state(false, "*", "javax.validation.constraints.NotNull.message");
	}

	@Override
	public void perform(final Booking booking) {
		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());
		booking.setDraftMode(true);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Collection<Flight> flights;
		SelectChoices choices;
		SelectChoices classes;
		Dataset dataset;

		flights = this.repository.findAllFlights().stream().filter(flight -> flight.getScheduledDeparture() != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
			&& this.repository.findLegsByFlightId(flight.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());

		choices = SelectChoices.from(flights, "label", booking.getFlight());
		classes = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastNibble", "draftMode");
		dataset.put("flights", choices);
		if (booking.isDraftMode() && (choices.getSelected() == null || choices.getSelected().getKey() == null))
			dataset.put("flight", "0");
		else
			dataset.put("flight", booking.getFlight() != null ? choices.getSelected().getKey() : "0");
		dataset.put("classes", classes);
		dataset.put("travelClass", classes.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}

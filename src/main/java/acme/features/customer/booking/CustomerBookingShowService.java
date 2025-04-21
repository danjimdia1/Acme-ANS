
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
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.getBookingById(bookingId);
		Customer customer = booking != null ? booking.getCustomer() : null;

		boolean status = booking != null && super.getRequest().getPrincipal().hasRealm(customer);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.getBookingById(bookingId);

		this.getBuffer().addData(booking);
	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void unbind(final Booking booking) {
		Collection<Flight> flights;
		Collection<Flight> generalFlights;
		SelectChoices choices;
		SelectChoices generalChoices;
		SelectChoices classChoices;
		Dataset dataset;

		flights = this.repository.findAllFlights().stream().filter(flight -> flight.getScheduledDeparture() != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
			&& this.repository.findLegsByFlightId(flight.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());
		generalFlights = this.repository.findAllFlights();

		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "travelClass", "purchaseMoment", "price", "lastNibble", "draftMode");

		if (!booking.isDraftMode()) {
			generalChoices = SelectChoices.from(generalFlights, "label", booking.getFlight());
			dataset.put("flight", generalChoices.getSelected() != null ? generalChoices.getSelected().getKey() : "0");
			dataset.put("flights", generalChoices);

		} else {
			boolean flightStillValid = flights.contains(booking.getFlight());

			if (!flightStillValid)
				booking.setFlight(null);
			choices = SelectChoices.from(flights, "label", booking.getFlight());

			dataset.put("flight", booking.getFlight() != null && choices.getSelected() != null ? choices.getSelected().getKey() : "0");
			dataset.put("flights", choices);
		}

		dataset.put("classes", classChoices);
		dataset.put("bookingId", booking.getId());

		super.getResponse().addData(dataset);
	}

}

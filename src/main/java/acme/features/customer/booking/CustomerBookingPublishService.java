
package acme.features.customer.booking;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.entities.flights.Flight;
import acme.entities.passengers.Passenger;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		int bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.getBookingById(bookingId);
		Customer owner = booking == null ? null : booking.getCustomer();

		boolean status = booking != null && super.getRequest().getPrincipal().hasRealm(owner);

		if (status && "POST".equals(super.getRequest().getMethod())) {
			List<Flight> validFlights = this.repository.findValidFlights().stream().filter(f -> f.getScheduledDeparture().after(MomentHelper.getCurrentMoment())).toList();

			Integer flightId = super.getRequest().getData("flight", Integer.class);
			Flight flight = flightId != null && flightId != 0 ? this.repository.findFlightById(flightId) : null;

			if (flightId != null && flightId != 0 && !validFlights.contains(flight))
				status = false;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.getBookingById(bookingId);
		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "flight", "locatorCode", "travelClass", "lastNibble");
	}

	@Override
	public void validate(final Booking booking) {
		Collection<Passenger> passengers = this.repository.findPassengersByBookingId(booking.getId());
		super.state(!passengers.isEmpty(), "*", "customer.booking.publish.no-passengers");

		boolean allPublished = passengers.stream().allMatch(p -> !p.isDraftMode());
		super.state(allPublished, "passenger", "customer.booking.publish.no-passengers-published");
		Boolean lastNibble = !StringHelper.isBlank(booking.getLastNibble());
		super.state(lastNibble, "lastNibble", "customer.booking.publish.no-card-nibble-stored");
	}

	@Override
	public void perform(final Booking booking) {
		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		List<Flight> valid = this.repository.findValidFlights().stream().filter(f -> f.getScheduledDeparture().after(MomentHelper.getCurrentMoment())).toList();

		Flight current = booking.getFlight();
		Flight selected = current != null && valid.contains(current) ? current : null;

		SelectChoices choices = SelectChoices.from(valid, "label", selected);
		SelectChoices classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Dataset dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastNibble", "draftMode");

		dataset.put("flight", choices.getSelected() != null ? choices.getSelected().getKey() : "0");
		dataset.put("flights", choices);
		dataset.put("classes", classChoices);
		dataset.put("bookingId", booking.getId());

		super.getResponse().addData(dataset);
	}

}

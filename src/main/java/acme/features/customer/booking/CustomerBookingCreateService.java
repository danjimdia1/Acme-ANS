
package acme.features.customer.booking;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Customer customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		Booking booking;
		booking = new Booking();
		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());
		booking.setDraftMode(true);
		booking.setCustomer(customer);
		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "flight", "locatorCode", "travelClass", "lastNibble");
	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());
		booking.setDraftMode(true);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		SelectChoices flights;
		SelectChoices classes;
		Dataset dataset;

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "lastNibble", "price", "draftMode");

		flights = SelectChoices.from(this.repository.findAllFlights().stream().filter(flight -> flight.getScheduledDeparture() != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment())
			&& this.repository.findLegsByFlightId(flight.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList()), "label", booking.getFlight());
		classes = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset.put("flights", flights);
		dataset.put("flight", flights.getSelected() != null && flights.getSelected().getKey() != null ? flights.getSelected().getKey() : "0");
		dataset.put("classes", classes);
		dataset.put("travelClass", classes.getSelected().getKey());

		super.getResponse().addData(dataset);

	}

}

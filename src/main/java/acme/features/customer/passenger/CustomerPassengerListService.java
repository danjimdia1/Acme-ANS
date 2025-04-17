
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;
import acme.realms.customer.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {

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

		if (super.getRequest().getData().isEmpty())
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
		Collection<Passenger> passengers;
		Customer customer;
		int bookingId;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		if (super.getRequest().getData().isEmpty())
			passengers = this.repository.findPassengersByCustomerId(customer.getId());
		else {
			bookingId = super.getRequest().getData("bookingId", int.class);
			passengers = this.repository.findPassengersByBookingId(bookingId);
			super.getResponse().addGlobal("bookingId", bookingId);
			if (super.getRequest().hasData("draftMode")) {
				boolean draftMode = super.getRequest().getData("draftMode", boolean.class);
				super.getResponse().addGlobal("draftMode", draftMode);
			}
		}
		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");

		super.getResponse().addData(dataset);
	}

}


package acme.features.airlineManager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.flights.FlightSelfTransfer;
import acme.entities.legs.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class AirlineManagerFlightDeleteService extends AbstractGuiService<AirlineManager, Flight> {

	@Autowired
	private AirlineManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Flight flight;
		AirlineManager manager;

		manager = (AirlineManager) super.getRequest().getPrincipal().getActiveRealm();

		masterId = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(masterId);
		// manager = flight != null ? null : flight.getManager();
		status = flight != null && flight.isDraftMode() && super.getRequest().getPrincipal().hasRealm(manager);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int id;

		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);

		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		;
	}

	@Override
	public void perform(final Flight flight) {
		Collection<Leg> legs;

		legs = this.repository.findLegsByFlightId(flight.getId());
		this.repository.deleteAll(legs);
		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		SelectChoices selfTransfers = SelectChoices.from(FlightSelfTransfer.class, flight.getSelfTransfer());
		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "draftMode");

		dataset.put("selfTransfers", selfTransfers);
		dataset.put("scheduledDeparture", flight.getScheduledDeparture());
		dataset.put("scheduledArrival", flight.getScheduledArrival());
		dataset.put("originCity", flight.getOriginCity());
		dataset.put("destinationCity", flight.getDestinationCity());
		dataset.put("numberOfLayovers", flight.getNumberOfLayovers());

		super.getResponse().addData(dataset);
	}

}

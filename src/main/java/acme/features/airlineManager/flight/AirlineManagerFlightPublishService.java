
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
public class AirlineManagerFlightPublishService extends AbstractGuiService<AirlineManager, Flight> {

	@Autowired
	private AirlineManagerFlightRepository repository;


	@Override
	public void authorise() {
		int flightId;
		Flight flight;
		AirlineManager manager;

		flightId = super.getRequest().getData("id", int.class);

		flight = this.repository.findFlightById(flightId);

		manager = (AirlineManager) super.getRequest().getPrincipal().getActiveRealm();

		boolean status = flight != null && flight.isDraftMode() && super.getRequest().getPrincipal().hasRealm(manager) && flight.getManager().equals(manager);

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
		int flightId = flight.getId();
		Collection<Leg> legs = this.repository.findLegsByFlightId(flightId);
		Collection<Leg> publishedLegs = this.repository.findPublishedLegsByFlightId(flightId);

		boolean hasLegs = !legs.isEmpty();
		boolean hasPublishedLegs = !publishedLegs.isEmpty();
		super.state(hasLegs, "*", "airline-manager.flight.publish.error.no-legs");
		super.state(hasPublishedLegs, "*", "airline-manager.flight.publish.error.no-legs-published");
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
		this.repository.save(flight);
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

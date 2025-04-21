
package acme.features.airlineManager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class AirlineManagerLegListService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	private AirlineManagerLegRepository repository;


	@Override
	public void authorise() {
		int flightId;
		Flight flight;
		AirlineManager manager;

		flightId = super.getRequest().getData("flightId", int.class);

		flight = this.repository.findFlightById(flightId);

		manager = (AirlineManager) super.getRequest().getPrincipal().getActiveRealm();

		boolean status = flight != null && super.getRequest().getPrincipal().hasRealm(manager) && flight.getManager().equals(manager);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Leg> legs;
		int flightId;

		flightId = super.getRequest().getData("flightId", int.class);
		legs = this.repository.findLegsByFlightId(flightId);

		super.getResponse().addGlobal("flightId", flightId);
		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status");

		dataset.put("flightNumber", leg.getFlightNumber());
		dataset.put("departureAirport", leg.getDepartureAirport().getName());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getName());
		dataset.put("flightId", leg.getFlight().getId());

		super.getResponse().addData(dataset);
	}
}


package acme.features.any.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@GuiService
public class AnyLegListService extends AbstractGuiService<Any, Leg> {

	@Autowired
	private AnyLegRepository repository;


	@Override
	public void authorise() {
		boolean status;

		int flightId = super.getRequest().getData("flightId", int.class);
		Flight flight = this.repository.findPublishedFlightById(flightId);

		if (flight == null)
			status = false;
		else
			status = true;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Leg> legs;
		int flightId;

		flightId = super.getRequest().getData("flightId", int.class);
		legs = this.repository.findAllLegsByFlightId(flightId);

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

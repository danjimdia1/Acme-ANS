
package acme.features.airlineManager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class AirlineManagerLegListService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	private AirlineManagerLegRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Leg> legs;
		int masterId;

		masterId = super.getRequest().getData("flightId", int.class);
		legs = this.repository.findLegsByFlightId(masterId);

		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status");

		dataset.put("flightNumber", leg.getFlightNumber());
		dataset.put("departureAirport", leg.getDepartureAirport().getName());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getName());
		super.getResponse().addData(dataset);
	}
}

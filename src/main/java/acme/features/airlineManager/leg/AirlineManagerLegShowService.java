
package acme.features.airlineManager.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class AirlineManagerLegShowService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	private AirlineManagerLegRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		SelectChoices statuses = SelectChoices.from(LegStatus.class, leg.getStatus());
		SelectChoices departureAirports = SelectChoices.from(this.repository.findAllAirports(), "iataCode", leg.getDepartureAirport());
		SelectChoices arrivalAirports = SelectChoices.from(this.repository.findAllAirports(), "iataCode", leg.getArrivalAirport());
		SelectChoices aircrafts = SelectChoices.from(this.repository.findAircraftsByLegAirline(leg.getId()), "registrationNumber", leg.getAircraft());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");

		dataset.put("duration", leg.getDuration());
		dataset.put("departureAirport", leg.getDepartureAirport().getIataCode());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getIataCode());
		dataset.put("aircraftRegNumber", leg.getAircraft().getRegistrationNumber());

		dataset.put("statuses", statuses);
		dataset.put("departureAirports", departureAirports);
		dataset.put("arrivalAirports", arrivalAirports);
		dataset.put("aircrafts", aircrafts);

		super.getResponse().addData(dataset);
	}

}

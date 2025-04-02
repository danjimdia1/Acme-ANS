
package acme.features.airlineManager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class AirlineManagerLegShowService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	private AirlineManagerLegRepository repository;


	@Override
	public void authorise() {

		int legId;
		Leg leg;
		AirlineManager manager;

		manager = (AirlineManager) super.getRequest().getPrincipal().getActiveRealm();

		legId = super.getRequest().getData("id", int.class);

		leg = this.repository.findLegById(legId);

		boolean status = leg != null && super.getRequest().getPrincipal().hasRealm(manager) && leg.getFlight().getManager().equals(manager);

		super.getResponse().setAuthorised(status);
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

		Collection<Aircraft> aircraftsByAirline = this.repository.findAircraftsByAirline(leg.getFlight().getManager().getAirline().getId());

		SelectChoices statuses = SelectChoices.from(LegStatus.class, leg.getStatus());
		SelectChoices departureAirports = SelectChoices.from(this.repository.findAllAirports(), "iataCode", leg.getDepartureAirport());
		SelectChoices arrivalAirports = SelectChoices.from(this.repository.findAllAirports(), "iataCode", leg.getArrivalAirport());
		SelectChoices aircrafts = SelectChoices.from(aircraftsByAirline, "registrationNumber", leg.getAircraft());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "draftMode", "departureAirport", "arrivalAirport", "aircraft");

		dataset.put("flightId", leg.getFlight().getId());

		dataset.put("airlineIata", leg.getFlight().getManager().getAirline().getIATA());

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

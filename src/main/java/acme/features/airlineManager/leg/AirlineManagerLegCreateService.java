
package acme.features.airlineManager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class AirlineManagerLegCreateService extends AbstractGuiService<AirlineManager, Leg> {

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

		boolean status = flight != null && super.getRequest().getPrincipal().hasRealm(manager) && super.getRequest().getPrincipal().getAccountId() == flight.getManager().getUserAccount().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int masterId = super.getRequest().getData("flightId", int.class);
		Flight flight = this.repository.findFlightById(masterId);

		Leg leg = new Leg();
		leg.setFlight(flight);
		leg.setDraftMode(true);
		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft");
	}

	@Override
	public void validate(final Leg leg) {
		;
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		Collection<Aircraft> aircraftsByAirline = this.repository.findAircraftsByAirline(leg.getFlight().getManager().getAirline().getId());

		SelectChoices statuses = SelectChoices.from(LegStatus.class, leg.getStatus());
		SelectChoices departureAirports = SelectChoices.from(this.repository.findAllAirports(), "iataCode", leg.getDepartureAirport());
		SelectChoices arrivalAirports = SelectChoices.from(this.repository.findAllAirports(), "iataCode", leg.getArrivalAirport());
		SelectChoices aircrafts = SelectChoices.from(aircraftsByAirline, "registrationNumber", leg.getAircraft());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft");

		dataset.put("airlineIata", leg.getFlight().getManager().getAirline().getIATA());
		dataset.put("flightId", leg.getFlight().getId());
		dataset.put("statuses", statuses);
		dataset.put("departureAirports", departureAirports);
		dataset.put("arrivalAirports", arrivalAirports);
		dataset.put("aircrafts", aircrafts);

		super.getResponse().addData(dataset);
	}

}

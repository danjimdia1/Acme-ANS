
package acme.features.airlineManager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class AirlineManagerLegUpdateService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	private AirlineManagerLegRepository repository;


	@Override
	public void authorise() {
		int legId;
		Leg leg;

		legId = super.getRequest().getData("id", int.class);

		leg = this.repository.findLegById(legId);

		boolean status = leg != null && leg.isDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(AirlineManager.class) && super.getRequest().getPrincipal().getAccountId() == leg.getFlight().getManager().getUserAccount().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {

		int aircraftId = super.getRequest().getData("aircraft", int.class);
		Aircraft aircraft = this.repository.findAircraftById(aircraftId);

		int arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		Airport arrivalAirport = this.repository.findAirportById(arrivalAirportId);

		int departureAirportId = super.getRequest().getData("departureAirport", int.class);
		Airport departureAirport = this.repository.findAirportById(departureAirportId);

		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");

		leg.setAircraft(aircraft);
		leg.setArrivalAirport(arrivalAirport);
		leg.setDepartureAirport(departureAirport);
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
		Collection<Airport> airports = this.repository.findAllAirports();
		Collection<Aircraft> aircrafts = this.repository.findAllAircrafts();

		dataset = super.unbindObject(leg, "flightNumber", "scheduledArrival", "scheduledDeparture", "status", "draftMode");
		SelectChoices choiceAircrafts = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());
		SelectChoices choiceDepartureAirports = SelectChoices.from(airports, "iataCode", leg.getDepartureAirport());
		SelectChoices choiceArrivalAirports = SelectChoices.from(airports, "iataCode", leg.getArrivalAirport());
		SelectChoices choiceStatuses = SelectChoices.from(LegStatus.class, leg.getStatus());

		dataset.put("airlineIata", leg.getFlight().getManager().getAirline().getIATA());
		dataset.put("aircraft", choiceAircrafts.getSelected().getKey());
		dataset.put("aircrafts", choiceAircrafts);
		dataset.put("departureAirport", choiceDepartureAirports.getSelected().getKey());
		dataset.put("departureAirports", choiceDepartureAirports);
		dataset.put("arrivalAirport", choiceArrivalAirports.getSelected().getKey());
		dataset.put("arrivalAirports", choiceArrivalAirports);
		dataset.put("statuses", choiceStatuses);
		dataset.put("duration", leg.getDuration());

		dataset.put("flightId", leg.getFlight().getId());

		super.getResponse().addData(dataset);
	}

}

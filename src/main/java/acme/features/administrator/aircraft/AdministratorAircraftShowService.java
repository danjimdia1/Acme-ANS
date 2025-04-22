
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.Status;
import acme.entities.airlines.Airline;

@GuiService
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state -------------------------------------------------------------------
	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		int id;

		id = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		SelectChoices statuses;
		Dataset dataset;
		SelectChoices selectedAirlines;
		Collection<Airline> airlines;

		airlines = this.repository.findAllAirlines();
		selectedAirlines = SelectChoices.from(airlines, "name", aircraft.getAirline());

		statuses = SelectChoices.from(Status.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");

		dataset.put("airlines", selectedAirlines);
		dataset.put("statuses", statuses);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);

	}

}

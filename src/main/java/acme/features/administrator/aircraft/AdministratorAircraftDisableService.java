
package acme.features.administrator.aircraft;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.Status;

@GuiService
public class AdministratorAircraftDisableService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


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
		aircraft = (Aircraft) this.repository.findById(id).get();
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "status");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		;
	}

	@Override
	public void perform(final Aircraft aircraft) {
		if (aircraft.getStatus() == Status.ACTIVE)
			aircraft.setStatus(Status.MAINTENANCE);
		else
			aircraft.setStatus(Status.ACTIVE);

		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "status", "cargoWeight", "details", "airline");

		super.getResponse().addData(dataset);
	}
}


package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;
import acme.entities.airlines.AirlineType;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

	@Autowired
	private AdministratotAirlineRepository repository;


	@Override
	public void authorise() {
		Administrator administrator;

		administrator = (Administrator) super.getRequest().getPrincipal().getActiveRealm();

		boolean status = super.getRequest().getPrincipal().hasRealm(administrator);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airline airline;
		int id;

		id = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(id);

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "IATA", "foundationMoment", "website", "type", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airline airline) {

		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

	}

	@Override
	public void perform(final Airline airline) {
		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		Dataset dataset;

		SelectChoices types = SelectChoices.from(AirlineType.class, airline.getType());

		dataset = super.unbindObject(airline, "name", "IATA", "foundationMoment", "website", "type", "email", "phoneNumber");

		dataset.put("types", types);

		super.getResponse().addData(dataset);
	}
}

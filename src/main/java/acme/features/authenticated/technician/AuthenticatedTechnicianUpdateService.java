
package acme.features.authenticated.technician;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.technician.Technician;

@GuiService
public class AuthenticatedTechnicianUpdateService extends AbstractGuiService<Authenticated, Technician> {

	@Autowired
	private AuthenticatedTechnicianRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		Technician technician = this.repository.findTechnicianByUserAccountId(userAccountId);

		super.getBuffer().addData(technician);
	}

	@Override
	public void bind(final Technician object) {
		super.bindObject(object, "licenseNumber", "phoneNumber", "specialisation", "annualHealthTestPassed", "yearsOfExperience", "certifications");
	}

	@Override
	public void validate(final Technician object) {
		List<Technician> all = this.repository.findAllTechnicians();
		boolean duplicate = all.stream().anyMatch(t -> t.getId() != object.getId() && t.getLicenseNumber().equals(object.getLicenseNumber()));

		super.state(!duplicate, "licenseNumber", "acme.validation.technician.identifier.message");
	}

	@Override
	public void perform(final Technician object) {
		this.repository.save(object);
	}

	@Override
	public void unbind(final Technician object) {
		Dataset dataset = super.unbindObject(object, "licenseNumber", "phoneNumber", "specialisation", "annualHealthTestPassed", "yearsOfExperience", "certifications");
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}


package acme.features.authenticated.technician;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.technician.Technician;

@GuiService
public class AuthenticatedTechnicianCreateService extends AbstractGuiService<Authenticated, Technician> {

	@Autowired
	private AuthenticatedTechnicianRepository repository;


	@Override
	public void authorise() {
		boolean status = !super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		UserAccount userAccount = this.repository.findUserAccountById(userAccountId);

		Technician technician = new Technician();
		technician.setUserAccount(userAccount);

		super.getBuffer().addData(technician);
	}

	@Override
	public void bind(final Technician object) {
		super.bindObject(object, "licenseNumber", "phoneNumber", "specialisation", "annualHealthTestPassed", "yearsOfExperience", "certifications");
	}

	@Override
	public void validate(final Technician object) {
		List<String> existingLicenses = this.repository.findAllLicenseNumbers();
		if (object.getLicenseNumber() != null)
			super.state(!existingLicenses.contains(object.getLicenseNumber()), "licenseNumber", "acme.validation.technician.identifier.message");
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


package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.realms.technician.Technician;

public class TechnicianValidator extends AbstractValidator<ValidTechnician, Technician> {

	// Internal state ---------------------------------------------------------

	// Initialiser ------------------------------------------------------------

	@Override
	public void initialise(final ValidTechnician annotation) {
		assert annotation != null;
	}

	// AbstractValidator interface --------------------------------------------

	@Override
	public boolean isValid(final Technician technician, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (technician == null || technician.getLicenseNumber() == null || technician.getIdentity() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (StringHelper.isBlank(technician.getLicenseNumber()))
			super.state(context, false, "licenseNumber", "javax.validation.constraints.NotBlank.message");
		else {
			boolean containsInitials;
			DefaultUserIdentity identity = technician.getIdentity();
			char nameFirstLetter = identity.getName().charAt(0);
			char surnameFirstLetter = identity.getSurname().charAt(0);
			String initials = "" + nameFirstLetter + surnameFirstLetter;
			containsInitials = StringHelper.startsWith(technician.getLicenseNumber(), initials, false); //The licesnse must start with the 2 initials
			super.state(context, containsInitials, "licenseNumber", "acme.validation.technician.identifier.message");
		}

		result = !super.hasErrors(context);
		return result;
	}
}

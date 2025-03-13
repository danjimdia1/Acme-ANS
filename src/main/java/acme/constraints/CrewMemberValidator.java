
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.realms.crewMember.CrewMember;

public class CrewMemberValidator extends AbstractValidator<ValidCrewMember, CrewMember> {

	@Override
	protected void initialise(final ValidCrewMember annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final CrewMember crewMember, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (crewMember == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {

			String initials = "";
			String name = crewMember.getUserAccount().getIdentity().getName();
			String surname = crewMember.getUserAccount().getIdentity().getSurname();

			initials += name.charAt(0);
			initials += surname.charAt(0);

			boolean validEmployeeCode;

			String employeeCode = crewMember.getEmployeeCode();

			boolean validLength = employeeCode.length() >= 8 && employeeCode.length() <= 9;

			boolean validInitials = employeeCode.startsWith(initials);

			validEmployeeCode = validLength && validInitials;

			super.state(context, validEmployeeCode, "employeeCode", "java.validation.crewMember.employeeCode.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}

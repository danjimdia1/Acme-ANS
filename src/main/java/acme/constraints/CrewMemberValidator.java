
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.realms.crewMember.CrewMember;
import acme.realms.crewMember.CrewMemberRepository;

@Validator
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
			{
				if (StringHelper.isBlank(crewMember.getEmployeeCode()))
					super.state(context, false, "identifier", "java.validation.crewMember.identifier.identifier-couldnt-be-blank");
				else {
					String initials = "";
					String name = crewMember.getUserAccount().getIdentity().getName();
					String surname = crewMember.getUserAccount().getIdentity().getSurname();

					initials += name.charAt(0);
					initials += surname.charAt(0);

					boolean validIdentifier = StringHelper.startsWith(crewMember.getEmployeeCode(), initials, true);

					super.state(context, validIdentifier, "identifier", "java.validation.crewMember.validIdentifier.invalid-identifier");
				}
			}
			{
				CrewMemberRepository repository;
				repository = SpringHelper.getBean(CrewMemberRepository.class);
				boolean repeatedEmployeeCode = repository.findByEmployeeCode(crewMember.getEmployeeCode(), crewMember.getId()).isEmpty();
				super.state(context, repeatedEmployeeCode, "identifier", "java.validation.crewMember.repeatedEmployeeCode.message");
			}
		}

		result = !super.hasErrors(context);

		return result;
	}
}

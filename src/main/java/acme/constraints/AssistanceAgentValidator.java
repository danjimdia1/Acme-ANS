
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.realms.AssistanceAgent;

public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

	@Override
	protected void initialise(final ValidAssistanceAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AssistanceAgent agent, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (agent == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String initials = "";
			String name = agent.getIdentity().getName();
			String surname = agent.getIdentity().getSurname();
			String[] surnameParts = surname.split(" ");

			initials += name.charAt(0);
			initials += surnameParts[0].charAt(0);
			if (surnameParts.length > 1)
				initials += surnameParts[1].charAt(0);

			boolean validEmployeeCode = agent.getEmployeeCode().matches("^" + initials + "\\d{6}$");

			super.state(context, validEmployeeCode, "employeeCode", "java.validation.assistanceAgent.identifier.message");

		}

		result = !super.hasErrors(context);

		return result;

	}

}

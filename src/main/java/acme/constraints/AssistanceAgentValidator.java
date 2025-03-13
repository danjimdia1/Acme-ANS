
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.AssistanceAgent;

@Validator
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
			String name = agent.getIdentity().getName().toUpperCase();
			String surname = agent.getIdentity().getSurname().toUpperCase();

			initials += name.charAt(0);
			initials += surname.charAt(0);

			String code = agent.getEmployeeCode();

			boolean validEmployeeCode = code.startsWith(initials);

			super.state(context, validEmployeeCode, "employeeCode", "java.validation.assistanceAgent.identifier.message");

		}

		result = !super.hasErrors(context);

		return result;

	}

}

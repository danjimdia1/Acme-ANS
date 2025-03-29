
package acme.constraints;

import java.util.Optional;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.realms.assistanceAgent.AssistanceAgent;
import acme.realms.assistanceAgent.AssistanceAgentRepository;

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

		else if (!StringHelper.isBlank(agent.getEmployeeCode())) {
			String initials = "";
			String name = agent.getIdentity().getName();
			String surname = agent.getIdentity().getSurname();

			initials += name.charAt(0);
			initials += surname.charAt(0);

			boolean validEmployeeCode = StringHelper.startsWith(agent.getEmployeeCode(), initials, true);
			super.state(context, validEmployeeCode, "employeeCode", "java.validation.assistanceAgent.employeeCode.invalidEmployeeCode");

			Optional<AssistanceAgent> sameCode = SpringHelper.getBean(AssistanceAgentRepository.class).findByCode(agent.getEmployeeCode(), agent.getId());
			boolean repeatedCode = !sameCode.isPresent();
			super.state(context, repeatedCode, "employeeCode", "java.validation.assistanceAgent.employeeCode.repeatedEmployeeCode");
		}

		result = !super.hasErrors(context);

		return result;

	}

}

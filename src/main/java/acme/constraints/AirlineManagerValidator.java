
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.realms.airlineManager.AirlineManager;
import acme.realms.airlineManager.AirlineManagerRepository;

@Validator
public class AirlineManagerValidator extends AbstractValidator<ValidAirlineManager, AirlineManager> {

	@Override
	protected void initialise(final ValidAirlineManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AirlineManager airlineManager, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (airlineManager == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			{
				if (!StringHelper.isBlank(airlineManager.getIdentifier())) {
					String initials = "";
					String name = airlineManager.getUserAccount().getIdentity().getName();
					String surname = airlineManager.getUserAccount().getIdentity().getSurname();

					initials += name.charAt(0);
					initials += surname.charAt(0);

					boolean validIdentifier = StringHelper.startsWith(airlineManager.getIdentifier(), initials, true);

					super.state(context, validIdentifier, "identifier", "java.validation.airlineManager.validIdentifier.invalid-identifier");
				}
			}
			{
				AirlineManagerRepository repository;
				repository = SpringHelper.getBean(AirlineManagerRepository.class);

				Boolean repeatedIdentifier = repository.findByIdentifier(airlineManager.getIdentifier(), airlineManager.getId()).isEmpty();

				super.state(context, repeatedIdentifier, "identifier", "java.validation.airlineManager.repeatedIdentifier.identifier.message");

			}
		}

		result = !super.hasErrors(context);

		return result;
	}

}

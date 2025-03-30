
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.SpringHelper;
import acme.entities.airlines.Airline;
import acme.entities.airlines.AirlineRepository;

@Validator
public class AirlineValidator extends AbstractValidator<ValidAirline, Airline> {

	@Override
	protected void initialise(final ValidAirline annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airline airline, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (airline == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String iataCode = airline.getIATA();

			if (iataCode == null || iataCode.isBlank() || !iataCode.matches("^[A-Z]{3}$"))
				super.state(context, false, "identifier", "java.validation.airline.identifier.identifier-couldnt-be-blank");

			{
				AirlineRepository repository;
				repository = SpringHelper.getBean(AirlineRepository.class);
				boolean repeatedAirlineIATA = repository.findByIATA(airline.getIATA(), airline.getId()).isEmpty();
				super.state(context, repeatedAirlineIATA, "identifier", "java.validation.airline.repeatedflightNumber.flightNumber.message");
			}
		}

		result = !super.hasErrors(context);

		return result;
	}
}

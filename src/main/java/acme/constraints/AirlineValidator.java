
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
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
			if (!StringHelper.isBlank(airline.getIATA())) {

				AirlineRepository repository;
				repository = SpringHelper.getBean(AirlineRepository.class);
				boolean repeatedAirlineIATA = repository.findByIATA(airline.getIATA(), airline.getId()).isEmpty();
				super.state(context, repeatedAirlineIATA, "IATA", "acme.validation.airline.repeatedAirlineIATA.message");
			}
			if (!StringHelper.matches(airline.getIATA(), "^[A-Z]{3}"))
				super.state(context, false, "IATA", "acme.validation.airline.iata.wrong-pattern.message");
		}

		result = !super.hasErrors(context);

		return result;
	}
}

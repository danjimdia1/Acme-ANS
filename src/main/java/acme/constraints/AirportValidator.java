
package acme.constraints;

import java.util.Optional;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.entities.airport.Airport;
import acme.entities.airport.AirportRepository;

@Validator
public class AirportValidator extends AbstractValidator<ValidAirport, Airport> {

	@Override
	protected void initialise(final ValidAirport annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airport airport, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (airport == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		else if (!StringHelper.isBlank(airport.getIataCode())) {
			Optional<Airport> sameCode = SpringHelper.getBean(AirportRepository.class).findByCode(airport.getIataCode(), airport.getId());
			boolean repeatedCode = !sameCode.isPresent();
			super.state(context, repeatedCode, "employeeCode", "java.validation.assistanceAgent.employeeCode.repeatedIATACode");
		}

		result = !super.hasErrors(context);

		return result;

	}

}

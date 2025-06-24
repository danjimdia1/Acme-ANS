
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftRepository;

@Validator
public class AircraftValidator extends AbstractValidator<ValidAircraft, Aircraft> {

	@Override
	protected void initialise(final ValidAircraft annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Aircraft aircraft, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (aircraft == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		String registrationNumber = aircraft.getRegistrationNumber();
		if (!StringHelper.isBlank(registrationNumber) && SpringHelper.getBean(AircraftRepository.class).findByRegistrationNumberAndNotAircraftId(registrationNumber, aircraft.getId()).isPresent())
			super.state(context, false, "registrationNumber", "acme.validation.registrationNumber.not-unique");
		result = !super.hasErrors(context);
		return result;
	}

}

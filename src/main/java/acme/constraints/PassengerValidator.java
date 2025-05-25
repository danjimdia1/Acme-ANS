
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.passengers.Passenger;

@Validator
public class PassengerValidator extends AbstractValidator<ValidPassenger, Passenger> {

	@Override
	protected void initialise(final ValidPassenger annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Passenger passenger, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (passenger == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (passenger.getDateOfBirth() == null)
			super.state(context, false, "dateOfBirth", "acme.validation.dateOfBirth.null-or-invalid");
		else if (passenger.getDateOfBirth() != null && !MomentHelper.isBeforeOrEqual(passenger.getDateOfBirth(), MomentHelper.getCurrentMoment()))
			super.state(context, false, "dateOfBirth", "acme.validation.dateOfBirth.notPast.message");

		result = !super.hasErrors(context);

		return result;
	}

}

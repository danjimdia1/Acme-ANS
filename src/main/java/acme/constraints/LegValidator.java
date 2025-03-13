
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.legs.Leg;

@Validator
public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (leg == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			{
				String airlineIataCode = leg.getAircraft().getAirline().getIATA();
				boolean validFlightNumber = leg.getFlightNumber().startsWith(airlineIataCode) && leg.getFlightNumber().length() == 7;

				super.state(context, validFlightNumber, "*", "java.validation.leg.flightNumber.message");
			}
			{

				boolean validScheduledArrival = MomentHelper.isAfter(leg.getScheduledArrival(), leg.getScheduledDeparture());

				super.state(context, validScheduledArrival, "*", "java.validation.leg.scheduledArrival.message");
			}
		}
		result = !super.hasErrors(context);

		return result;
	}
}

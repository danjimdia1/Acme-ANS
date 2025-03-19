
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;

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
				if (StringHelper.isBlank(leg.getFlightNumber()))
					super.state(context, false, "*", "java.validation.leg.flightNumber.flightNumber-couldnt-be-blank");

				String airlineIataCode = leg.getAircraft().getAirline().getIATA();
				boolean validFlightNumber = StringHelper.startsWith(leg.getFlightNumber(), airlineIataCode, true);

				super.state(context, validFlightNumber, "*", "java.validation.leg.flightNumber.message");
			}
			{

				if (leg.getScheduledArrival() == null || leg.getScheduledDeparture() == null)
					super.state(context, false, "*", "java.validation.leg.scheduledDates.scheduled-dates-couldnt-be-null");

				boolean validScheduledArrival = MomentHelper.isAfter(leg.getScheduledArrival(), leg.getScheduledDeparture());

				super.state(context, validScheduledArrival, "*", "java.validation.leg.scheduledArrival.message");
			}
			{
				LegRepository repository;
				repository = SpringHelper.getBean(LegRepository.class);
				boolean repeatedflightNumber = repository.findByFlightNumber(leg.getFlightNumber(), leg.getId()).isEmpty();
				super.state(context, repeatedflightNumber, "identifier", "java.validation.leg.repeatedflightNumber.flightNumber.message");
			}

		}
		result = !super.hasErrors(context);

		return result;
	}
}

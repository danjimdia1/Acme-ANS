
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.SpringHelper;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;

@Validator
public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	@Override
	protected void initialise(final ValidFlight annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Flight flight, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (flight == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			LegRepository repository;
			repository = SpringHelper.getBean(LegRepository.class);
			List<Leg> legs = repository.findByFlightIdOrdered(flight.getId());

			if (legs != null)
				for (int i = 0; i < legs.size() - 1; i++) {
					Leg currentLeg = legs.get(i);
					Leg nextLeg = legs.get(i + 1);

					if (MomentHelper.isAfter(currentLeg.getScheduledArrival(), nextLeg.getScheduledDeparture()))
						super.state(context, false, "*", "acme.validation.flight.legs.sequential-order");
				}
		}
		result = !super.hasErrors(context);

		return result;
	}
}

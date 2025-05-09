
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.entities.aircraft.Status;
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
			if (!StringHelper.isBlank(leg.getFlightNumber()))
				if (leg.getFlight() != null) {
					String airlineIataCode = leg.getFlight().getManager().getAirline().getIATA();
					boolean validFlightNumber = StringHelper.startsWith(leg.getFlightNumber(), airlineIataCode, true);
					super.state(context, validFlightNumber, "flightNumber", "acme.validation.leg.flightNumber.validFlightNumber.message");
				}
			if (leg.getScheduledDeparture() != null) {
				boolean isAfterCurrent = MomentHelper.isAfter(leg.getScheduledDeparture(), MomentHelper.getCurrentMoment());
				super.state(context, isAfterCurrent, "scheduledDeparture", "acme.validation.leg.scheduledArrival.must-be-after-current-moment.message");
			}
			if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
				boolean validScheduledArrival = MomentHelper.isAfter(leg.getScheduledArrival(), leg.getScheduledDeparture());
				super.state(context, validScheduledArrival, "scheduledArrival", "acme.validation.leg.scheduledArrival.must-be-after.message");
			}

			if (!StringHelper.isBlank(leg.getFlightNumber())) {
				LegRepository repository = SpringHelper.getBean(LegRepository.class);
				boolean repeatedFlightNumber = repository.findByFlightNumber(leg.getFlightNumber(), leg.getId()).isEmpty();
				super.state(context, repeatedFlightNumber, "flightNumber", "acme.validation.leg.flightNumber.repeatedflightNumber.message");
			}
			if (leg.getAircraft() != null)
				if (leg.getAircraft().getStatus().equals(Status.MAINTENANCE))
					super.state(context, false, "aircraft", "acme.validation.leg.aircraft.maintenance.message");

			if (leg.getFlight() != null && leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
				LegRepository repository = SpringHelper.getBean(LegRepository.class);
				List<Leg> otherLegsSameFlight = repository.findByFlightId(leg.getFlight().getId(), leg.getId());

				for (Leg otherLeg : otherLegsSameFlight)
					if (otherLeg.getScheduledArrival() != null && otherLeg.getScheduledDeparture() != null) {
						boolean isOverlapping = !(MomentHelper.isBefore(leg.getScheduledArrival(), otherLeg.getScheduledDeparture()) || MomentHelper.isAfter(leg.getScheduledDeparture(), otherLeg.getScheduledArrival()));

						if (isOverlapping)
							super.state(context, false, "scheduledArrival", "acme.validation.leg.flight.overlapping.message");
					}
			}

			if (leg.getAircraft() != null && leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
				LegRepository repository = SpringHelper.getBean(LegRepository.class);
				List<Leg> otherLegs = repository.findByAircraftId(leg.getAircraft().getId(), leg.getId());
				for (Leg otherLeg : otherLegs)
					if (otherLeg.getScheduledArrival() != null && otherLeg.getScheduledDeparture() != null) {
						boolean isOverlapping = MomentHelper.isBefore(leg.getScheduledDeparture(), otherLeg.getScheduledArrival()) && MomentHelper.isAfter(leg.getScheduledArrival(), otherLeg.getScheduledDeparture());
						if (isOverlapping)
							super.state(context, false, "aircraft", "acme.validation.leg.aircraft.overlapping.message");
					}
			}

			if (leg.getDepartureAirport() != null && leg.getArrivalAirport() != null) {
				boolean sameAirport = leg.getDepartureAirport().equals(leg.getArrivalAirport());
				if (sameAirport)
					super.state(context, false, "arrivalAirport", "acme.validation.leg.aircraft.no-same-airport.message");
			}
		}

		result = !super.hasErrors(context);

		return result;
	}
}

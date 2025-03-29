
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogRepository;

@Validator
public class ClaimValidator extends AbstractValidator<ValidClaim, Claim> {

	@Autowired
	private TrackingLogRepository trackingLogRepository;


	@Override
	protected void initialise(final ValidClaim annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Claim claim, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (claim == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		else {
			List<TrackingLog> trackingLogs = this.trackingLogRepository.findAllTrackingLogsByClaimId(claim.getId());

			for (int i = 0; i < trackingLogs.size() - 1; i++) {
				TrackingLog currentLog = trackingLogs.get(i);
				TrackingLog nextLog = trackingLogs.get(i + 1);

				boolean correctPercentage = currentLog.getResolutionPercentage() <= nextLog.getResolutionPercentage();
				super.state(context, correctPercentage, "resolutionPercentage", "acme.validation.claim.percentage.message");
			}
		}

		result = !super.hasErrors(context);

		return result;

	}

}

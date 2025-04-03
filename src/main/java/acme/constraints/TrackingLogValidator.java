
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog log, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (log == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		if (MomentHelper.isAfter(log.getCreationMoment(), log.getLastUpdateMoment()))
			super.state(context, false, "moment", "acme.validation.trackingLog.wrongMoments.message");

		if (log.getResolutionPercentage() == 100.00) {
			boolean correctResolution;
			correctResolution = !StringHelper.isBlank(log.getResolution());
			super.state(context, correctResolution, "resolution", "acme.validation.trackingLog.resolution.message");

			boolean correctStatus;
			correctStatus = log.getStatus().equals(TrackingLogStatus.ACCEPTED) || log.getStatus().equals(TrackingLogStatus.REJECTED);
			super.state(context, correctStatus, "status", "acme.validation.trackingLog.status.message");
		}

		else {
			boolean correctPendingStatus;
			correctPendingStatus = log.getStatus().equals(TrackingLogStatus.PENDING);
			super.state(context, correctPendingStatus, "status", "acme.validation.trackingLog.status.message");
		}

		result = !super.hasErrors(context);

		return result;

	}

}

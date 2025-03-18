
package acme.constraints;

import java.util.Optional;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogRepository;
import acme.entities.trackingLogs.TrackingLogStatus;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Autowired
	private TrackingLogRepository trackingLogRepository;


	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog log, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (log == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		Optional<TrackingLog> latestTrackingLogOpt = this.trackingLogRepository.findLastTrackingLogByClaimId(log.getClaim().getId(), log.getId());

		if (log.getResolutionPercentage() == 100.00) {
			boolean correctResolution;
			correctResolution = log.getResolution() != null;
			super.state(context, correctResolution, "resolution", "acme.validation.trackingLog.resolution.message");

			boolean correctStatus;
			correctStatus = log.getStatus().equals(TrackingLogStatus.ACCEPTED) || log.getStatus().equals(TrackingLogStatus.REJECTED);
			super.state(context, correctStatus, "status", "acme.validation.trackingLog.status.message");
		}

		else {
			boolean correctNullResolution;
			correctNullResolution = log.getResolution() == null;
			super.state(context, correctNullResolution, "resolution", "acme.validation.trackingLog.resolution.message");

			boolean correctPendingStatus;
			correctPendingStatus = log.getStatus().equals(TrackingLogStatus.ACCEPTED) || log.getStatus().equals(TrackingLogStatus.REJECTED);
			super.state(context, correctPendingStatus, "status", "acme.validation.trackingLog.status.message");
		}

		if (latestTrackingLogOpt.isPresent()) {
			TrackingLog latestTrackingLog = latestTrackingLogOpt.get();
			boolean correctPercentage;
			correctPercentage = latestTrackingLog.getResolutionPercentage() < log.getResolutionPercentage();
			super.state(context, correctPercentage, "status", "acme.validation.trackingLog.percentage.message");
		}

		result = !super.hasErrors(context);

		return result;

	}

}


package acme.features.crewMember.activityLog;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMActivityLogPublishService extends AbstractGuiService<CrewMember, ActivityLog> {

	@Autowired
	private FlightCMActivityLogRepository repository;


	@Override
	public void authorise() {
		String method = super.getRequest().getMethod();
		boolean status = false;

		if (method.equals("GET"))
			status = false;
		else {
			int activityLogId = super.getRequest().getData("id", int.class);
			ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);

			if (activityLog != null) {
				int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
				boolean authorised = this.repository.thatActivityLogIsOf(activityLogId, crewMemberId);
				boolean authorised1 = this.repository.existsCrewMember(crewMemberId) && authorised;
				status = authorised1 && activityLog != null && activityLog.isDraftMode();
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "typeIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		int activityLogId = activityLog.getId();

		FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLog.getId());
		if (activityLog.getRegistrationMoment() == null || flightAssignment == null)
			return;

		Leg leg = flightAssignment.getLeg();
		if (leg == null || leg.getScheduledArrival() == null)
			return;

		Date activityLogMoment = activityLog.getRegistrationMoment();
		boolean activityLogMomentIsAfterscheduledArrival = this.repository.associatedWithCompletedLeg(activityLogId, activityLogMoment);
		super.state(activityLogMomentIsAfterscheduledArrival, "WrongActivityLogDate", "acme.validation.activityLog.wrongMoment.message");

		boolean flightAssignmentIsPublished = this.repository.isFlightAssignmentAlreadyPublishedByActivityLogId(activityLogId);
		super.state(flightAssignmentIsPublished, "activityLog", "acme.validation.ActivityLog.FlightAssignmentNotPublished.message");
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		activityLog.setDraftMode(false);

		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;
		dataset = super.unbindObject(activityLog, "registrationMoment", "typeIncident", "description", "severityLevel", "draftMode");
		dataset.put("draftMode", activityLog.isDraftMode());
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}

}

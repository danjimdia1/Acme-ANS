
package acme.features.crewMember.activityLog;

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
public class FlightCMActivityLogUpdateService extends AbstractGuiService<CrewMember, ActivityLog> {

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
				boolean authorised1 = this.repository.existsCrewMember(crewMemberId);
				boolean authorised = authorised1 && this.repository.thatActivityLogIsOf(activityLogId, crewMemberId);

				status = authorised && activityLog != null && activityLog.isDraftMode();
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
		if (activityLog == null)
			return;

		FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLog.getId());
		if (activityLog.getRegistrationMoment() == null || flightAssignment == null)
			return;

		Leg leg = flightAssignment.getLeg();
		if (leg == null || leg.getScheduledArrival() == null)
			return;

		super.state(this.repository.associatedWithCompletedLeg(activityLog.getId(), MomentHelper.getCurrentMoment()), "WrongActivityLogDate", "acme.validation.activityLog.wrongMoment.message");

	}

	@Override
	public void perform(final ActivityLog activityLog) {
		activityLog.setDraftMode(true);
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());

		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset = super.unbindObject(activityLog, "registrationMoment", "typeIncident", "description", "severityLevel", "draftMode");
		dataset.put("readonly", false);
		dataset.put("draftMode", activityLog.isDraftMode());

		super.getResponse().addData(dataset);
	}

}

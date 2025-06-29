
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
		int activityLogId = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		boolean authorised1 = this.repository.existsCrewMember(crewMemberId);
		boolean authorised2 = authorised1 && this.repository.thatActivityLogIsOf(activityLogId, crewMemberId);
		boolean authorised = authorised2 && activityLog != null && activityLog.isDraftMode();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog al) {
		super.bindObject(al, "registrationMoment", "typeIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog al) {
		if (al == null)
			return;
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(al.getId());
		if (al.getRegistrationMoment() == null || flightAssignment == null)
			return;
		Leg leg = flightAssignment.getLeg();
		if (leg == null || leg.getScheduledArrival() == null)
			return;
		boolean activityLogMomentIsAfterscheduledArrival = this.repository.associatedWithCompletedLeg(al.getId(), MomentHelper.getCurrentMoment());
		super.state(activityLogMomentIsAfterscheduledArrival, "WrongActivityLogDate", "acme.validation.activityLog.wrongMoment.message");
	}

	@Override
	public void perform(final ActivityLog al) {
		al.setRegistrationMoment(MomentHelper.getCurrentMoment());
		al.setDraftMode(true);
		this.repository.save(al);
	}

	@Override
	public void unbind(final ActivityLog al) {
		Dataset dataset = super.unbindObject(al, "registrationMoment", "typeIncident", "description", "severityLevel", "draftMode");
		dataset.put("readonly", false);
		dataset.put("draftMode", al.isDraftMode());

		super.getResponse().addData(dataset);
	}

}

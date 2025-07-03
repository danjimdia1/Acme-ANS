
package acme.features.crewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMActivityLogShowService extends AbstractGuiService<CrewMember, ActivityLog> {

	@Autowired
	private FlightCMActivityLogRepository repository;


	@Override
	public void authorise() {
		int activityLogId = super.getRequest().getData("id", int.class);
		boolean authorised = false;
		boolean isHis = false;
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);
		boolean authorised3 = this.repository.existsActivityLog(activityLogId);
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightAssignment fa = this.repository.findFlightAssignmentByActivityLogId(activityLogId);
		if (fa != null) {
			boolean authorised2 = this.repository.existsFlightAssignment(fa.getId());

			boolean authorised1 = authorised3 && authorised2 && this.repository.existsCrewMember(crewMemberId);
			authorised = authorised1 && this.repository.thatActivityLogIsOf(activityLogId, crewMemberId);
			isHis = fa.getCrewMember().getId() == crewMemberId;
		}

		super.getResponse().setAuthorised(authorised && activityLog != null && isHis);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLog.getId());

		Dataset dataset = super.unbindObject(activityLog, "registrationMoment", "typeIncident", "description", "severityLevel", "draftMode");
		dataset.put("id", activityLog.getId());
		dataset.put("masterId", flightAssignment.getId());
		dataset.put("draftMode", activityLog.isDraftMode());
		dataset.put("masterDraftMode", flightAssignment.isDraftMode());
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}

}

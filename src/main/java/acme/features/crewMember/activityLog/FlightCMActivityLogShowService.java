
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
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);
		FlightAssignment flightAssignament = this.repository.findFlightAssignmentByActivityLogId(activityLogId);

		boolean authorised1 = this.repository.existsCrewMember(crewMemberId);
		boolean authorised2 = authorised1 && this.repository.thatActivityLogIsOf(activityLogId, crewMemberId);
		boolean authorised3 = flightAssignament.getCrewMember().getId() == crewMemberId;
		boolean authorised = authorised2 && authorised3 && activityLog != null;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog al) {
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(al.getId());

		Dataset dataset = super.unbindObject(al, "registrationMoment", "typeIncident", "description", "severityLevel", "draftMode");
		dataset.put("id", al.getId());
		dataset.put("masterId", flightAssignment.getId());
		dataset.put("readonly", false);
		dataset.put("draftMode", al.isDraftMode());
		dataset.put("masterDraftMode", flightAssignment.getDraftMode());

		super.getResponse().addData(dataset);
	}

}


package acme.features.crewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMActivityLogDeleteService extends AbstractGuiService<CrewMember, ActivityLog> {

	@Autowired
	private FlightCMActivityLogRepository repository;


	@Override
	public void authorise() {
		String method = super.getRequest().getMethod();
		boolean status;

		if (method.equals("GET"))
			status = false;
		else {
			int activityLogId = super.getRequest().getData("id", int.class);
			ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);

			int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
			boolean authorised1 = this.repository.existsCrewMember(crewMemberId);
			boolean authorised = authorised1 && this.repository.thatActivityLogIsOf(activityLogId, crewMemberId);

			status = authorised && activityLog != null && activityLog.isDraftMode();
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
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.delete(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
	}

}

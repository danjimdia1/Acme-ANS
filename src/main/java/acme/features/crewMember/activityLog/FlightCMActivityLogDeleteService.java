
package acme.features.crewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
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
		int activityLogId = super.getRequest().getData("id", int.class);
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);

		boolean authorised1 = this.repository.thatActivityLogIsOf(activityLogId, crewMemberId);
		boolean authorised2 = authorised1 && this.repository.existsCrewMember(crewMemberId);
		boolean authorised = authorised2 && activityLog.isDraftMode() && activityLog != null;

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
		;
	}

	@Override
	public void perform(final ActivityLog al) {
		this.repository.delete(al);
	}

	@Override
	public void unbind(final ActivityLog al) {
		Dataset dataset = super.unbindObject(al, "registrationMoment", "typeIncident", "description", "severityLevel", "draftMode");
		dataset.put("readonly", false);
		dataset.put("draftMode", al.isDraftMode());

		super.getResponse().addData(dataset);
	}

}

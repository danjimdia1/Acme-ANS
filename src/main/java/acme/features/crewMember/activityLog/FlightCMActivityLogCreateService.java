
package acme.features.crewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMActivityLogCreateService extends AbstractGuiService<CrewMember, ActivityLog> {

	@Autowired
	private FlightCMActivityLogRepository repository;


	@Override
	public void authorise() {
		int masterId = super.getRequest().getData("masterId", int.class);
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(masterId);

		boolean authorised1 = flightAssignment != null;
		boolean authorised2 = authorised1 && this.repository.existsCrewMember(crewMemberId);
		boolean authorised = authorised2 && flightAssignment.getCrewMember().getId() == crewMemberId;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int masterId = super.getRequest().getData("masterId", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(masterId);

		ActivityLog activityLog = new ActivityLog();
		activityLog.setFlightAssignment(flightAssignment);
		activityLog.setTypeIncident("");
		activityLog.setDraftMode(true);
		activityLog.setSeverityLevel(0);
		activityLog.setDescription("");
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());

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
		this.repository.save(al);
	}

	@Override
	public void unbind(final ActivityLog al) {
		int masterId = super.getRequest().getData("masterId", int.class);

		Dataset dataset = super.unbindObject(al, "registrationMoment", "typeIncident", "description", "severityLevel", "draftMode");
		dataset.put("masterId", masterId);
		dataset.put("readonly", false);
		dataset.put("draftMode", al.isDraftMode());
		dataset.put("masterDraftMode", !this.repository.isFlightAssignmentAlreadyPublishedById(masterId));

		super.getResponse().addData(dataset);
	}

}

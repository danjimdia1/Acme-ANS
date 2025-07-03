
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
		boolean status = false;
		int masterId;
		FlightAssignment flightAssignment;

		if (super.getRequest().hasData("masterId", int.class)) {
			masterId = super.getRequest().getData("masterId", int.class);
			int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
			boolean authorised = this.repository.existsCrewMember(crewMemberId);

			flightAssignment = this.repository.findFlightAssignmentById(masterId);
			boolean authorised2 = false;
			if (flightAssignment != null) {
				authorised2 = this.repository.existsFlightAssignment(masterId);
				status = authorised && authorised2;
				boolean isHis = flightAssignment.getCrewMember().getId() == crewMemberId;

				status = status && this.repository.isFlightAssignmentCompleted(MomentHelper.getCurrentMoment(), masterId) && isHis;
			}
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int masterId = super.getRequest().getData("masterId", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(masterId);

		ActivityLog activityLog = new ActivityLog();
		activityLog.setDescription("");
		activityLog.setFlightAssignment(flightAssignment);
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		activityLog.setSeverityLevel(0);
		activityLog.setDraftMode(true);
		activityLog.setTypeIncident("");

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
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		int masterId = super.getRequest().getData("masterId", int.class);

		Dataset dataset = super.unbindObject(activityLog, "registrationMoment", "typeIncident", "description", "severityLevel", "draftMode");
		dataset.put("masterId", masterId);
		dataset.put("masterDraftMode", !this.repository.isFlightAssignmentAlreadyPublishedById(masterId));
		dataset.put("draftMode", activityLog.isDraftMode());
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);

	}

}

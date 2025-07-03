
package acme.features.crewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMActivityLogListService extends AbstractGuiService<CrewMember, ActivityLog> {

	@Autowired
	private FlightCMActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int masterId;
		FlightAssignment flightAssignment;
		if (super.getRequest().hasData("masterId", int.class)) {

			masterId = super.getRequest().getData("masterId", int.class);
			flightAssignment = this.repository.findFlightAssignmentById(masterId);
			if (flightAssignment != null) {

				int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
				boolean authorised = this.repository.existsCrewMember(crewMemberId);

				status = authorised && flightAssignment != null;
				boolean isHis = flightAssignment.getCrewMember().getId() == crewMemberId;
				status = status && this.repository.isFlightAssignmentCompleted(MomentHelper.getCurrentMoment(), masterId) && isHis;
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int masterId = super.getRequest().getData("masterId", int.class);
		Collection<ActivityLog> activityLog = this.repository.findActivityLogsByMasterId(masterId);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset = super.unbindObject(activityLog, "registrationMoment", "typeIncident", "description", "severityLevel", "draftMode");
		super.addPayload(dataset, activityLog, "registrationMoment", "typeIncident");

		int masterId = super.getRequest().getData("masterId", int.class);

		boolean showCreate = this.repository.flightAssignmentAssociatedWithCompletedLeg(masterId, MomentHelper.getCurrentMoment());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
		super.getResponse().addData(dataset);
	}

}

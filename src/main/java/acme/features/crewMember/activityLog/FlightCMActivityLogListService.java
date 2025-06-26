
package acme.features.crewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
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

		int masterId = super.getRequest().getData("masterId", int.class);
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		FlightAssignment flightAssignament = this.repository.findFlightAssignmentById(masterId);

		boolean authorised1 = this.repository.existsCrewMember(crewMemberId);
		boolean authorised2 = authorised1 && flightAssignament != null;
		boolean authorised = flightAssignament.getCrewMember().getId() == crewMemberId && authorised2;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("masterId", int.class);
		Collection<ActivityLog> activityLog = this.repository.findActivityLogsByMasterId(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset = super.unbindObject(activityLog, "registrationMoment", "typeIncident", "description", "severityLevel");
		super.addPayload(dataset, activityLog, "registrationMoment", "typeIncident");

		super.getResponse().addData(dataset);

	}

	@Override
	public void unbind(final Collection<ActivityLog> activityLog) {
		int id = super.getRequest().getData("masterId", int.class);
		boolean show = this.repository.isFlightAssignmentAlreadyPublishedById(id);

		System.out.println("ID:" + id + " del assignament= " + this.repository.isFlightAssignmentAlreadyPublishedById(id));
		System.out.flush();

		super.getResponse().addGlobal("id", id);
		super.getResponse().addGlobal("show", show);
	}

}

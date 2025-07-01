
package acme.features.crewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMAssignmentDeleteService extends AbstractGuiService<CrewMember, FlightAssignment> {

	@Autowired
	private FlightCMAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean authorised = false;
		String method = super.getRequest().getMethod();

		if (method.equals("GET"))
			authorised = false;
		else {
			int id = super.getRequest().getData("id", int.class);
			FlightAssignment fa = this.repository.findFlightAssignmentById(id);
			int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
			if (fa != null) {

				boolean authorised1 = this.repository.existsCrewMemberById(flightCrewMemberId);
				boolean authorised2 = authorised1 && this.repository.thatFlightAssignmentIsOf(id, flightCrewMemberId);
				boolean authorised3 = fa.getCrewMember().getId() == flightCrewMemberId;
				authorised = fa.isDraftMode() && authorised2 && authorised3;
			}
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(id);
		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		;
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		;
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		Collection<ActivityLog> activityLogs = this.repository.findActivityLogsByFlightAssignmentId(assignment.getId());
		this.repository.deleteAll(activityLogs);
		this.repository.delete(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		;
	}
}

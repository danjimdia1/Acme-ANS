
package acme.features.crewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.CurrentStatus;
import acme.entities.flightAssignment.Duty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.crewMember.AvailabilityStatus;
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMAssignmentShowService extends AbstractGuiService<CrewMember, FlightAssignment> {

	@Autowired
	private FlightCMAssignmentRepository repository;


	@Override
	public void authorise() {
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int flightAssignmentId = super.getRequest().getData("id", int.class);

		boolean authorised1 = this.repository.isFlightAssignmentOfCrewMember(flightAssignmentId, crewMemberId);
		boolean authorised = authorised1 && this.repository.existsCrewMemberById(crewMemberId);
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {

		SelectChoices legChoices;
		SelectChoices crewMemberChoices;
		SelectChoices currentStatus;
		SelectChoices duty;

		int flightAssignmentId = super.getRequest().getData("id", int.class);

		Collection<Leg> legs = this.repository.findAllLegs();
		Collection<CrewMember> crewMembers = this.repository.findCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);

		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		crewMemberChoices = SelectChoices.from(crewMembers, "employeeCode", assignment.getCrewMember());
		currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, assignment.getDuty());

		Dataset dataset = super.unbindObject(assignment, "duty", "lastUpdate", "currentStatus", "remarks", "draftMode");
		dataset.put("currentStatus", currentStatus);
		dataset.put("flightCrewMember", crewMemberChoices.getSelected().getKey());
		dataset.put("flightCrewMembers", crewMemberChoices);
		dataset.put("isCompleted", this.repository.isLegCompletedByFlightAssignment(flightAssignmentId, MomentHelper.getCurrentMoment()));
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);

		System.out.println(this.repository.isLegCompletedByFlightAssignment(flightAssignmentId, MomentHelper.getCurrentMoment()));
		super.getResponse().addData(dataset);
	}

}

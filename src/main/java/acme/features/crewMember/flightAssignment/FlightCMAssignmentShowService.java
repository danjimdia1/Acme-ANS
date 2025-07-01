
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
		boolean authorised = false;
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int flightAssignmentId = super.getRequest().getData("id", int.class);

		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		if (flightAssignment != null) {
			boolean existsAssignment = this.repository.existsFlightAssignment(flightAssignmentId);
			boolean existsCrew = this.repository.existsCrewMemberById(crewMemberId);
			boolean isOwner = this.repository.isFlightAssignmentOfCrewMember(flightAssignmentId, crewMemberId);
			boolean matchesEntity = flightAssignment.getCrewMember().getId() == crewMemberId;

			authorised = existsAssignment && existsCrew && isOwner && matchesEntity;
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

		boolean isCompleted = this.repository.isLegCompletedByFlightAssignment(flightAssignmentId, MomentHelper.getCurrentMoment());

		Dataset dataset = super.unbindObject(assignment, "duty", "lastUpdate", "currentStatus", "remarks", "draftMode");
		dataset.put("currentStatus", currentStatus);
		dataset.put("crewMember", crewMemberChoices.getSelected().getLabel());
		dataset.put("crewMembers", crewMemberChoices);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("isCompleted", isCompleted);

		super.getResponse().addData(dataset);
	}
}

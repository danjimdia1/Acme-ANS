
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
public class FlightCMAssignmentPublishService extends AbstractGuiService<CrewMember, FlightAssignment> {

	@Autowired
	private FlightCMAssignmentRepository repository;


	@Override
	public void authorise() {
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		boolean authorised1 = this.repository.isFlightAssignmentOfCrewMember(flightAssignmentId, crewMemberId);
		boolean authorised2 = this.repository.existsCrewMemberById(crewMemberId);
		boolean authorised3 = flightAssignment.getDraftMode() && MomentHelper.isFuture(flightAssignment.getLeg().getScheduledArrival());
		boolean authorised = authorised1 && authorised2 && authorised3;
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment = new FlightAssignment();

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		Integer crewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		CrewMember crewMember = this.repository.findCrewMemberById(crewMemberId);

		Integer legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.bindObject(assignment, "duty", "lastUpdate", "currentStatus", "remarks");
		assignment.setLeg(leg);
		assignment.setCrewMember(crewMember);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		;
	}

	@Override
	public void unbind(final FlightAssignment assignment) {

		SelectChoices currentStatus;
		SelectChoices duty;
		SelectChoices legChoices;
		SelectChoices crewMemberChoices;

		Collection<Leg> legs = this.repository.findAllLegs();
		Collection<CrewMember> crewMembers = this.repository.findCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);

		currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, assignment.getDuty());
		legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		crewMemberChoices = SelectChoices.from(crewMembers, "employeeCode", assignment.getCrewMember());

		Dataset dataset = super.unbindObject(assignment, "duty", "lastUpdate", "CurrentStatus", "remarks", "draftMode");
		dataset.put("confirmation", false);
		dataset.put("flightCrewMember", crewMemberChoices.getSelected().getKey());
		dataset.put("flightCrewMembers", crewMemberChoices);
		dataset.put("duty", duty);
		dataset.put("readonly", false);
		dataset.put("lastUpdate", MomentHelper.getCurrentMoment());
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("currentStatus", currentStatus);

		super.getResponse().addData(dataset);
	}
}


package acme.features.crewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.Duty;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.crewMember.AvailabilityStatus;
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMAssignmentUpdateService extends AbstractGuiService<CrewMember, FlightAssignment> {

	@Autowired
	private FlightCMAssignmentRepository repository;


	@Override
	public void authorise() {
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		boolean authorised1 = this.repository.existsCrewMemberById(crewMemberId);
		boolean authorised2 = authorised1 && this.repository.thatFlightAssignmentIsOf(flightAssignmentId, crewMemberId);
		boolean authorised = authorised2 && flightAssignment != null && flightAssignment.getDraftMode();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		Integer crewMemberId = super.getRequest().getData("crewMember", int.class);
		CrewMember crewMember = this.repository.findCrewMemberById(crewMemberId);

		Integer legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.bindObject(assignment, "duty", "lastUpdate", "currentStatus", "remarks");
		assignment.setCrewMember(crewMember);
		assignment.setLeg(leg);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		;
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assert assignment != null;
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Collection<CrewMember> crewMembers = this.repository.findCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);
		Collection<Leg> legs = this.repository.findAllLegs();

		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		SelectChoices crewMemberChoices = SelectChoices.from(crewMembers, "employeeCode", assignment.getCrewMember());
		SelectChoices currentStatus = SelectChoices.from(acme.entities.flightAssignment.CurrentStatus.class, assignment.getCurrentStatus());
		SelectChoices duty = SelectChoices.from(Duty.class, assignment.getDuty());

		Dataset dataset = super.unbindObject(assignment, "duty", "lastUpdate", "currentStatus", "remarks", "draftMode");
		dataset.put("confirmation", false);
		dataset.put("lastUpdate", MomentHelper.getCurrentMoment());
		dataset.put("crewMember", crewMemberChoices.getSelected().getKey());
		dataset.put("crewMembers", crewMemberChoices);
		dataset.put("duty", duty);
		dataset.put("readonly", false);
		dataset.put("currentStatus", currentStatus);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}
}

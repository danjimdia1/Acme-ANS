
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
public class FlightCMAssignmentCreateService extends AbstractGuiService<CrewMember, FlightAssignment> {

	@Autowired
	private FlightCMAssignmentRepository repository;


	@Override
	public void authorise() {
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean authorised = this.repository.existsCrewMemberById(crewMemberId);
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment = new FlightAssignment();

		flightAssignment.setCurrentStatus(CurrentStatus.PENDING);
		flightAssignment.setDuty(Duty.CABIN_ATTENDANT);
		flightAssignment.setCrewMember(this.repository.findCrewMemberById(super.getRequest().getPrincipal().getActiveRealm().getId()));
		flightAssignment.setLastUpdate(MomentHelper.getCurrentMoment());
		flightAssignment.setRemarks("");
		flightAssignment.setDraftMode(true);

		super.getBuffer().addData(flightAssignment);
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
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		this.repository.save(assignment);
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

		Dataset dataset = super.unbindObject(assignment, "duty", "lastUpdate", "currentStatus", "remarks", "draftMode");
		dataset.put("confirmation", false);
		dataset.put("flightCrewMember", crewMemberChoices.getSelected().getKey());
		dataset.put("flightCrewMembers", crewMemberChoices);
		dataset.put("readonly", false);
		dataset.put("duty", duty);
		dataset.put("lastUpdate", MomentHelper.getBaseMoment());
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("currentStatus", currentStatus);

		super.getResponse().addData(dataset);
	}
}

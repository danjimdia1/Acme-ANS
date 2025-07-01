
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
		boolean authorised1 = this.repository.existsCrewMemberById(crewMemberId);

		boolean authorised2 = true;
		if (super.getRequest().hasData("leg", int.class)) {
			int legId = super.getRequest().getData("leg", int.class);
			if (legId != 0)
				authorised2 = this.repository.existsLeg(legId);
		}

		super.getResponse().setAuthorised(authorised1 && authorised2);
	}

	@Override
	public void load() {
		FlightAssignment assignment = new FlightAssignment();

		assignment.setDraftMode(true);
		assignment.setCurrentStatus(CurrentStatus.PENDING);
		assignment.setDuty(Duty.CABIN_ATTENDANT);
		assignment.setCrewMember(this.repository.findCrewMemberById(super.getRequest().getPrincipal().getActiveRealm().getId()));
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setRemarks("");

		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {

		Integer legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		CrewMember crewMember = this.repository.findCrewMemberById(crewMemberId);

		super.bindObject(assignment, "duty", "currentStatus", "remarks");
		assignment.setLeg(leg);
		assignment.setCrewMember(crewMember);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		CrewMember crewMember = assignment.getCrewMember();
		Leg leg = assignment.getLeg();

		if (crewMember != null && leg != null && this.isLegIncompatible(assignment)) {
			super.state(false, "crewMember", "acme.validation.FlightAssignment.CrewMemberIncompatibleLegs.message");
			return;
		}

		if (leg != null)
			this.checkPilotAndCopilotAssignment(assignment);
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Collection<Leg> legs = this.repository.findAllLegs();
		Collection<CrewMember> crewMembers = this.repository.findCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);

		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		SelectChoices crewMemberChoices = SelectChoices.from(crewMembers, "employeeCode", assignment.getCrewMember());
		SelectChoices currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());
		SelectChoices duty = SelectChoices.from(Duty.class, assignment.getDuty());

		Dataset dataset = super.unbindObject(assignment, "duty", "lastUpdate", "currentStatus", "remarks", "draftMode");
		dataset.put("confirmation", false);
		dataset.put("crewMember", crewMemberChoices.getSelected().getKey());
		dataset.put("crewMemberLabel", crewMemberChoices.getSelected().getLabel());
		dataset.put("crewMembers", crewMemberChoices);
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("readonly", false);
		dataset.put("lastUpdate", MomentHelper.getBaseMoment());
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}

	private boolean isLegIncompatible(final FlightAssignment assignment) {
		Collection<Leg> assignedLegs = this.repository.findLegsByCrewMember(assignment.getCrewMember().getId());
		Leg newLeg = assignment.getLeg();

		return assignedLegs.stream().anyMatch(existingLeg -> !this.legsAreCompatible(newLeg, existingLeg));
	}

	private boolean legsAreCompatible(final Leg newLeg, final Leg oldLeg) {
		return !(MomentHelper.isInRange(newLeg.getScheduledDeparture(), oldLeg.getScheduledDeparture(), oldLeg.getScheduledArrival()) || MomentHelper.isInRange(newLeg.getScheduledArrival(), oldLeg.getScheduledDeparture(), oldLeg.getScheduledArrival()));
	}

	private void checkPilotAndCopilotAssignment(final FlightAssignment assignment) {
		boolean havePilot = this.repository.existsCrewMemberWithDutyInLeg(assignment.getLeg().getId(), Duty.PILOT);
		boolean haveCopilot = this.repository.existsCrewMemberWithDutyInLeg(assignment.getLeg().getId(), Duty.CO_PILOT);

		if (Duty.PILOT.equals(assignment.getDuty()))
			super.state(!havePilot, "duty", "acme.validation.FlightAssignment.havePilot.message");

		if (Duty.CO_PILOT.equals(assignment.getDuty()))
			super.state(!haveCopilot, "duty", "acme.validation.FlightAssignment.haveCopilot.message");
	}
}

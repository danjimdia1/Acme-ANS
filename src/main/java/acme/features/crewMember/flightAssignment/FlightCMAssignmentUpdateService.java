
package acme.features.crewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;

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
public class FlightCMAssignmentUpdateService extends AbstractGuiService<CrewMember, FlightAssignment> {

	@Autowired
	private FlightCMAssignmentRepository repository;


	@Override
	public void authorise() {
		String method = super.getRequest().getMethod();
		boolean authorised = false;
		boolean isHis = false;
		FlightAssignment assignment = null;

		if (!method.equals("GET")) {
			int assignmentId = super.getRequest().getData("id", int.class);
			assignment = this.repository.findFlightAssignmentById(assignmentId);

			int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
			int legId = super.getRequest().getData("leg", int.class);

			boolean legExists = legId == 0 || this.repository.existsLeg(legId);
			boolean memberExists = this.repository.existsCrewMemberById(crewMemberId);
			boolean isOwner = this.repository.thatFlightAssignmentIsOf(assignmentId, crewMemberId);

			isHis = assignment != null && assignment.getCrewMember().getId() == crewMemberId;
			authorised = legExists && memberExists && isOwner;
		}

		super.getResponse().setAuthorised(authorised && assignment != null && assignment.isDraftMode() && isHis);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(id);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		CrewMember crewMember = this.repository.findCrewMemberById(crewMemberId);

		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.bindObject(assignment, "duty", "currentStatus", "remarks");

		assignment.setCrewMember(crewMember);
		assignment.setLeg(leg);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		FlightAssignment original = this.repository.findFlightAssignmentById(assignment.getId());

		boolean dutyChanged = !original.getDuty().equals(assignment.getDuty());
		boolean legChanged = !original.getLeg().equals(assignment.getLeg());
		boolean statusChanged = !original.getCurrentStatus().equals(assignment.getCurrentStatus());

		if (!(dutyChanged || legChanged || statusChanged))
			return;

		Leg leg = assignment.getLeg();
		if (leg != null && legChanged && !this.isLegCompatible(assignment))
			super.state(false, "crewMember", "acme.validation.FlightAssignment.CrewMemberIncompatibleLegs.message");

		if (leg != null && (dutyChanged || legChanged))
			this.checkPilotAndCopilotAssignment(assignment);
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assignment.setLastUpdate(MomentHelper.getCurrentMoment());
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Collection<CrewMember> crewMembers = this.repository.findCrewMembersByAvailability(AvailabilityStatus.AVAILABLE);
		Collection<Leg> legs = this.repository.findAllLegs();

		SelectChoices crewMemberChoices = SelectChoices.from(crewMembers, "employeeCode", assignment.getCrewMember());
		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignment.getLeg());
		SelectChoices duty = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices currentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());

		int assignmentId = super.getRequest().getData("id", int.class);
		Date now = MomentHelper.getCurrentMoment();
		boolean isCompleted = this.repository.isLegCompletedByFlightAssignment(assignmentId, now);

		Dataset dataset = super.unbindObject(assignment, "duty", "lastUpdate", "currentStatus", "remarks", "draftMode");

		dataset.put("readonly", false);
		dataset.put("currentStatus", currentStatus);
		dataset.put("lastUpdate", now);
		dataset.put("crewMember", crewMemberChoices.getSelected().getKey());
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("draftMode", assignment.isDraftMode());
		dataset.put("crewMembers", crewMemberChoices);
		dataset.put("isCompleted", isCompleted);
		dataset.put("duty", duty);

		super.getResponse().addData(dataset);
	}

	private boolean isLegCompatible(final FlightAssignment assignment) {
		Collection<Leg> legsByMember = this.repository.findLegsByCrewMember(assignment.getCrewMember().getId());
		Leg newLeg = assignment.getLeg();

		return legsByMember.stream().allMatch(existingLeg -> !MomentHelper.isInRange(newLeg.getScheduledDeparture(), existingLeg.getScheduledDeparture(), existingLeg.getScheduledArrival())
			&& !MomentHelper.isInRange(newLeg.getScheduledArrival(), existingLeg.getScheduledDeparture(), existingLeg.getScheduledArrival()));
	}

	private void checkPilotAndCopilotAssignment(final FlightAssignment assignment) {
		boolean hasPilot = this.repository.existsCrewMemberWithDutyInLeg(assignment.getLeg().getId(), Duty.PILOT);
		boolean hasCopilot = this.repository.existsCrewMemberWithDutyInLeg(assignment.getLeg().getId(), Duty.CO_PILOT);

		if (Duty.PILOT.equals(assignment.getDuty()))
			super.state(!hasPilot, "duty", "acme.validation.FlightAssignment.havePilot.message");
		if (Duty.CO_PILOT.equals(assignment.getDuty()))
			super.state(!hasCopilot, "duty", "acme.validation.FlightAssignment.haveCopilot.message");
	}
}


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
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMAssignmentPublishService extends AbstractGuiService<CrewMember, FlightAssignment> {

	@Autowired
	private FlightCMAssignmentRepository repository;


	@Override
	public void authorise() {

		String method = super.getRequest().getMethod();
		boolean status;

		if (method.equals("GET"))
			status = false;
		else {
			int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
			int flightAssignamentId = super.getRequest().getData("id", int.class);
			boolean authorised = this.repository.thatFlightAssignmentIsOf(flightAssignamentId, crewMemberId);
			FlightAssignment flightAssignment;
			boolean authorised1 = this.repository.existsCrewMemberById(crewMemberId);
			flightAssignment = this.repository.findFlightAssignmentById(flightAssignamentId);
			int legId = super.getRequest().getData("leg", int.class);
			boolean authorised3 = true;
			if (legId != 0)
				authorised3 = this.repository.existsLeg(legId);

			status = authorised3 && authorised1 && authorised && flightAssignment.isDraftMode() && MomentHelper.isFuture(flightAssignment.getLeg().getScheduledArrival());
			boolean authorised4 = flightAssignment.getCrewMember().getId() == crewMemberId;
			boolean authorised5 = flightAssignment.getDuty() == Duty.LEAD_ATTENDANT;

			status = status && authorised4 && authorised5;

		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment = new FlightAssignment();

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment fa) {

		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		CrewMember crewMember = this.repository.findCrewMemberById(crewMemberId);

		int id = super.getRequest().getData("id", int.class);
		fa.setId(id);

		Integer legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);

		super.bindObject(fa, "duty", "currentStatus", "remarks");
		FlightAssignment or = this.repository.findFlightAssignmentById(fa.getId());

		fa.setLeg(leg);
		fa.setCrewMember(crewMember);
		fa.setLastUpdate(or.getLastUpdate());
	}

	@Override
	public void validate(final FlightAssignment fa) {
		FlightAssignment original = this.repository.findFlightAssignmentById(fa.getId());
		Leg leg = fa.getLeg();
		boolean cambioDuty = !original.getDuty().equals(fa.getDuty());

		boolean cambioLeg = !original.getLeg().equals(fa.getLeg());

		if (leg != null && cambioLeg && !this.isLegCompatible(fa))
			super.state(false, "flightCrewMember", "acme.validation.FlightAssignment.CrewMemberIncompatibleLegs.message");

		if (leg != null && (cambioDuty || cambioLeg))
			this.checkPilotAndCopilotAssignment(fa);

		boolean legCompleted = this.isLegCompleted(leg);
		if (legCompleted)
			super.state(false, "leg", "acme.validation.FlightAssignament.LegAlreadyCompleted.message");
	}

	private boolean isLegCompleted(final Leg leg) {
		return leg != null && leg.getScheduledArrival() != null && leg.getScheduledArrival().before(MomentHelper.getCurrentMoment());
	}

	private boolean isLegCompatible(final FlightAssignment fa) {
		Collection<Leg> legsByMember = this.repository.findLegsByCrewMember(fa.getCrewMember().getId());
		Leg newLeg = fa.getLeg();

		return legsByMember.stream().allMatch(existingLeg -> this.areLegsCompatible(newLeg, existingLeg));
	}

	private boolean areLegsCompatible(final Leg newLeg, final Leg oldLeg) {
		return !(MomentHelper.isInRange(newLeg.getScheduledDeparture(), oldLeg.getScheduledDeparture(), oldLeg.getScheduledArrival()) || MomentHelper.isInRange(newLeg.getScheduledArrival(), oldLeg.getScheduledDeparture(), oldLeg.getScheduledArrival()));
	}

	private void checkPilotAndCopilotAssignment(final FlightAssignment flightAssignament) {
		boolean havePilot = this.repository.existsCrewMemberWithDutyInLeg(flightAssignament.getLeg().getId(), Duty.PILOT);
		boolean haveCopilot = this.repository.existsCrewMemberWithDutyInLeg(flightAssignament.getLeg().getId(), Duty.CO_PILOT);

		if (Duty.PILOT.equals(flightAssignament.getDuty()))
			super.state(!havePilot, "duty", "acme.validation.FlightAssignment.havePilot.message");
		if (Duty.CO_PILOT.equals(flightAssignament.getDuty()))
			super.state(!haveCopilot, "duty", "acme.validation.FlightAssignment.haveCopilot.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignament) {
		flightAssignament.setDraftMode(false);
		flightAssignament.setLastUpdate(MomentHelper.getCurrentMoment());

		this.repository.save(flightAssignament);
	}

	@Override
	public void unbind(final FlightAssignment fa) {

		SelectChoices currentStatus;
		SelectChoices duty;
		SelectChoices legChoices;

		Collection<Leg> legs;
		boolean isCompleted;
		int flightAssignmentId;

		flightAssignmentId = super.getRequest().getData("id", int.class);

		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		isCompleted = this.repository.areLegsCompletedByFlightAssignament(flightAssignmentId, currentMoment);
		Dataset dataset;
		FlightAssignment fa2 = this.repository.findFlightAssignmentById(flightAssignmentId);
		legs = this.repository.findAllLegs();

		legChoices = SelectChoices.from(legs, "flightNumber", fa.getLeg());

		currentStatus = SelectChoices.from(CurrentStatus.class, fa.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, fa.getDuty());

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		CrewMember crewMember = this.repository.findCrewMemberById(flightCrewMemberId);

		dataset = super.unbindObject(fa, "duty", "lastUpdate", "currentStatus", "remarks", "draftMode");
		dataset.put("readonly", false);
		dataset.put("lastUpdate", MomentHelper.getCurrentMoment());
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("flightCrewMember", crewMember.getEmployeeCode());
		dataset.put("isCompleted", isCompleted);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("draftMode", fa2.isDraftMode());

		super.getResponse().addData(dataset);
	}
}

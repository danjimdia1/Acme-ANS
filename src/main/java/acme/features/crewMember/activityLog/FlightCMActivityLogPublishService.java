
package acme.features.crewMember.activityLog;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMActivityLogPublishService extends AbstractGuiService<CrewMember, ActivityLog> {

	@Autowired
	private FlightCMActivityLogRepository repository;


	@Override
	public void authorise() {
		int activityLogId = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(activityLogId);

		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		boolean authorised1 = this.repository.thatActivityLogIsOf(activityLogId, crewMemberId);
		boolean authorised2 = authorised1 && this.repository.existsCrewMember(crewMemberId);
		boolean authorised = authorised2 && activityLog != null && activityLog.isDraftMode();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog al) {
		super.bindObject(al, "registrationMoment", "typeIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog al) {
		int activityLogId = al.getId();

		FlightAssignment flightAssignament = this.repository.findFlightAssignmentByActivityLogId(al.getId());
		if (al.getRegistrationMoment() == null || flightAssignament == null)
			return;
		Leg leg = flightAssignament.getLeg();
		if (leg == null || leg.getScheduledArrival() == null)
			return;
		Date activityLogMoment = al.getRegistrationMoment();
		boolean activityLogMomentIsAfterscheduledArrival = this.repository.associatedWithCompletedLeg(activityLogId, activityLogMoment);
		super.state(activityLogMomentIsAfterscheduledArrival, "WrongActivityLogDate", "acme.validation.activityLog.wrongMoment.message");
		System.out.println("El moment está despues de la fecha de llegada (el flightAssignament se completo)? " + activityLogMomentIsAfterscheduledArrival);
		boolean flightAssignamentIsPublished = this.repository.isFlightAssignmentAlreadyPublishedByActivityLogId(activityLogId);
		System.out.println("Se publico el flightAssignament? " + flightAssignament.isDraftMode() + " lo que devuelve la llamada a db es: " + flightAssignamentIsPublished);
		super.state(flightAssignamentIsPublished, "activityLog", "acme.validation.ActivityLog.FlightAssignamentNotPublished.message");
	}

	@Override
	public void perform(final ActivityLog activityLog) {

		if (this.huboAlgunCambio(activityLog))
			activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		activityLog.setDraftMode(false);
		this.repository.save(activityLog);
	}

	private boolean huboAlgunCambio(final ActivityLog activityLogNuevo) {
		ActivityLog activityLogViejo = this.repository.findActivityLogById(activityLogNuevo.getId());
		boolean cambio = false;
		cambio = !activityLogViejo.getDescription().equals(activityLogNuevo.getDescription()) || activityLogViejo.getSeverityLevel() != activityLogNuevo.getSeverityLevel() || activityLogViejo.getTypeIncident() != activityLogNuevo.getTypeIncident();

		return cambio;
	}

	@Override
	public void unbind(final ActivityLog al) {
		FlightAssignment flightAssignament = this.repository.findFlightAssignmentByActivityLogId(al.getId());

		Dataset dataset = super.unbindObject(al, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode");
		dataset.put("masterId", flightAssignament.getId());
		dataset.put("draftMode", al.isDraftMode());
		System.out.println("Soy publish, el activity log tiene draftMode? " + al.isDraftMode() + " y el flightAssignament? " + flightAssignament.isDraftMode());

		dataset.put("readonly", false);
		dataset.put("masterDraftMode", flightAssignament.isDraftMode());

		super.getResponse().addData(dataset);
	}

}

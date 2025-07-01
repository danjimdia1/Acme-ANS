
package acme.features.crewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.crewMember.CrewMember;

@GuiService
public class FlightCMAssignmentPlannedListService extends AbstractGuiService<CrewMember, FlightAssignment> {

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
		Collection<FlightAssignment> fas;

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		Date currentMoment = MomentHelper.getCurrentMoment();
		fas = this.repository.findAllFAByPlannedLeg(currentMoment, flightCrewMemberId);

		super.getBuffer().addData(fas);
	}

	@Override
	public void unbind(final FlightAssignment fa) {
		Dataset dataset = super.unbindObject(fa, "duty", "lastUpdate", "currentStatus", "remarks", "draftMode");

		super.getResponse().addData(dataset);
	}

}

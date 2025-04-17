
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
		boolean isAuthorised = this.repository.existsCrewMemberById(crewMemberId);

		super.getResponse().setAuthorised(isAuthorised);
	}

	@Override
	public void load() {
		int crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Date now = MomentHelper.getCurrentMoment();

		Collection<FlightAssignment> upcomingAssignments = this.repository.findAllFAByPlannedLeg(now, crewMemberId);

		super.getBuffer().addData(upcomingAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset = super.unbindObject(flightAssignment, "duty", "lastUpdate", "currentStatus", "remarks");

		super.getResponse().addData(dataset);
	}

}


package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimType;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimDeleteService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Claim claim;
		AssistanceAgent agent;
		int claimId;
		boolean status;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);
		agent = claim == null ? null : claim.getAssistanceAgent();
		status = claim != null && claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(agent);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int claimId;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "registrationMoment", "email", "description", "type");
	}

	@Override
	public void validate(final Claim claim) {
		;
	}

	@Override
	public void perform(final Claim claim) {
		Collection<TrackingLog> logs;

		logs = this.repository.findAllTrackingLogsByClaimId(claim.getId());
		this.repository.deleteAll(logs);
		this.repository.delete(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;

		SelectChoices types = SelectChoices.from(ClaimType.class, claim.getType());
		SelectChoices legs = SelectChoices.from(this.repository.findAllPublishedLegs(), "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description", "type", "draftMode");

		dataset.put("types", types);
		dataset.put("leg", legs.getSelected().getKey());
		dataset.put("legs", legs);

		super.getResponse().addData(dataset);
	}

}

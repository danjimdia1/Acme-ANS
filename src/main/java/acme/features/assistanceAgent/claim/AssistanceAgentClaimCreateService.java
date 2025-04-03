
package acme.features.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimType;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimCreateService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		status = super.getRequest().getPrincipal().hasRealm(agent);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		AssistanceAgent agent;

		agent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();

		claim = new Claim();
		claim.setDraftMode(true);
		claim.setAssistanceAgent(agent);
		claim.setRegistrationMoment(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "registrationMoment", "email", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		if (claim.getLeg() == null)
			super.state(false, "leg", "acme.validation.leg.nullLeg.message");
		//else {
		//boolean wrongMoment = MomentHelper.isAfter(claim.getRegistrationMoment(), claim.getLeg().getScheduledArrival());
		//super.state(wrongMoment, "registrationMoment", "acme.validation.registrationMoment.wrongMoment.message");
		//}
	}

	@Override
	public void perform(final Claim claim) {
		this.repository.save(claim);
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

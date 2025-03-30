
package acme.features.airlineManager.leg;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.legs.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiController
public class AirlineManagerLegController extends AbstractGuiController<AirlineManager, Leg> {

	@Autowired
	private AirlineManagerLegListService	listService;

	@Autowired
	private AirlineManagerLegShowService	showService;

	@Autowired
	private AirlineManagerLegCreateService	createService;

	@Autowired
	private AirlineManagerLegPublishService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);

		super.addCustomCommand("publish", "update", this.publishService);
	}
}


package acme.features.airlineManager.leg;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.legs.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiController
public class AirlineManagerController extends AbstractGuiController<AirlineManager, Leg> {

	@Autowired
	private AirlineManagerLegListService listService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
	}
}

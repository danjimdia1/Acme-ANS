
package acme.features.technician.involves;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.involves.Involves;
import acme.realms.technician.Technician;

@GuiController
public class TechnicianInvolvesController extends AbstractGuiController<Technician, Involves> {

	//Internal state --------------------------------------------------------------

	@Autowired
	private TechnicianInvolvesCreateService	createService;

	@Autowired
	private TechnicianInvolvesDeleteService	deleteService;

	//Constructors ----------------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
	}

}

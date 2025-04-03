
package acme.features.technician.dashboard;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.forms.technician.Dashboard;
import acme.realms.technician.Technician;

@GuiController
public class TechnicianDashboardController extends AbstractGuiController<Technician, Dashboard> {

	@Autowired
	private TechnicianDashboardShowService showService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
	}

}

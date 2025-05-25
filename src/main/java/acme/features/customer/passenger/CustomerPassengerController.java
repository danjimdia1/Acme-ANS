
package acme.features.customer.passenger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.passengers.Passenger;
import acme.realms.customer.Customer;

@GuiController
public class CustomerPassengerController extends AbstractGuiController<Customer, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerListService	listService;

	@Autowired
	private CustomerPassengerCreateService	createService;

	@Autowired
	private CustomerPassengerShowService	showService;

	@Autowired
	private CustomerPassengerUpdateService	updateService;

	@Autowired
	private CustomerPassengerPublishService	publishService;

	@Autowired
	private CustomerPassengerDeleteService	deleteService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addBasicCommand("delete", this.deleteService);

	}

}

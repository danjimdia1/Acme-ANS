
package acme.features.crewMember.flightAssignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.crewMember.CrewMember;

@GuiController
public class FlightCMAssignmentController extends AbstractGuiController<CrewMember, FlightAssignment> {

	@Autowired
	private FlightCMAssignmentCompletedListService	listCompletedService;

	@Autowired
	private FlightCMAssignmentPlannedListService	listPlannedService;

	@Autowired
	private FlightCMAssignmentCreateService			createService;

	@Autowired
	private FlightCMAssignmentUpdateService			updateService;

	@Autowired
	private FlightCMAssignmentDeleteService			deleteService;

	@Autowired
	private FlightCMAssignmentPublishService		publishService;

	@Autowired
	private FlightCMAssignmentShowService			showService;


	@PostConstruct
	protected void initialise() {

		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("show", this.showService);

		super.addCustomCommand("planned-list", "list", this.listPlannedService);
		super.addCustomCommand("completed-list", "list", this.listCompletedService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}

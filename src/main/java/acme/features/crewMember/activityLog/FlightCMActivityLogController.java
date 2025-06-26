
package acme.features.crewMember.activityLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.activityLog.ActivityLog;
import acme.realms.crewMember.CrewMember;

@GuiController
public class FlightCMActivityLogController extends AbstractGuiController<CrewMember, ActivityLog> {

	@Autowired
	private FlightCMActivityLogListService		listService;

	@Autowired
	private FlightCMActivityLogCreateService	createService;

	@Autowired
	private FlightCMActivityLogUpdateService	updateService;

	@Autowired
	private FlightCMActivityLogDeleteService	deleteService;

	@Autowired
	private FlightCMActivityLogPublishService	publishService;

	@Autowired
	private FlightCMActivityLogShowService		showService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("show", this.showService);

		super.addCustomCommand("publish", "update", this.publishService);
	}
}

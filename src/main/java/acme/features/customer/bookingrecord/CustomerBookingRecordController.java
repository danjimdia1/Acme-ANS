
package acme.features.customer.bookingrecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.bookingrecords.BookingRecord;
import acme.realms.customer.Customer;

@GuiController
public class CustomerBookingRecordController extends AbstractGuiController<Customer, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRecordListService	listService;

	@Autowired
	private CustomerBookingRecordCreateService	createService;

	@Autowired
	private CustomerBookingRecordShowService	showService;

	@Autowired
	private CustomerBookingRecordUpdateService	updateService;

	@Autowired
	private CustomerBookingRecordPublishService	publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);

	}

}

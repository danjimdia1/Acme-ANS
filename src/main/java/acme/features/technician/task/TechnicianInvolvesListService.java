
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Task;
import acme.features.technician.involves.TechnicianInvolvesRepository;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianInvolvesListService extends AbstractGuiService<Technician, Task> {

	//Internal state ---------------------------------------------

	@Autowired
	private TechnicianInvolvesRepository repository;

	//AbstractGuiService interface -------------------------------


	@Override
	public void authorise() {
		int technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
		super.getResponse().setAuthorised(technicianId == maintenanceRecord.getTechnician().getId());
	}

	@Override
	public void load() {
		Collection<Task> task;
		int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		task = this.repository.findTaskOfMaintenanceRecord(maintenanceRecordId);

		super.getBuffer().addData(task);
	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;

		dataset = super.unbindObject(task, "type", "priority", "published");

		super.addPayload(dataset, task, "type");

		super.getResponse().addData(dataset);
	}
}

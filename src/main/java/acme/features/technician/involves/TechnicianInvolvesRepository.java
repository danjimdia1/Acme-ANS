
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.involves.Involves;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Task;

@Repository
public interface TechnicianInvolvesRepository extends AbstractRepository {

	@Query("SELECT t FROM Task t WHERE t.technician.id = :technicianId ")
	Collection<Task> findAllTaskByTechnicianId(final int technicianId);

	@Query("SELECT t FROM Task t WHERE t.id = :id")
	Task findTaskById(int id);

	@Query("SELECT m FROM MaintenanceRecord m WHERE m.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("SELECT i.task FROM Involves i WHERE i.maintenanceRecord.id = :maintenanceRecordId")
	Collection<Task> findTaskOfMaintenanceRecord(int maintenanceRecordId);

	@Query("SELECT i FROM Involves i WHERE i.maintenanceRecord.id = :maintenanceRecordId AND i.task.id = :taskId")
	Involves findInvolvesByMaintenanceRecordAndTask(int maintenanceRecordId, int taskId);
}

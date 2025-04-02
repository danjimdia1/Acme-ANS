
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.involves.Involves;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Task;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("SELECT m FROM MaintenanceRecord m")
	Collection<MaintenanceRecord> findAllMaintenanceRecords();

	@Query("SELECT m FROM MaintenanceRecord m WHERE m.technician.id = :technicianId ")
	Collection<MaintenanceRecord> findAllMaintenanceRecordByTechnicianId(final int technicianId);

	@Query("SELECT m FROM MaintenanceRecord m WHERE m.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("SELECT a FROM Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("SELECT i.task FROM Involves i WHERE i.maintenanceRecord.id = :id")
	Collection<Task> findAllRelatedTaskWithMaintenanceRecord(int id);

	@Query("SELECT COUNT(i.task) FROM Involves i WHERE i.maintenanceRecord.id = :id AND i.task.published = TRUE")
	Integer findPublishedTaskOfMaintenanceRecord(int id);

	@Query("SELECT i FROM Involves i WHERE i.maintenanceRecord.id = :id")
	Collection<Involves> findInvolvesByMaintenanceRecordId(int id);
}

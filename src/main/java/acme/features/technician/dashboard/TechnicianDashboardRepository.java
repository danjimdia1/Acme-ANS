
package acme.features.technician.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenanceRecord;

@Repository
public interface TechnicianDashboardRepository extends AbstractRepository {

	// 1. Mantenimientos agrupados por estado
	@Query("SELECT mr.status, COUNT(mr) FROM MaintenanceRecord mr WHERE mr.technician.id = :technicianId GROUP BY mr.status")
	List<Object[]> countMaintenanceRecordsByStatus(int technicianId);

	// 2. Próxima inspección más cercana
	@Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.technician.id = :technicianId AND mr.nextInspectionDue > CURRENT_DATE ORDER BY mr.nextInspectionDue ASC")
	List<MaintenanceRecord> findNextInspectionRecord(int technicianId);

	// 3. Aeronaves con más tareas
	@Query("SELECT i.maintenanceRecord.aircraft FROM Involves i WHERE i.task.technician.id = :technicianId GROUP BY i.maintenanceRecord.aircraft ORDER BY COUNT(i.task) DESC")
	List<Aircraft> findTopAircraftsByTasks(int technicianId);

	// 4. Estadísticas de coste
	@Query("SELECT AVG(mr.estimatedCost.amount), MIN(mr.estimatedCost.amount), MAX(mr.estimatedCost.amount), STDDEV(mr.estimatedCost.amount) FROM MaintenanceRecord mr WHERE mr.technician.id = :technicianId AND mr.maintenanceMoment >= :oneYearAgo")
	Object[] findCostStats(Date oneYearAgo, int technicianId);

	// 5. Estadísticas de duración de tareas
	@Query("SELECT COUNT(t), AVG(t.estimatedDuration), MIN(t.estimatedDuration), MAX(t.estimatedDuration), STDDEV(t.estimatedDuration) FROM Task t WHERE t.technician.id = :technicianId")
	Object[] findDurationStats(int technicianId);
}

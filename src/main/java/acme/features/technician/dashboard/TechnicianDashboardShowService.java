
package acme.features.technician.dashboard;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.maintenanceRecord.MaintenanceRecordStatus;
import acme.forms.TechnicianDashboard;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianDashboardShowService extends AbstractGuiService<Technician, TechnicianDashboard> {

	@Autowired
	private TechnicianDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		TechnicianDashboard dashboard = new TechnicianDashboard();
		int technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Date oneYearAgo = MomentHelper.deltaFromCurrentMoment(-1, java.time.temporal.ChronoUnit.YEARS);

		// 1. Mantenimientos por estado
		Map<String, Integer> statusCounts = new HashMap<>();
		for (Object[] entry : this.repository.countMaintenanceRecordsByStatus(technicianId)) {
			MaintenanceRecordStatus status = (MaintenanceRecordStatus) entry[0];
			Long count = (Long) entry[1];
			statusCounts.put(status.name(), count.intValue());
		}
		dashboard.setNumMaintenanceRecordsByStatus(statusCounts);

		// 2. Próxima inspección más cercana
		List<MaintenanceRecord> nextInspections = this.repository.findNextInspectionRecord(technicianId);
		dashboard.setNearestInspectionDue(nextInspections.isEmpty() ? null : nextInspections.get(0).getNextInspectionDue());

		// 3. Top aeronaves por tareas
		List<Aircraft> topAircrafts = this.repository.findTopAircraftsByTasks(technicianId);
		List<String> registrationNumbers = topAircrafts.stream().map(Aircraft::getRegistrationNumber).toList();
		dashboard.setTopAircraftByTasks(registrationNumbers);

		// 4. Estadísticas de coste
		Object[] costStats = this.repository.findCostStats(oneYearAgo, technicianId);
		if (costStats != null && costStats.length == 4) {
			dashboard.setCostAverage((Double) costStats[0]);
			dashboard.setCostMinimum((Double) costStats[1]);
			dashboard.setCostMaximum((Double) costStats[2]);
			dashboard.setCostDeviation((Double) costStats[3]);
		}

		// 5. Estadísticas de duración
		Object[] durationStats = this.repository.findDurationStats(technicianId);
		if (durationStats != null && durationStats.length == 5) {
			dashboard.setDurationAverage((Double) durationStats[1]);
			dashboard.setDurationMinimum(((Number) durationStats[2]).intValue());
			dashboard.setDurationMaximum(((Number) durationStats[3]).intValue());
			dashboard.setDurationDeviation((Double) durationStats[4]);
		}

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final TechnicianDashboard dashboard) {
		Dataset dataset = new Dataset();

		// Estados
		Map<String, Integer> counts = dashboard.getNumMaintenanceRecordsByStatus();
		dataset.put("statusCompleted", counts.getOrDefault("COMPLETED", 0));
		dataset.put("statusPending", counts.getOrDefault("PENDING", 0));
		dataset.put("statusInProgress", counts.getOrDefault("IN_PROGRESS", 0));

		// Próxima inspección
		Date inspection = dashboard.getNearestInspectionDue();
		dataset.put("nearestNextInspection", inspection != null ? inspection.toString() : "---");

		// Aeronaves
		List<String> aircrafts = dashboard.getTopAircraftByTasks();
		String formattedAircrafts = String.join(", ", aircrafts);
		dataset.put("mostTasksAircrafts", formattedAircrafts);

		// Costes
		dataset.put("avgCost", dashboard.getCostAverage());
		dataset.put("minCost", dashboard.getCostMinimum());
		dataset.put("maxCost", dashboard.getCostMaximum());
		dataset.put("devCost", dashboard.getCostDeviation());

		// Duraciones
		dataset.put("avgDur", dashboard.getDurationAverage());
		dataset.put("minDur", dashboard.getDurationMinimum());
		dataset.put("maxDur", dashboard.getDurationMaximum());
		dataset.put("devDur", dashboard.getDurationDeviation());

		super.getResponse().addData(dataset);
	}

}

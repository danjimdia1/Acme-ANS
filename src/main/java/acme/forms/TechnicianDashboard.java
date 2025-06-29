
package acme.forms;

import java.util.Date;
import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianDashboard extends AbstractForm {

	private static final long		serialVersionUID	= 1L;

	// Mantenimientos por estado
	private Map<String, Integer>	numMaintenanceRecordsByStatus;

	// Fecha de la próxima inspección
	private Date					nearestInspectionDue;

	// Top 5 aeronaves con más tareas
	private List<String>			topAircraftByTasks;

	// Estadísticas de coste
	private Double					costAverage;
	private Double					costMinimum;
	private Double					costMaximum;
	private Double					costDeviation;

	// Estadísticas de duración
	private Double					durationAverage;
	private Integer					durationMinimum;
	private Integer					durationMaximum;
	private Double					durationDeviation;
}

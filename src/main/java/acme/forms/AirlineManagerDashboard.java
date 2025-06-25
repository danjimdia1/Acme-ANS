
package acme.forms;

import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.entities.airport.Airport;
import acme.entities.legs.LegStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirlineManagerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	private Integer					ranking;
	private Integer					yearsToRetire;
	private Double					ratioOnTimeDelayedFlights;
	private Airport					mostPopularAirport;
	private Airport					lessPopularAirport;
	private Map<LegStatus, Integer>	legsByStatus;
	private Map<String, Double>		averageFlightCost;
	private Map<String, Double>		minimumFlightCost;
	private Map<String, Double>		maximumFlightCost;
	private Map<String, Double>		standardDeviationFlightCost;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}

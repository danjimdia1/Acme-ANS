
package acme.forms;

import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
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
	private Money					averageFlightCost;
	private Money					minimumFlightCost;
	private Money					maximumFlightCost;
	private Money					standardDeviationFlightCost;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}

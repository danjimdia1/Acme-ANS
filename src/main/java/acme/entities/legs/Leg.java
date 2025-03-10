
package acme.entities.legs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidLeg;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flights.Flight;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidLeg
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory()
	@ValidString(pattern = "^[A-Z]{3}\\d{4}$")
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory()
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledDeparture;

	@Mandatory()
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledArrival;

	@Mandatory()
	@Valid()
	@Automapped()
	private LegStatus			status;

	// Derived attributes -----------------------------------------------------


	@Transient()
	public Double getDuration() {

		long departure = this.getScheduledDeparture().getTime();
		long arrival = this.getScheduledArrival().getTime();

		double durationInHr = (arrival - departure) / 3600000.0; // 3600000 ms = 1 hora

		return durationInHr;
	}

	// Relationships ----------------------------------------------------------


	@Mandatory()
	@Valid()
	@ManyToOne(optional = false)
	private Flight		flight;

	@Mandatory()
	@Valid()
	@ManyToOne(optional = false)
	private Airport		departureAirport;

	@Mandatory()
	@Valid()
	@ManyToOne(optional = false)
	private Flight		arrivalAirport;

	@Mandatory()
	@Valid()
	@ManyToOne(optional = false)
	private Aircraft	aircraft;
}

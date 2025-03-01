
package acme.entities.legs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.flights.Flight;

public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	// De momento vamos a poner el flight number a mano(no se si es un atributo derivado)

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
	@ValidNumber(min = 1, max = 1000, integer = 4, fraction = 0)
	@Automapped()
	private Integer				duration;

	@Mandatory()
	@Valid()
	@Automapped()
	private LegStatus			status;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory()
	@Valid()
	@ManyToOne(optional = false)
	private Flight				flight;

	// Vamos a dejar comentadas las relaciones para cuando se creen Airport y Aircraft

	// @Mandatory()
	// @Valid()
	// @ManyToOne(optional = false)
	// private Airport				departureAirport;

	// @Mandatory()
	// @Valid()
	// @ManyToOne(optional = false)
	// private Flight				arrivalAirport;

	// @Mandatory()
	// @Valid()
	// @ManyToOne(optional = false)
	// private Aircraft				aircraft;
}

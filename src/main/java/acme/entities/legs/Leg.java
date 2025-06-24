
package acme.entities.legs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.MomentHelper;
import acme.constraints.ValidLeg;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flights.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidLeg
@Table(indexes = {
	@Index(columnList = "flightNumber"), //
	@Index(columnList = "scheduledDeparture"), //
	@Index(columnList = "scheduledArrival"), //
	@Index(columnList = "flight_id, scheduledDeparture"), //
	@Index(columnList = "flight_id, scheduledArrival"), //
	@Index(columnList = "draftMode"),
})
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory()
	@ValidString(pattern = "^[A-Z]{3}\\d{4}$")
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory()
	@ValidMoment()
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledDeparture;

	@Mandatory()
	@ValidMoment()
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledArrival;

	@Mandatory()
	@Valid()
	@Automapped()
	private LegStatus			status;

	@Mandatory
	// HINT: @Valid by default.
	@Automapped
	private boolean				draftMode;

	// Derived attributes -----------------------------------------------------


	@Transient()
	public Double getDuration() {
		if (this.scheduledArrival == null || this.scheduledDeparture == null)
			return 0.;
		double duration = MomentHelper.computeDuration(this.scheduledDeparture, this.scheduledArrival).toMinutes() / 60.0;
		return duration;
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
	private Airport		arrivalAirport;

	@Mandatory()
	@Valid()
	@ManyToOne(optional = false)
	private Aircraft	aircraft;
}

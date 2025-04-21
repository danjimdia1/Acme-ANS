
package acme.entities.bookings;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidBooking;
import acme.entities.flights.Flight;
import acme.realms.customer.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidBooking
public class Booking extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Valid
	@Automapped
	private TravelClass			travelClass;

	@Optional
	@ValidString(pattern = "^[0-9]{4}$")
	@Automapped
	private String				lastNibble;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Customer			customer;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight				flight;


	@Transient
	public Money getPrice() {
		Money price = new Money();
		if (this.flight == null || this.flight.getCost() == null) {
			price.setAmount(0.0);
			price.setCurrency("EUR");
			return price;
		}
		price.setAmount(this.flight.getCost().getAmount() * SpringHelper.getBean(BookingRepository.class).getNumberPassengersOfBooking(this.getId()));
		price.setCurrency(this.flight.getCost().getCurrency());
		return price;
	}

}


package acme.entities.flights;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.realms.AirlineManager;
import lombok.Getter;
import lombok.Setter;

@Entity()
@Getter()
@Setter()
public class Flight extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory()
	@ValidString(min = 1, max = 50)
	@Automapped()
	private String				tag;

	@Mandatory()
	@Valid()
	@Automapped()
	private FlightSelfTransfer	indication;

	@Mandatory()
	@ValidMoney(min = 0.00, max = 1000000.00)
	@Automapped()
	private Money				cost;

	@Optional()
	@ValidString()
	@Automapped()
	private String				description;

	// Derived attributes -----------------------------------------------------

	// Atributos que vienen de la entidad Leg
	// private Date scheduledDeparture;
	// private Date scheduledArrival;
	// private String origin;
	// private String destination;
	// private Integer lavoyers;

	// Relationships ----------------------------------------------------------

	@Mandatory()
	@Valid()
	@ManyToOne(optional = false)
	private AirlineManager		manager;

}

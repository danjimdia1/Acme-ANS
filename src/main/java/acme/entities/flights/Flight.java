
package acme.entities.flights;

import java.beans.Transient;
import java.util.Date;

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
import acme.client.helpers.SpringHelper;
import acme.entities.legs.LegRepository;
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

	// private String origin;
	// private String destination;


	@Transient()
	public Date getScheduledDeparture() {
		Date result;
		LegRepository repository;
		Date wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findFirstScheduledDeparture(this.getId()).get(0);
		result = wrapper;

		return result;
	}

	@Transient()
	public Date getScheduledArrival() {
		Date result;
		LegRepository repository;
		Date wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLastScheduledArrival(this.getId()).get(0);
		result = wrapper;

		return result;
	}

	@Transient()
	public Integer getNumberOfLavoyers() {
		Integer result;
		LegRepository repository;
		Integer wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.numberOfLavoyers(this.getId());
		result = wrapper == null ? 0 : wrapper.intValue();

		return result;
	}

	// Relationships ----------------------------------------------------------


	@Mandatory()
	@Valid()
	@ManyToOne(optional = false)
	private AirlineManager manager;

}

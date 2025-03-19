
package acme.entities.flights;

import java.beans.Transient;
import java.util.Date;
import java.util.List;

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
import acme.realms.airlineManager.AirlineManager;
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
	private FlightSelfTransfer	selftTransfer;

	@Mandatory()
	@ValidMoney(min = 0.00, max = 1000000.00)
	@Automapped()
	private Money				cost;

	@Optional()
	@ValidString()
	@Automapped()
	private String				description;

	// Derived attributes -----------------------------------------------------


	@Transient()
	public Date getScheduledDeparture() {
		Date result;
		LegRepository repository;
		List<Date> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findFirstScheduledDeparture(this.getId());
		result = wrapper == null ? null : wrapper.get(0);

		return result;
	}

	@Transient()
	public Date getScheduledArrival() {
		Date result;
		LegRepository repository;
		List<Date> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLastScheduledArrival(this.getId());
		result = wrapper == null ? null : wrapper.get(0);

		return result;
	}

	public String getOriginCity() {
		String result;
		LegRepository repository;
		List<String> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findFirstOriginCity(this.getId());
		result = wrapper == null ? "" : wrapper.get(0);

		return result;
	}

	public String getDestinationCity() {
		String result;
		LegRepository repository;
		List<String> wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLastDestinationCity(this.getId());
		result = wrapper == null ? "" : wrapper.get(0);

		return result;
	}

	@Transient()
	public Integer getNumberOfLayovers() {
		Integer result;
		LegRepository repository;
		Integer wrapper;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.numberOfLavoyers(this.getId());
		result = wrapper == null ? 0 : wrapper - 1; // El numero de lavoyers es el total menos 1

		return result;
	}

	// Relationships ----------------------------------------------------------


	@Mandatory()
	@Valid()
	@ManyToOne(optional = false)
	private AirlineManager manager;

}

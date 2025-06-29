
package acme.entities.flights;

import java.beans.Transient;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidFlight;
import acme.entities.legs.LegRepository;
import acme.realms.airlineManager.AirlineManager;
import lombok.Getter;
import lombok.Setter;

@Entity()
@Getter()
@Setter()
@ValidFlight()
@Table(indexes = {
	@Index(columnList = "draftMode"), //
	@Index(columnList = "cost_currency, manager_id")
})
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
	private FlightSelfTransfer	selfTransfer;

	@Mandatory()
	@ValidMoney(min = 0.00, max = 1000000.00)
	@Automapped()
	private Money				cost;

	@Optional()
	@ValidString()
	@Automapped()
	private String				description;

	@Mandatory
	// HINT: @Valid by default.
	@Automapped
	private boolean				draftMode;

	// Derived attributes -----------------------------------------------------


	@Transient()
	public Date getScheduledDeparture() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		List<Date> departures = repository.findFirstScheduledDeparture(this.getId());
		return departures.isEmpty() ? null : departures.get(0);
	}

	@Transient()
	public Date getScheduledArrival() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		List<Date> arrivals = repository.findLastScheduledArrival(this.getId());
		return arrivals.isEmpty() ? null : arrivals.get(0);
	}

	@Transient()
	public String getOriginCity() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		List<String> origins = repository.findFirstOriginCity(this.getId());
		return origins.isEmpty() ? "Desconocido" : origins.get(0);
	}

	@Transient()
	public String getDestinationCity() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		List<String> destinations = repository.findLastDestinationCity(this.getId());
		return destinations.isEmpty() ? "Desconocido" : destinations.get(0);
	}

	@Transient()
	public Integer getNumberOfLayovers() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		Integer layovers = repository.numberOfLavoyers(this.getId());
		return layovers == null || layovers == 0 ? 0 : layovers - 1;
	}

	@Transient()
	public String getLabel() {
		String origin = this.getOriginCity();
		String destination = this.getDestinationCity();
		return origin + "-" + destination;
	}
	// Relationships ----------------------------------------------------------


	@Mandatory()
	@Valid()
	@ManyToOne(optional = false)
	private AirlineManager manager;

}

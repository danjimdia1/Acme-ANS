
package acme.entities.maintenanceRecord;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.entities.aircraft.Aircraft;
import acme.realms.technician.Technician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MaintenanceRecord extends AbstractEntity {
	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date					maintenanceMoment;

	@Mandatory
	@Valid
	@Automapped
	private MaintenanceRecordStatus	status;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date					nextInspectionDue;

	@Mandatory
	@ValidMoney //By default -> (min = 0.00, max = 1000000.00)
	@Automapped
	private Money					estimatedCost;

	@Mandatory
	@Valid
	@Automapped
	private Boolean					published;

	// @Optional Attributes -------------------------------------------------------------

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String					notes;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne
	private Technician				technician;

	@Mandatory
	@Valid
	@ManyToOne
	private Aircraft				aircraft;

}

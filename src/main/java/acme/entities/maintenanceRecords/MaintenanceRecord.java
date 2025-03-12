
package acme.entities.maintenanceRecords;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.entities.aircraft.Aircraft;
import acme.realms.Technician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "maintenance_records")
public class MaintenanceRecord extends AbstractEntity {

	// Serialisation identifier
	private static final long serialVersionUID = 1L;


	// Attributes
	public enum Status {
		PENDING, IN_PROGRESS, COMPLETED
	}


	// Attributes
	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date		maintenanceMoment;

	@Mandatory
	@Enumerated(EnumType.STRING)
	private Status		status;

	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	private Date		nextInspectionDue;

	@Mandatory
	@ValidMoney
	private Money		estimatedCost;

	@Optional
	@ValidString(min = 0, max = 255)
	private String		notes;

	// Relationships
	@Mandatory
	@ManyToOne(optional = false)
	private Technician	technician;

	@Mandatory
	@ManyToOne(optional = false)
	private Aircraft	aircraft;
}

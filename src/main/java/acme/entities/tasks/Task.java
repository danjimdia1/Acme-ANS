
package acme.entities.tasks;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.realms.Technician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "task")
public class Task extends AbstractEntity {

	// Serialisation identifier
	private static final long serialVersionUID = 1L;


	// Task type enumeration
	public enum TaskType {
		MAINTENANCE, INSPECTION, REPAIR, SYSTEM_CHECK
	}

	// Attributes


	@Mandatory
	@Enumerated(EnumType.STRING)
	@Automapped
	private TaskType	type;

	@Mandatory
	@ValidString(min = 0, max = 255)
	@Automapped
	private String		description;

	@Mandatory
	@ValidNumber(min = 0, max = 10)
	@Automapped
	private int			priority;

	@Mandatory
	@ValidNumber(min = 0)
	@Automapped
	private Integer		estimatedDuration;

	// Relationships
	@Mandatory
	@ManyToOne(optional = false)
	@Valid
	private Technician	technician;
}


package acme.realms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AirlineManager extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory()
	@ValidString(min = 8, pattern = "^[A-Z]{2-3}\\d{6}$")
	@Column(unique = true)
	private String				identifier;

	@Mandatory()
	@ValidNumber(min = 0, integer = 2, fraction = 0)
	@Automapped()
	private Integer				yearsOfExperience;

	@Mandatory()
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				birthDate;

	@Optional()
	@ValidUrl()
	@Automapped()
	private String				pictureLink;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
}

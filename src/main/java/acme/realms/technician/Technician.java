
package acme.realms.technician;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidIdentifier;
import acme.constraints.ValidPhoneNumber;
import acme.constraints.ValidTechnician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidTechnician
public class Technician extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidIdentifier
	@Column(unique = true)
	private String				licenseNumber;

	@Mandatory
	@ValidPhoneNumber
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				specialisation;

	@Mandatory
	@Automapped
	private boolean				annualHealthTestPassed;

	@Mandatory
	@ValidNumber(min = 0, max = 120)
	@Automapped
	private Integer				yearsOfExperience;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				certifications;

	// Derived attributes -------------------------------------------------------------

	// Relationships ------------------------------------------------------------------

}

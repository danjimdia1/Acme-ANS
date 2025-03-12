
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import acme.client.components.basis.AbstractRole;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "technicians")
public class Technician extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	private String				licenseNumber;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	private String				phoneNumber;

	@Mandatory
	@Column(length = 50)
	private String				specialisation;

	@Mandatory
	private boolean				annualHealthTestPassed;

	@Mandatory
	@Min(0)
	@Max(50) // Suponiendo que no hay técnicos con más de 50 años de experiencia
	private int					yearsOfExperience;

	@Optional
	@Column(length = 255)
	private String				certifications;
}

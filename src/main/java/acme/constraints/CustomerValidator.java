
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.realms.customer.Customer;
import acme.realms.customer.CustomerRepository;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {

	@Override
	protected void initialise(final ValidCustomer annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (customer == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		String identifier = customer.getIdentifier();
		DefaultUserIdentity identity = customer.getUserAccount().getIdentity();

		if (customer != null && StringHelper.isBlank(identifier))
			super.state(context, false, "identifier", "acme.validation.identifier.is-blank");

		String name = identity.getName();
		String surname = identity.getSurname();
		if (customer != null && StringHelper.isBlank(name) && StringHelper.isBlank(surname))
			super.state(context, false, "identifier", "acme.validation.identifier.blank-name-surname");

		if (customer != null && !StringHelper.isBlank(identifier) && !StringHelper.isBlank(name) && !StringHelper.isBlank(surname) && !StringHelper.startsWith(identifier, name.substring(0, 1) + surname.substring(0, 1), true))
			super.state(context, false, "identifier", "java.validation.customer.identifier.invalidPrefix");

		if (customer != null && !StringHelper.isBlank(identifier) && SpringHelper.getBean(CustomerRepository.class).findByIdentifierAnNotCustomerId(identifier, customer.getId()).isPresent())
			super.state(context, false, "identifier", "acme.validation.identifier.not-unique");

		result = !super.hasErrors(context);
		return result;
	}
}

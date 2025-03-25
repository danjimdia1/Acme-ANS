
package acme.constraints;

import java.util.Optional;

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

		if (!StringHelper.isBlank(identifier)) {
			String name = identity.getName();
			String surname = identity.getSurname();
			if (!StringHelper.isBlank(name) && !StringHelper.isBlank(surname)) {
				if (!StringHelper.startsWith(identifier, name.substring(0, 1) + surname.substring(0, 1), true))
					super.state(context, false, "identifier", "java.validation.customer.identifier.invalidPrefix");

				Optional<Customer> foundCustomer = SpringHelper.getBean(CustomerRepository.class).findByIdentifierAnNotCustomerId(identifier, customer.getId());
				if (foundCustomer.isPresent())
					super.state(context, false, "identifier", "acme.validation.identifier.not-unique");
			} else
				super.state(context, false, "identifier", "acme.validation.identifier.blank-name-surname");
		} else
			super.state(context, false, "identifier", "acme.validation.identifier.is-blank");
		result = !super.hasErrors(context);
		return result;
	}
}

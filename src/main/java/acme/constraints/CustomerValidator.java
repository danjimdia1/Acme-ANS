
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.Customer;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {

	@Override
	protected void initialise(final ValidCustomer annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result = true;

		if (customer == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		String identifier = customer.getIdentifier();
		DefaultUserIdentity identity = customer.getUserAccount().getIdentity();

		if (identifier != null && !identifier.isBlank() && identity != null) {
			String name = identity.getName();
			String surname = identity.getSurname();

			if (name != null && !name.isBlank() && surname != null && !surname.isBlank()) {
				String expectedPrefix = (name.substring(0, 1) + surname.substring(0, 1)).toUpperCase();
				String actualPrefix = identifier.substring(0, 2).toUpperCase();

				boolean validPrefix = expectedPrefix.equals(actualPrefix);

				if (!validPrefix) {
					super.state(context, false, "identifier", "java.validation.customer.identifier.invalidPrefix");
					result = false;
				}
			}
		}

		result = result && !super.hasErrors(context);
		return result;
	}
}

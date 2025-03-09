
package acme.constraints;

import java.time.Year;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.services.Service;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	@Override
	protected void initialise(final ValidService annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		if (service == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		String promotionCode = service.getPromotionCode();
		if (promotionCode != null && !promotionCode.isBlank()) {
			String[] parts = promotionCode.split("-");
			if (parts.length == 2) {
				String lastTwoDigits = parts[1];
				int currentYearLastTwoDigits = Year.now().getValue() % 100;

				boolean validYear = lastTwoDigits.equals(String.format("%02d", currentYearLastTwoDigits));

				if (!validYear) {
					super.state(context, false, "promotionCode", "java.validation.service.promotionCode.invalidYear");
					result = false;
				}
			}
		}

		result = result && !super.hasErrors(context);
		return result;
	}
}

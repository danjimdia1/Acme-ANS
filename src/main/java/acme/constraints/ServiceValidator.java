
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.entities.services.Service;
import acme.entities.services.ServiceRepository;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	@Override
	protected void initialise(final ValidService annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {
		boolean result;
		assert context != null;

		if (service == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		String promotionCode = service.getPromotionCode();
		if (StringHelper.isBlank(promotionCode))
			super.state(context, false, "PromotionCode", "acme.validation.promotion-code.is-blank");

		if (!StringHelper.endsWith(promotionCode, String.valueOf(MomentHelper.getCurrentMoment().getYear() % 100), true))
			super.state(context, false, "PromotionCode", "acme.validation.promotion-code.not-current-year");

		if (!StringHelper.isBlank(promotionCode) && SpringHelper.getBean(ServiceRepository.class).findByPromotionCodeAndNotServiceId(promotionCode, service.getId()).isPresent())
			super.state(context, false, "PromotionCode", "acme.validation.promotion-code.not-unique");

		result = !super.hasErrors(context);
		return result;
	}
}

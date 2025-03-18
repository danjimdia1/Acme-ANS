
package acme.constraints;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.services.Service;
import acme.entities.services.ServiceRepository;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	@Autowired
	private ServiceRepository serviceRepository;


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
		else {
			Date currentMoment = MomentHelper.getCurrentMoment();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentMoment);
			Integer currentYear = calendar.get(Calendar.YEAR);
			String last2DigitsYear = String.valueOf(currentYear).substring(2);

			if (service.getPromotionCode() != null && service.getPromotionCode().length() >= 2) {
				String last2DigitsCode = service.getPromotionCode().substring(service.getPromotionCode().length() - 2);
				super.state(context, last2DigitsCode.equals(last2DigitsYear), "PromotionCode", "acme.validation.promotion-code.not-current-year");

				Optional<Service> existingService = this.serviceRepository.findByPromotionCode(service.getPromotionCode());
				if (existingService.isPresent())
					super.state(context, false, "PromotionCode", "acme.validation.promotion-code.not-unique");
			}
		}

		result = !super.hasErrors(context);
		return result;
	}
}


package acme.constraints;

import java.util.Optional;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.SpringHelper;
import acme.client.helpers.StringHelper;
import acme.entities.bookings.Booking;
import acme.entities.bookings.BookingRepository;

@Validator
public class BookingValidator extends AbstractValidator<ValidBooking, Booking> {

	@Override
	protected void initialise(final ValidBooking annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Booking booking, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (booking == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		String locatorCode = booking.getLocatorCode();
		if (StringHelper.isBlank(locatorCode))
			super.state(context, false, "locatorCode", "acme.validation.locatorCode.is-blank");

		Optional<Booking> foundBooking = SpringHelper.getBean(BookingRepository.class).findByLocatorCodeAnNotBookingId(locatorCode, booking.getId());
		if (foundBooking.isPresent())
			super.state(context, false, "locatorCode", "acme.validation.locatorCode.not-unique");

		result = !super.hasErrors(context);
		return result;
	}

}


package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
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

		if (booking != null && StringHelper.isBlank(booking.getLocatorCode()))
			super.state(context, false, "locatorCode", "acme.validation.locatorCode.is-blank");

		if (booking != null && !StringHelper.isBlank(booking.getLocatorCode()) && SpringHelper.getBean(BookingRepository.class).findByLocatorCodeAnNotBookingId(booking.getLocatorCode(), booking.getId()).isPresent())
			super.state(context, false, "locatorCode", "acme.validation.locatorCode.not-unique");

		if (booking != null && booking.getFlight() == null)
			super.state(context, false, "flight", "acme.validation.flight.is-null");

		if (booking != null && booking.getFlight() != null && !SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).isPresent())
			super.state(context, false, "flight", "acme.validation.flight.is-null");

		if (booking != null && booking.getFlight() != null && SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).isPresent()
			&& SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).get().isDraftMode())
			super.state(context, false, "flight", "acme.validation.flight.isDraftMode");

		if (booking != null && booking.getFlight() != null && SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).isPresent()
			&& SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).get().isDraftMode() && SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).get().getScheduledDeparture() == null)
			super.state(context, false, "flight", "acme.validation.flight.null-scheduled-departure");

		if (booking != null && booking.getFlight() != null && SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).isPresent()
			&& SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).get().isDraftMode() && SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).get().getScheduledDeparture() != null
			&& SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).get().getScheduledDeparture().after(MomentHelper.getCurrentMoment()))
			super.state(context, false, "flight", "acme.validation.flight.null-scheduled-departure");

		if (booking != null && booking.getFlight() != null && SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).isPresent()
			&& SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).get().isDraftMode() && SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).get().getScheduledDeparture() != null
			&& SpringHelper.getBean(BookingRepository.class).findFlightById(booking.getFlight().getId()).get().getScheduledDeparture().after(MomentHelper.getCurrentMoment()))
			super.state(context, false, "flight", "acme.validation.flight.null-scheduled-departure");

		result = !super.hasErrors(context);
		return result;
	}

}

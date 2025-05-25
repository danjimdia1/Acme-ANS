
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.SpringHelper;
import acme.entities.bookingrecords.BookingRecord;
import acme.entities.bookingrecords.BookingRecordRepository;

@Validator
public class BookingRecordValidator extends AbstractValidator<ValidBookingRecord, BookingRecord> {

	@Override
	protected void initialise(final ValidBookingRecord annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final BookingRecord bookingRecord, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;
		if (bookingRecord == null)
			super.state(context, false, "*", "acme.validation.booking-record.is-null");
		else if (bookingRecord != null && bookingRecord.getPassenger() == null)
			super.state(context, false, "passenger", "acme.validation.passenger.is-null");
		else if (SpringHelper.getBean(BookingRecordRepository.class).findPassengersByBookingId(bookingRecord.getId()).contains(bookingRecord.getPassenger()))
			super.state(context, false, "passenger", "acme.validation.passenger.already-on-board");
		else if (!SpringHelper.getBean(BookingRecordRepository.class).findUndraftedPassengerByCustomerId(bookingRecord.getBooking().getCustomer().getId()).contains(bookingRecord.getPassenger()))
			super.state(context, false, "passenger", "acme.validation.passenger.is-draftMode");
		result = !super.hasErrors(context);
		return result;
	}

}

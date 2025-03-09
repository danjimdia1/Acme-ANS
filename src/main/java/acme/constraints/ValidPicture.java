
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

@Constraint(validatedBy = {})
@ReportAsSingleViolation

@Length(min = 1, max = 255)
@URL

public @interface ValidPicture {

	// Standard validation properties -----------------------------------------

	String message() default "Picture URL not valid";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}

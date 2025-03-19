
package acme.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Email;

import org.hibernate.validator.constraints.Length;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented

@Constraint(validatedBy = {})
@ReportAsSingleViolation

@Length(min = 0, max = 255)
@Email

public @interface ValidEmailOrNull {

	// Standard validation properties -----------------------------------------

	String message() default "{javax.validation.constraints.Email.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}

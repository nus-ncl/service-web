package sg.ncl.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StartDateNotAfterEndDateValidator.class})
public @interface StartDateNotAfterEndDate {

    String message() default "Start Date must not be after End Date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String start() default "start";
    String end() default  "end";
    String pattern() default "yyyy-MM-dd";
}

package sg.ncl.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class StartDateNotAfterEndDateValidator implements ConstraintValidator<StartDateNotAfterEndDate, Object> {

    private String start;
    private String end;
    private String pattern;
    private DateTimeFormatter formatter;

    @Override
    public void initialize(StartDateNotAfterEndDate constraintAnnotation) {
        this.start = constraintAnnotation.start();
        this.end = constraintAnnotation.end();
        this.pattern = constraintAnnotation.pattern();

        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder().appendPattern(pattern);
        if (!pattern.contains("d")) {
            builder = builder.parseDefaulting(ChronoField.DAY_OF_MONTH, 1);
        }
        this.formatter = builder.toFormatter();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String startValue = (String) (new BeanWrapperImpl(value).getPropertyValue(start));
        String endValue = (String) (new BeanWrapperImpl(value).getPropertyValue(end));

        if (startValue == null || startValue.isEmpty() || endValue == null || endValue.isEmpty()) {
            return true;
        }

        try {
            LocalDate startDate = LocalDate.parse(startValue, formatter);
            LocalDate endDate = LocalDate.parse(endValue, formatter);
            return !startDate.isAfter(endDate);
        } catch (Exception e) {
            e.printStackTrace();
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.toString()).addConstraintViolation();
            return false;
        }
    }
}

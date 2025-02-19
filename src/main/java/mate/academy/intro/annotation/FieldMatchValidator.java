package mate.academy.intro.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mate.academy.intro.dto.UserRegistrationRequestDto;

public class FieldMatchValidator implements
        ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserRegistrationRequestDto dto,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (dto == null) {
            return true;
        }
        return dto.getPassword().equals(dto.getRepeatPassword());
    }
}

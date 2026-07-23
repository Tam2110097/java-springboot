package vn.tamnguyen.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

// định nghĩa 1 interface là 1 annotation
@Documented
//gọi sang PhoneValidator để xử lý
@Constraint(validatedBy = PhoneValidator.class)
// chỉ định annotation có thể áp dụng ở method, field
@Target({ ElementType.METHOD, ElementType.FIELD })
// chạy ở runtime
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {

    String message() default "Invalid phone number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
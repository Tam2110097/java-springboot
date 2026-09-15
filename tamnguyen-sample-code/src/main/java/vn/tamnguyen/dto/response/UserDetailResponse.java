package vn.tamnguyen.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import vn.tamnguyen.dto.validator.PhoneNumber;
import vn.tamnguyen.util.Gender;
import vn.tamnguyen.util.UserStatus;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
public class UserDetailResponse implements Serializable {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private Gender gender;

    private String username;

    private String type;

    private UserStatus status;
}

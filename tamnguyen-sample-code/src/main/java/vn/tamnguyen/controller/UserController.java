package vn.tamnguyen.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import vn.tamnguyen.configuration.Translator;
import vn.tamnguyen.dto.request.SampleDTO;
import vn.tamnguyen.dto.request.UserRequestDTO;
import vn.tamnguyen.dto.response.ResponseData;
import vn.tamnguyen.dto.response.ResponseError;
import vn.tamnguyen.dto.response.ResponseSuccess;
import vn.tamnguyen.service.UserService;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
public class UserController {

    @PostMapping(value = "/")
    public ResponseData<Integer> addUser(@Valid @RequestBody UserRequestDTO userDTO) {
//        System.out.println("Request add user " + userDTO.getFirstName());

        log.info("Request add user {} {}", userDTO.getFirstName(), userDTO.getLastName());
//        SampleDTO dto = SampleDTO.builder()
//                .id(1)
//                .name("TamNguyen")
//                .build();
        return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), 1);
    }

    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int userId, @Valid @RequestBody UserRequestDTO user) {
//        System.out.println("Update user");
        log.info("Request update userId={}", userId);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
    }

    @PatchMapping("/{userId}")
    public ResponseData<?> updateStatus(@Min(1) @PathVariable int userId,
                               @RequestParam boolean status) {
        log.info("Request change status, userId={}", userId);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"));
    }

    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int userId) {
        log.info("Request delete userId={}", userId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.del.success"));
    }

    @GetMapping("/{userId}")
    public ResponseData<UserRequestDTO> getUser(@PathVariable @Min(1) int userId) {
//        System.out.println("Request get userId=" + userId);
        log.info("Request get user detail, userId={}", userId);
        return new ResponseData<>(HttpStatus.OK.value(), "User", new UserRequestDTO("Tam", "Nguyen", "0987654321", "tamnguyen@gmail.com"));
    }

    @GetMapping("/list")
    public ResponseData<List<UserRequestDTO>> getAllUser(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                         @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize) {
//        System.out.println("Request get all of users");
        log.info("Request get all of users");
        return new ResponseData<>(HttpStatus.OK.value(), "users", List.of(new UserRequestDTO("Tam", "Nguyen", "tamnguyen@gmail.com", "0123456789"),
                new UserRequestDTO("Leo", "Messi", "leomessi@email.com", "0123456456")));
    }
}

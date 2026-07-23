package vn.tamnguyen.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn.tamnguyen.dto.request.UserRequestDTO;
import vn.tamnguyen.dto.response.ResponseData;
import vn.tamnguyen.dto.response.ResponseError;
import vn.tamnguyen.dto.response.ResponseSuccess;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/user")
public class UserController {

//    @Operation(summary = "summary", description = "description", responses = {
//            @ApiResponse(responseCode = "201", description = "User added successfully",
//                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                    examples = @ExampleObject(name = "ex name", summary = "ex summary",
//                            value = """
//                                    {
//                                        "data": 1,
//                                        "message": "User added successfully",
//                                        "status": 201
//                                    }
//                                    """
//                    ))
//            )
//    })
//    @RequestMapping(method = POST, path="/", headers = "apiKey=v1.0")
//    @PostMapping(value = "/", headers = "apiKey=v1.0")
    @PostMapping(value = "/")
//    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Integer> addUser(@Valid @RequestBody UserRequestDTO userDTO) {
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Can not create user");
//        return new ResponseData<>(HttpStatus.CREATED.value(), "User added successfully", 1);
    }

    @PutMapping("/{userId}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseData<?> updateUser(@PathVariable @Min(1) int userId, @Valid @RequestBody UserRequestDTO user) {
        System.out.println("Update user");
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User updated successfully");
    }

    @PatchMapping("/{userId}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseData<?> updateStatus(@Min(1) @PathVariable int userId,
                               @RequestParam boolean status) {
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User status changed successfully");
    }

    @DeleteMapping("/{userId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseData<?> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int userId) {
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "User deleted successfully");
    }

    @GetMapping("/{userId}")
//    @ResponseStatus(HttpStatus.OK)
    public ResponseData<UserRequestDTO> getUser(@PathVariable @Min(1) int userId) {
        System.out.println("Request get userId=" + userId);
        return new ResponseData<>(HttpStatus.OK.value(), "User", new UserRequestDTO("Tam", "Nguyen", "0987654321", "tamnguyen@gmail.com"));
    }

    @GetMapping("/list")
//    @ResponseStatus(HttpStatus.OK)
    public ResponseData<List<UserRequestDTO>> getAllUsers(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int pageNo,
            @Min(10)@RequestParam(defaultValue = "20", required = false) int pageSize) {
        System.out.println("Request get all users");
        return new ResponseData<>(HttpStatus.OK.value(), "User", List.of(
                new UserRequestDTO(
                        "Tam",
                        "Nguyen",
                        "phone",
                        "@gmail.com"),
                new UserRequestDTO(
                        "Tam1",
                        "Nguyen1",
                        "phone",
                        "@gmail.com")
        ));
    }
}

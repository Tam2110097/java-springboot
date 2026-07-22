package vn.tamnguyen.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;
import vn.tamnguyen.dto.request.UserRequestDTO;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/user")
public class UserController {

//    @RequestMapping(method = POST, path="/", headers = "apiKey=v1.0")
//    @PostMapping(value = "/", headers = "apiKey=v1.0")
    @PostMapping(value = "/")
    public String addUser(@Valid @RequestBody UserRequestDTO userDTO) {
        return "User added";
    }

    @PutMapping("/{userId}")
    public String updateUser(@PathVariable @Min(1) int userId, @Valid @RequestBody UserRequestDTO user) {
        System.out.println("Update user");
        return "User updated";
    }

    @PatchMapping("/{userId}")
    public String updateStatus(@Min(1) @PathVariable int userId,
                               @RequestParam boolean status) {
        return "User Status changed";
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int userId) {
        return "User deleted";
    }

    @GetMapping("/{userId}")
    public UserRequestDTO getUser(@PathVariable @Min(1) int userId) {
        System.out.println("Request get userId=" + userId);
        return new UserRequestDTO("Tam", "Nguyen", "0987654321", "tamnguyen@gmail.com");
    }

    @GetMapping("/list")
    public List<UserRequestDTO> getAllUsers(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int pageNo,
            @Min(10)@RequestParam(defaultValue = "20", required = false) int pageSize) {
        System.out.println("Request get all users");
        return List.of(
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
        );
    }
}

package vn.tamnguyen.controller;

import org.springframework.web.bind.annotation.*;
import vn.tamnguyen.dto.request.UserRequestDTO;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/user")
public class UserController {

//    @RequestMapping(method = POST, path="/", headers = "apiKey=v1.0")
    @PostMapping(value = "/", headers = "apiKey=v1.0")
    public String addUser(@RequestBody UserRequestDTO userDTO) {
        return "User added";
    }

    @PutMapping("/{userId}")
    public String updateUser(@PathVariable int userId, @RequestBody UserRequestDTO userDTO) {
        System.out.println("Request update userId=" + userId);
        return "User updated";
    }

    @PatchMapping("/{userId}")
    public String changeStatus(@PathVariable int userId, @RequestParam(required = false) boolean status) {
        System.out.println("Request change status userId=" + userId);
        return "User status changed";
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable int userId) {
        System.out.println("Request delete userId=" + userId);
        return "User deleted";
    }

    @GetMapping("/{userId}")
    public UserRequestDTO getUser(@PathVariable int userId) {
        System.out.println("Request get userId=" + userId);
        return new UserRequestDTO("Tam", "Nguyen", "0987654321", "tamnguyen@gmail.com");
    }

    @GetMapping("/list")
    public List<UserRequestDTO> getAllUsers(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
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

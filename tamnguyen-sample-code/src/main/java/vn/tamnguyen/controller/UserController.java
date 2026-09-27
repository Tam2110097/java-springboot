package vn.tamnguyen.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.tamnguyen.configuration.Translator;
import vn.tamnguyen.dto.request.UserRequestDTO;
import vn.tamnguyen.dto.response.*;
import vn.tamnguyen.service.UserService;
import vn.tamnguyen.util.UserStatus;

import static jakarta.servlet.RequestDispatcher.ERROR_MESSAGE;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping(value = "/")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO request) {
        log.info("Request add user {} {}", request.getFirstName(), request.getLastName());

        try {
           long userId = userService.saveUser(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
        }catch (Exception e) {
            log.error("ErrorMessage = {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add user fail");
        }



    }

    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{userId}")
    public ResponseData<Void> updateUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") long userId, @Valid @RequestBody UserRequestDTO request) {
        log.info("Request update userId={}", userId);

        try {
            userService.updateUser(userId, request);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
        }

    }

    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{userId}")
    public ResponseData<Void> updateStatus(@Min(1) @PathVariable int userId,
                               @RequestParam UserStatus status) {
        log.info("Request change status, userId={} userStatus={}", userId, status);

        try {
            userService.changeStatus(userId, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user status fail");
        }


    }

    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{userId}")
    public ResponseData<Void> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") int userId) {
        log.info("Request delete userId={}", userId);

        try {
            userService.deleteUser(userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.del.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete user fail");
        }


    }

    @Operation(summary = "Get user detail", description = "Send a request via this API to get user information")
    @GetMapping("/{userId}")
    public ResponseData<UserDetailResponse> getUser(@PathVariable @Min(1) long userId) {
        log.info("Request get user detail, userId={}", userId);

        log.info("Request get user detail, userId={}", userId);

        try {
            UserDetailResponse user = userService.getUser(userId);
            return new ResponseData<>(HttpStatus.OK.value(), "user", user);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<PageResponse<?>> getAllUsers(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                     @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                     @RequestParam(required = false) String sortBy) {
        log.info("Request get user list, pageNo={}, pageSize={}", pageNo, pageSize);

        try {
            PageResponse<?> users = userService.getAllUsers(pageNo, pageSize, sortBy);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users with sort by multiple columns", description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple columns")
    @GetMapping("/list-with-sort-by-multiple-columns")
    public ResponseData<PageResponse<?>> getAllUsersWithSortByMultipleColumns(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                     @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                     @RequestParam(required = false) String... sorts) {
        log.info("Request get all users with sort by multiple columns, pageNo={}, pageSize={}", pageNo, pageSize);

        try {
            PageResponse<?> users = userService.getAllUsersWithSortByMultipleColumns(pageNo, pageSize, sorts);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users with sort by column and search", description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple columns")
    @GetMapping("/list-with-sort-by-multiple-columns-search")
    public ResponseData<PageResponse<?>> getAllUsersWithSortByColumnAndSearch(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                              @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                                              @RequestParam(required = false) String search,
                                                                              @RequestParam(required = false) String sortBy) {
        log.info("Request get all users with sort by column and search");

        try {
            PageResponse<?> users = userService.getAllUsersWithSortByColumnAndSearch(pageNo, pageSize, search, sortBy);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users with sort by criteria", description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple column")
    @GetMapping("/advance-search-by-criteria")
    public ResponseData<PageResponse<?>> advanceSearchByCriteria(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                 @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                                 @RequestParam(required = false) String sortBy,
                                                                 @RequestParam(required = false) String address,
                                                                 @RequestParam(required = false) String... search) {
        log.info("Request advance search query by criteria");

        try {
            PageResponse<?> users = userService.advanceSearchByCriteria(pageNo, pageSize, sortBy, address,search);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}

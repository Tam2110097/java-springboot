package vn.tamnguyen.service;

import org.springframework.data.domain.Pageable;
import vn.tamnguyen.dto.request.UserRequestDTO;
import vn.tamnguyen.dto.response.PageResponse;
import vn.tamnguyen.dto.response.UserDetailResponse;
import vn.tamnguyen.util.UserStatus;

public interface UserService {

    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus userStatus);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsers(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts);

    PageResponse<?> getAllUsersWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy);

    PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String address, String... search);

    PageResponse<?> advanceSearchWithSpecification(Pageable page, String[] user, String[] address);
}

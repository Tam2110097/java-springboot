package vn.tamnguyen.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.tamnguyen.dto.request.AddressDTO;
import vn.tamnguyen.dto.request.UserRequestDTO;
import vn.tamnguyen.dto.response.PageResponse;
import vn.tamnguyen.dto.response.UserDetailResponse;
import vn.tamnguyen.exception.ResourceNotFoundException;
import vn.tamnguyen.model.Address;
import vn.tamnguyen.model.User;
import vn.tamnguyen.repository.SearchRepository;
import vn.tamnguyen.repository.UserRepository;
import vn.tamnguyen.repository.specification.UserSpec;
import vn.tamnguyen.repository.specification.UserSpecificationBuilder;
import vn.tamnguyen.service.UserService;
import vn.tamnguyen.util.Gender;
import vn.tamnguyen.util.UserStatus;
import vn.tamnguyen.util.UserType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final SearchRepository searchRepository;

    @Override
    public long saveUser(UserRequestDTO request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .addresses(convertToAddress(request.getAddresses()))
                .build();
        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));

        userRepository.save(user);

        log.info("User has saved!");

        return user.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(convertToAddress(request.getAddresses()));
        userRepository.save(user);

        log.info("User has updated successfully, userId={}", userId);

    }

    @Override
    public void changeStatus(long userId, UserStatus userStatus) {
        User user = getUserById(userId);
        user.setStatus(userStatus);
        userRepository.save(user);

        log.info("User status has changed successfully, userId={}", userId);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        log.info("User has deleted permanent successfully, userId={}", userId);
    }

    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .type(user.getType())
                .build();
    }

    @Override
    public PageResponse getAllUsers(int pageNo, int pageSize, String sortBy) {
        // spring boot quy định pageNo phải bằng 0 nhưng khi mong muốn truyền vào pageNo lớn hơn 0 làm như sau
        int p = 0;
        if(pageNo > 0) {
            p = pageNo - 1;
        }

        List<Sort.Order> sorts = new ArrayList<>();

        // nếu có giá trị
        if(StringUtils.hasLength(sortBy)) {
            //firstName:asc|desc

            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);

            if(matcher.find()) {
                if(matcher.group(3).equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }

        }

        Page<User> page = userRepository.findAll(PageRequest.of(p, pageSize, Sort.by(sorts)));

        List<UserDetailResponse> list = page.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .type(user.getType())
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public PageResponse getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts) {
        int p = 0;
        if(pageNo > 0) {
            p = pageNo - 1;
        }

        List<Sort.Order> orders = new ArrayList<>();

        if(sorts != null) {
            for(String sortBy : sorts) {
                //firstName:asc|desc

                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);

                if(matcher.find()) {
                    if(matcher.group(3).equalsIgnoreCase("asc")) {
                        orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                    }
                }
            }
        }

        Page<User> page = userRepository.findAll(PageRequest.of(p, pageSize, Sort.by(orders)));

        List<UserDetailResponse> list = page.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .type(user.getType())
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public PageResponse getAllUsersWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {
        return searchRepository.getAllUsersWithSortByColumnAndSearch(pageNo, pageSize, search, sortBy);
    }

    @Override
    public PageResponse advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        return searchRepository.advanceSearchUser(pageNo, pageSize, sortBy, address, search);
    }

    @Override
    public PageResponse<?> advanceSearchWithSpecification(Pageable pageable, String[] user, String[] address) {

        Page<User> users = null;
        List<User> list = new ArrayList<>();

        if(user != null && address != null) {
            //tim kiem tren ca user va address --> join table

            return searchRepository.getUsersJoinedAddress(pageable, user, address);

        } else if(user != null && address == null) {
            //tim kiem tren bang user khong can join bang address

//            Specification<User> spec = UserSpec.hasFirstName("B");
//            Specification<User> genderSpec = UserSpec.notEqualGender(Gender.MALE);
//            Specification<User> finalSpec = spec.and(genderSpec);

            UserSpecificationBuilder builder = new UserSpecificationBuilder();

            for(String s : user) {
                Pattern pattern = Pattern.compile("(\\w+?)([:<>~!])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
                Matcher matcher = pattern.matcher(s);

                if(matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }

           list = userRepository.findAll(builder.build());
            return PageResponse.builder()
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalPage(10)
                    .items(list)
                    .build();
        } else {
            users = userRepository.findAll(pageable);
        }

        // neu khong nhap dk tim kiem tra lai 1 danh sach co phan trang

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(10)
                .items(users)
                .build();
    }

    /**
     * Covert Set<AddressDTO> to Set<Address>
     *
     * @param addresses
     * @return Set<Address>
     */
    private Set<Address> convertToAddress(Set<AddressDTO> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return result;
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}

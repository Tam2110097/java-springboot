package vn.tamnguyen.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import vn.tamnguyen.dto.response.PageResponse;
import vn.tamnguyen.model.Address;
import vn.tamnguyen.model.User;
import vn.tamnguyen.repository.criteria.SearchCriteria;
import vn.tamnguyen.repository.criteria.UserSearchQueryCriteriaConsumer;
import vn.tamnguyen.repository.specification.SpecSearchCriteria;
import vn.tamnguyen.util.Gender;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /*
        search with customize query and sort
    */
    public PageResponse<?> getAllUsersWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {

        StringBuilder sqlQuery = new StringBuilder("select new vn.tamnguyen.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone, u.dateOfBirth, u.gender, u.username, u.type, u.status) from User u where 1=1");

        if(StringUtils.hasLength(search)) {
            sqlQuery.append(" and lower(u.firstName) like lower(:firstName)");
            sqlQuery.append(" or lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" or lower(u.email) like lower(:email)");

        }

        // them sortBy
        if(StringUtils.hasLength(sortBy)) {
            //firstName:asc|desc

            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);

            if(matcher.find()) {
                sqlQuery.append(String.format(" order by u.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if(StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", String.format("%%%s%%", search));
            selectQuery.setParameter("lastName", String.format("%%%s%%", search));
            selectQuery.setParameter("email", String.format("%%%s%%", search));
        }

        List users = selectQuery.getResultList();
        System.out.println("=================>" + users);

        // query list user

        // query so record
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from User u where 1=1");

        if(StringUtils.hasLength(search)) {
            sqlCountQuery.append(" and lower(u.firstName) like lower(?1)");
            sqlCountQuery.append(" or lower(u.lastName) like lower(?2)");
            sqlCountQuery.append(" or lower(u.email) like lower(?3)");

        }

        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());
        if(StringUtils.hasLength(search)) {
            selectCountQuery.setParameter(1, String.format("%%%s%%", search));
            selectCountQuery.setParameter(2, String.format("%%%s%%", search));
            selectCountQuery.setParameter(3, String.format("%%%s%%", search));
        }

        System.out.println("COUNT QUERY = " + sqlCountQuery);
        System.out.println("SEARCH = [" + search + "]");
        System.out.println("HAS LENGTH = " + StringUtils.hasLength(search));
        Long totalElements = (Long) selectCountQuery.getSingleResult();
        System.out.println("=================>" + totalElements);

        Page<?> page = new PageImpl<Object>(users, PageRequest.of(pageNo, pageSize), totalElements);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(page.stream().toList())
                .build();
    }


    /*
        advance search with criteria and sort
        can join and search with tables
    */
    public PageResponse advanceSearchUser(int pageNo, int pageSize, String sortBy, String address, String... search) {
        // gia tri truyen vao search co dang firstName:T, lastName:T, email

        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        //1. lay ra danh sach user
        if(search != null) {
            for(String s : search) {
                //firstName:value

                Pattern pattern = Pattern.compile("(\\w+?)(:|>|<)(.*)");
                Matcher matcher = pattern.matcher(s);

                if(matcher.find()) {
                    searchCriteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        //2. lay ra so luong bang ghi
        List<User> users = getUsers(pageNo, pageSize, searchCriteriaList, sortBy, address);

        Long totalElements = getTotalElements(searchCriteriaList, address);

        return PageResponse.builder()
                .pageNo(pageNo) //offset = vi tri cua ban ghi trong danh sach
                .pageSize(pageSize)
                .totalPage(totalElements.intValue()) //total elements
                .items(users)
                .build();
    }

    private List<User> getUsers(int pageNo, int pageSize, List<SearchCriteria> searchCriteriaList, String sortBy, String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        // xu ly cac dieu kien tim kiem
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer queryConsumer = new UserSearchQueryCriteriaConsumer(criteriaBuilder, predicate, root);

        // xu ly join bang
        if(StringUtils.hasLength(address)) {
            Join<Address, User> addressUserJoin = root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");
            // tim kiem tren tat ca cac field cua address
            query.where(predicate, addressPredicate);
        } else {
            searchCriteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
            query.where(predicate);
        }



        //sort
        if(StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);

            if(matcher.find()) {
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("desc")) {
                    query.orderBy(criteriaBuilder.desc(root.get(columnName)));
                } else {
                    query.orderBy(criteriaBuilder.asc(root.get(columnName)));
                }
            }
        }

        return entityManager.createQuery(query).setFirstResult(pageNo).setMaxResults(pageSize).getResultList();
    }

    private Long getTotalElements(List<SearchCriteria> criteriaList, String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer queryConsumer = new UserSearchQueryCriteriaConsumer(criteriaBuilder, predicate, root);

        // xu ly join bang
        if(StringUtils.hasLength(address)) {
            Join<Address, User> addressUserJoin = root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");
            // tim kiem tren tat ca cac field cua address
            query.select(criteriaBuilder.count(root));
            query.where(predicate, addressPredicate);
        } else {
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
            query.select(criteriaBuilder.count(root));
            query.where(predicate);
        }

        return entityManager.createQuery(query).getSingleResult();
    }

    public PageResponse getUsersJoinedAddress(Pageable pageable, String[] user, String[] address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);
        Join<Address, User> addressRoot = userRoot.join("addresses");

       //build query
        List<Predicate> userPredicateList = new ArrayList<>();
        List<Predicate> addressPredicateList = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+?)([:<>~!])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
        for(String u : user) {
            Matcher matcher = pattern.matcher(u);
            if(matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toUserPredicate(userRoot, criteriaBuilder, criteria);
                userPredicateList.add(predicate);
            }
        }

        for(String a : address) {
            Matcher matcher = pattern.matcher(a);
            if(matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toAddressPredicate(addressRoot, criteriaBuilder, criteria);
                addressPredicateList.add(predicate);
            }
        }

        Predicate userPredicateArr = criteriaBuilder.or(userPredicateList.toArray(new Predicate[0]));
        Predicate addressPredicateArr = criteriaBuilder.or(addressPredicateList.toArray(new Predicate[0]));
        Predicate finalPredicate = criteriaBuilder.and(userPredicateArr, addressPredicateArr);
        query.where(finalPredicate);

        List<User> users = entityManager.createQuery(query)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long count = count(user, address);

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(count)
                .items(users)
                .build();
    }

    private long count(String[] user, String[] address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> userRoot = query.from(User.class);
        Join<Address, User> addressRoot = userRoot.join("addresses");

        //build query
        List<Predicate> userPredicateList = new ArrayList<>();
        List<Predicate> addressPredicateList = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+?)([:<>~!])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
        for(String u : user) {
            Matcher matcher = pattern.matcher(u);
            if(matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toUserPredicate(userRoot, criteriaBuilder, criteria);
                userPredicateList.add(predicate);
            }
        }

        for(String a : address) {
            Matcher matcher = pattern.matcher(a);
            if(matcher.find()) {
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                Predicate predicate = toAddressPredicate(addressRoot, criteriaBuilder, criteria);
                addressPredicateList.add(predicate);
            }
        }

        Predicate userPredicateArr = criteriaBuilder.or(userPredicateList.toArray(new Predicate[0]));
        Predicate addressPredicateArr = criteriaBuilder.or(addressPredicateList.toArray(new Predicate[0]));
        Predicate finalPredicate = criteriaBuilder.and(userPredicateArr, addressPredicateArr);
        query.select(criteriaBuilder.count(userRoot));
        query.where(finalPredicate);

        return entityManager.createQuery(query).getSingleResult();
    }


    public Predicate toUserPredicate(Root<User> root, CriteriaBuilder criteriaBuilder, SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> criteriaBuilder.equal(root.get(criteria.getKey()), Gender.valueOf(criteria.getValue().toString()));
            case NEGATION -> criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue().toString() + "%");
            case ENDS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString());
            case CONTAINS -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
        };
    }

    public Predicate toAddressPredicate(Join<Address, User> root, CriteriaBuilder criteriaBuilder, SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> criteriaBuilder.equal(root.get(criteria.getKey()), Gender.valueOf(criteria.getValue().toString()));
            case NEGATION -> criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue().toString() + "%");
            case ENDS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString());
            case CONTAINS -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
        };
    }
}

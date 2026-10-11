package vn.tamnguyen.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tamnguyen.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // -- JOIN --
    @Query(value = "select * from User u inner join Address a on u.id = a.userId where a.city = :city")
    List<User> getAllUser(String city);

    // -- DISTINCT --
    // @Query(value = "select distinct from User u where u.firstName=:firstName and u.lastName=:lastName")
    // trả về list nếu không có ý định phân trang, ngược lại trả về Page
    List<User> findDistinctByFirstNameAndLastName(String firstName, String lastName);

    // -- SINGLE FIELD --
    // @Query(value = "select * from User u where u.email= ?1")
    List<User> findByEmail(String email);

    // -- OR --
    // @Query(value = "select * from User u where u.firstName=:name or u.lastName=:name")
    List<User> findByFirstNameOrLastName(String name);

    // -- IS, EQUALS --
    // @Query(value = "select * from User u where u.firstName=:name")
    List<User> findByFirstNameIs(String name);
    List<User> findByFirstNameEquals(String name);
    List<User> findByFirstName(String name);

    // -- BETWEEN --
    // @Query(value = "select * from User u where u.createdAt between ?1 and 2?")
    List<User> findByCreatedAtBetween(Date startDate, Date endDate);

    // -- LESS THAN --
    // @Query(value = "select * from User u where u.age < :age")
    List<User> findByAgeLessThan(int age);
    List<User> findByAgeLessThanEqual();
    List<User> findByAgeGreaterThan();
    List<User> findByAgeGreaterThanEqual();

    // -- BEFORE AND AFTER --
    // @Query(value = "select * from User u where u.createdAt < date")
    List<User> findByCreatedAtBefore(Date date);
    List<User> findByCreatedAtAfter(Date date);

    // -- ISNULL, NULL --
    // @Query(value = "select * from User u where u.age is null")
    List<User> findByAgeIsNull();

    // -- NOTNULL, ISNOTNULL --
    // @Query(value = "select * from User u where u.age is not null")
    List<User> findByAgeNotNull();

    // -- LIKE, NOT LIKE --
    // @Query(value = "select * from User u where u.lastName like %:name%")
    List<User> findByLastNameLike(String name);

    // @Query(value = "select * from User u where u.lastName not like %:name%")
    List<User> findByLastNameNotLike(String name);

    // -- STARTINGWITH, ENDINGWITH --
    // @Query(value = "select * from User u where u.lastName like :name%")
    List<User> findByLastNameStartingWith(String name);

    // @Query(value = "select * from User u where u.lastName like %:name")
    List<User> findByLastNameEndingWith(String name);

    // -- CONTAINING --
    // @Query(value = "select * from User u where u.lastName like %:name%")
    List<User> findByLastNameContaining(String name);

    // -- NOT --
    // @Query(value = "select * from User u where u.lastName <> :name")
    List<User> findByLastNameNot(String name);

    // -- IN --
    // @Query(value = "select * from User u where u.age in (18,25,30)")
    List<User> findByAgeIn(Collection<Integer> age);

    // -- NOT IN --
    // @Query(value = "select * from User u where u.age not in (18,25,30)")
    List<User> findByAgeNotIn(Collection<Integer> age);

    // -- TRUE / FALSE --
    // @Query(value = "select * from User u where u.activated=true")
    List<User> findByActivatedTrue();

    // @Query(value = "select * from User u where u.activated=false")
    List<User> findByActivatedFalse();

    // -- IGNORECASE --
    // @Query(value = "select * from User u where LOWER(u.firstName) like LOWER(%:name%)")
    List<User> findByFirstNameIgnoreCase(String name);

    // -- ALL IGNORECASE --
    List<User> findByFirstNameAllIgnoreCase(String firstName, String lastName);

    // -- ORDER BY --
    List<User> findByFirstNameOrderByCreatedAtDesc(String name);
}

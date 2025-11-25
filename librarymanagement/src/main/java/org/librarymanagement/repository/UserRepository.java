package org.librarymanagement.repository;

import org.librarymanagement.dto.response.UserCountByMonth;
import org.librarymanagement.dto.response.UserDetailReponse;
import org.librarymanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {

    boolean existsUserByEmail(String email);
    boolean existsUserByPhone(String phone);
    boolean existsUserByUsername(String username);
    Optional<User> findByUsername(String username);
    User findByEmail(String email);
    User findUserById(Integer id);
    User findByName(String name);

    @Query("SELECT DISTINCT new org.librarymanagement.dto.response.UserDetailReponse" +
            "(u.id, u.name, u.username, u.password, u.status, u.activatedStatus, u.email, u.phone, u.role) " +
            "FROM User u")
    Page<UserDetailReponse> findAllUser(Pageable pageable);

        // Thống kê người dùng mới theo tháng trong một năm cụ thể
    @Query("""
        SELECT 
            FUNCTION('MONTH', u.createdAt) AS month,
            COUNT(u) AS userCount
        FROM User u
        WHERE u.createdAt IS NOT NULL
          AND FUNCTION('YEAR', u.createdAt) = :year
        GROUP BY FUNCTION('MONTH', u.createdAt)
        ORDER BY FUNCTION('MONTH', u.createdAt)
    """)
    List<UserCountByMonth> countNewUsersGroupedByMonth(@Param("year") Integer year);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Integer countNewUsersByDay(LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Query("""
        UPDATE User u
        SET u.role = :role
        WHERE u.id = :id
    """)
    int updateUserRole(
            @Param("id") Integer id,
            @Param("role") Integer role
    );
}

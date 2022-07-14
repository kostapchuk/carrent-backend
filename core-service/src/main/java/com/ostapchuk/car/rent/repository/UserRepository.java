package com.ostapchuk.car.rent.repository;

import com.ostapchuk.car.rent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findAllByOrderById();

    boolean existsByEmail(String email);

    @Modifying
    @Query("update User u set u.balance = 0 where u.id = :id")
    void resetBalance(@Param("id") Long id);

    @Modifying
    @Query("update User u set u.passportImgUrl = :url where u.id = :id")
    void updatePassportUrl(@Param("id") Long id, String url);

    @Modifying
    @Query("update User u set u.drivingLicenseImgUrl = :url where u.id = :id")
    void updateDrivingLicenseUrl(@Param("id") Long id, String url);
}

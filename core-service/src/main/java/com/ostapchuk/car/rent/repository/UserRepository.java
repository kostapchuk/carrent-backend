package com.ostapchuk.car.rent.repository;

import com.ostapchuk.car.rent.entity.Person;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<Person, Long> {

    Optional<Person> findByEmail(String email);

    List<Person> findAllByOrderById();

    boolean existsByEmail(String email);

    @Modifying
    @Query("update Person u set u.balance = 0 where u.id = :id")
    void resetBalance(@Param("id") Long id);

    @Modifying
    @Query("update Person u set u.passportImgUrl = :url where u.id = :id")
    void updatePassportUrl(@Param("id") Long id, String url);

    @Modifying
    @Query("update Person u set u.drivingLicenseImgUrl = :url where u.id = :id")
    void updateDrivingLicenseUrl(@Param("id") Long id, String url);
}

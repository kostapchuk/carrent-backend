package com.ostapchuk.car.rent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = User.USER)
public class User {

    public static final String USER = "user";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "verified")
    private boolean verified;

    @Column(name = "passport_img_url")
    private String passportImgUrl;

    @Column(name = "driving_license_img_url")
    private String drivingLicenseImgUrl;

    @OneToMany(mappedBy = USER)
    private List<Order> orders;
}

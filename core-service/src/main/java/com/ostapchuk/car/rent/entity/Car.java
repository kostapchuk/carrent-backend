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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;

import static javax.persistence.EnumType.STRING;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_id_seq")
    @SequenceGenerator(name = "car_id_seq", sequenceName = "car_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "mark", nullable = false)
    private String mark;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "img_link")
    private String imgLink;

    @Column(name = "rent_price_per_hour", nullable = false)
    private BigDecimal rentPricePerHour;

    @Column(name = "book_price_per_hour", nullable = false)
    private BigDecimal bookPricePerHour;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private CarStatus status = CarStatus.UNAVAILABLE;
}

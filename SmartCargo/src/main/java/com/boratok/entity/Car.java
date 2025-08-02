package com.boratok.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Car")
public class Car extends BaseEntity {

    @Column(name = "license_plate", columnDefinition = "nvarchar(12)")
    private String licensePlate;

    @JoinColumn(name = "car_status_id")
    @ManyToOne
    private EventLog status;

    @ManyToOne
    @JoinColumn(name = "car_type_id")//foreign key sutunu oluşturmak için
    private CarType carType;

}

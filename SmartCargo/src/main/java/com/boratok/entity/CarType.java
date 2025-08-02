package com.boratok.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CarType extends BaseEntity {

    @Column(name = "car_type", columnDefinition = "nvarchar(10)")
    private String cartype;

    @Column(name = "capacity")
    private Double capacity;

    private Double carSize;

}

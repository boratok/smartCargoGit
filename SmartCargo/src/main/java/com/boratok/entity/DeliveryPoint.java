package com.boratok.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPoint extends BaseEntity {

    @Column(name = "point_name", columnDefinition = "nvarchar(50)")
    private String deliveryPoint;
}

package com.boratok.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cargo_package")
public class CargoPackage extends BaseEntity {

    private Double weight;

    private Double size;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private EventLog status;

    @ManyToOne
    @JoinColumn(name = "delivery_point_id")//foreign key sutunu oluşturmak için
    private DeliveryPoint deliveryPoint;

    @ManyToOne
    @JoinColumn(name = "route_assignment_id")
    private RouteAssignment routeAssignment;

    @Column(name = "order_number")
    private Integer orderNumber;
}

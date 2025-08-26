package com.boratok.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryInfo extends BaseEntity{

    private Date deliveryDate;

    @ManyToOne
    @JoinColumn(name = "route_assignment_id")
    private RouteAssignment routeAssignment;

    @ManyToOne
    @JoinColumn(name = "cargo_packages_id")
    private CargoPackage cargoPackage;
}

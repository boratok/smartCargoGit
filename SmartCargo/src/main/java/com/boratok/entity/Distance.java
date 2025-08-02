package com.boratok.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Distance extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "start_point_id")
    private DeliveryPoint startPoint;

    @OneToOne
    @JoinColumn(name = "finish_point_id")
    private DeliveryPoint finishPoint;

    @Column(name = "distance_btw")
    private Double distanceBtw;

}

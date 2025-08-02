package com.boratok.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventLog extends BaseEntity {

    @Column(name = "event_type")
    private String eventType="siparis_alindi";

}

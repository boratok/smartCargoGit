package com.boratok.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends BaseEntity {

    @Column(name = "name_surname", columnDefinition = "nvarchar(50)")
    private String nameSurname;

    @ManyToOne
    @JoinColumn(name = "role_name_id")//driver tablosu foreign key
    private Users users;
}

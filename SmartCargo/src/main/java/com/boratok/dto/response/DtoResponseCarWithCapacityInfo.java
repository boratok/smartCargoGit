package com.boratok.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DtoResponseCarWithCapacityInfo {

    private Long carId;

    private double capacity;

    private double size;
}

package com.boratok.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DtoRequestDriver extends DtoBase {
    private String nameSurname;
    private Long users;
}

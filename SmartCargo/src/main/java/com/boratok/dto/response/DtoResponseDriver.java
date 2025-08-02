package com.boratok.dto.response;

import com.boratok.dto.request.DtoBase;
import com.boratok.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoResponseDriver {
    private String nameSurname;
    private Users users;

}

package com.boratok.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoRequestRouteAssgmnt extends DtoBase{
    private Long storage;
    private Date startDate;
    private Date finishDate;
    private Long driver;
    private Long car;

}

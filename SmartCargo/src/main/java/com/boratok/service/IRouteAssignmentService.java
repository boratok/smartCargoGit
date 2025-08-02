package com.boratok.service;

import com.boratok.dto.request.DtoRequestRouteAssgmnt;
import com.boratok.dto.response.DtoResponseRouteAssgmnt;

public interface IRouteAssignmentService {
    public DtoResponseRouteAssgmnt saveRouteAssgmnt(DtoRequestRouteAssgmnt dtoRequestRouteAssgmnt);

    public DtoResponseRouteAssgmnt getRouteAssgmntById(Long id);

    public boolean deleteRouteAssigmnt(Long id);

    public DtoResponseRouteAssgmnt updateRouteAssigmnt(Long id,DtoRequestRouteAssgmnt dtoRequestRouteAssgmnt);
}

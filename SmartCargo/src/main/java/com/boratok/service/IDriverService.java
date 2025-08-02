package com.boratok.service;

import com.boratok.dto.request.DtoRequestDriver;
import com.boratok.dto.response.DtoResponseDriver;
import org.springframework.stereotype.Service;

@Service
public interface IDriverService {
    public DtoRequestDriver saveDriver(DtoRequestDriver dtoRequestDriver);

    public DtoResponseDriver getDriverById(Long id);

    public boolean deleteDriver(Long id);

    public DtoResponseDriver updateDriver(Long id,DtoRequestDriver dtoRequestDriver);
}

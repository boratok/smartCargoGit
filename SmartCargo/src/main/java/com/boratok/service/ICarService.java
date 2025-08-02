package com.boratok.service;

import com.boratok.dto.response.DtoResponseCar;
import org.springframework.stereotype.Service;

@Service
public interface ICarService {

    public com.boratok.dto.request.DtoRequestCar saveCar(com.boratok.dto.request.DtoRequestCar dtoRequestCar);

    public DtoResponseCar findCarById(Long id);

    public boolean deleteCar(Long id);

    public DtoResponseCar updateCar(Long id, com.boratok.dto.request.DtoRequestCar dtoRequestCar);
}

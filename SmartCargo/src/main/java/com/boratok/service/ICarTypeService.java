package com.boratok.service;

import com.boratok.entity.CarType;

public interface ICarTypeService {
    public CarType saveCarType(CarType carType);

    public CarType getCarType(Long id);

    public boolean deleteCarType(Long id);

    public CarType updateCarType(Long id,CarType carType);
}

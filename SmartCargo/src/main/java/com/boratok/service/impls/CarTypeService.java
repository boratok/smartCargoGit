package com.boratok.service.impls;

import com.boratok.entity.CarType;
import com.boratok.repository.CarTypeRepository;
import com.boratok.service.ICarTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarTypeService implements ICarTypeService {

    @Autowired
    private CarTypeRepository carTypeRepository;

    @Override
    public CarType saveCarType(CarType carType) {
        carTypeRepository.save(carType);
        return carType;
    }

    @Override
    public CarType getCarType(Long id) {
        Optional<CarType> optCarType = carTypeRepository.findById(id);
        if (optCarType.isEmpty()) {
            System.out.println("bu id de bir araç türü yok");
        }
        CarType carType = optCarType.get();

        return carType;
    }

    @Override
    public boolean deleteCarType(Long id) {
        Optional<CarType> optCarType = carTypeRepository.findById(id);
        if (optCarType.isEmpty()) {
            System.out.println("bu id de bir araç türü yok");
            return false;
        }
        carTypeRepository.delete(optCarType.get());

        return true;
    }

    @Override
    public CarType updateCarType(Long id, CarType carType) {
        Optional<CarType> optCarType = carTypeRepository.findById(id);
        if (optCarType.isEmpty()) {
            System.out.println("bu id de bir araç türü yok");
            return null;
        }
        CarType toUpdate = optCarType.get();
        toUpdate.setCapacity(carType.getCapacity());
        toUpdate.setCarSize(carType.getCarSize());
        toUpdate.setCartype(carType.getCartype());

        carTypeRepository.save(toUpdate);

        return toUpdate;
    }
}

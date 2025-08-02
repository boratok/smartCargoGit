package com.boratok.service.impls;

import com.boratok.dto.request.DtoRequestCar;
import com.boratok.dto.response.DtoResponseCar;
import com.boratok.entity.Car;
import com.boratok.entity.CarType;
import com.boratok.entity.EventLog;
import com.boratok.repository.CarRepository;
import com.boratok.repository.CarTypeRepository;
import com.boratok.repository.EventLogRepository;
import com.boratok.service.ICarService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarService implements ICarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarTypeRepository carTypeRepository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Override
    public DtoRequestCar saveCar(DtoRequestCar dtoRequestCar) {

        Car car = new Car();

        car.setLicensePlate(dtoRequestCar.getLicensePlate());

        Optional<CarType> optCarType = carTypeRepository.findById(dtoRequestCar.getCarType());
        if (optCarType.isPresent()) {
            car.setCarType(optCarType.get());
        } else {
            return null;
        }

        carRepository.save(car);
        return dtoRequestCar;
    }

    @Override
    public DtoResponseCar findCarById(Long id) {
        Optional<Car> optCar = carRepository.findById(id);
        if (optCar.isEmpty()) {
            System.out.println("Boyle bir deger yok");
            return null;
        }
        DtoResponseCar dtoResponseCar = new DtoResponseCar();
        Car car = optCar.get();

        EventLog status = optCar.get().getStatus();

        BeanUtils.copyProperties(car, dtoResponseCar);
        dtoResponseCar.setCarType(optCar.get().getCarType());
        dtoResponseCar.setStatus(status);
        return dtoResponseCar;
    }

    @Override
    public boolean deleteCar(Long id) {
        Optional<Car> optCar = carRepository.findById(id);
        if (optCar.isEmpty()) {
            System.out.println("bu id de bir car yok");
            return false;
        }
        carRepository.delete(optCar.get());
        return true;
    }

    @Override
    public DtoResponseCar updateCar(Long id, com.boratok.dto.request.DtoRequestCar dtoRequestCar) {
        Optional<Car> optCar = carRepository.findById(id);
        if (optCar.isEmpty()) {
            System.out.println("bu id de bir arac bulunmamakta");
            return null;
        }
        //bu asagidaki kisimda yeni degere setleme islemlerini yapiyoruz
        Car findCar = optCar.get();
        findCar.setLicensePlate(dtoRequestCar.getLicensePlate());

        Optional<CarType> optCarType = carTypeRepository.findById(dtoRequestCar.getCarType());
        if (optCarType.isPresent()) {
            findCar.setCarType(optCarType.get());
        } else {
            return null;
        }

        Optional<EventLog> optEventLog = eventLogRepository.findById(dtoRequestCar.getStatus());
        if (optEventLog.isPresent()) {
            findCar.setStatus(optEventLog.get());
        } else {
            return null;
        }

        carRepository.save(findCar);


        //guncelledigimiz veriyi postman uzerinde gormek icin dto olarak deger donuyoruz

        optCar = carRepository.findById(id);

        DtoResponseCar dtoResponseCar = new DtoResponseCar();
        dtoResponseCar.setLicensePlate(dtoRequestCar.getLicensePlate());
        dtoResponseCar.setCarType(optCar.get().getCarType());
        dtoResponseCar.setStatus(optEventLog.get());

        return dtoResponseCar;
    }
}

package com.boratok.service.impls;

import com.boratok.dto.response.*;
import com.boratok.dto.request.DtoRequestPackage;
import com.boratok.entity.*;
import com.boratok.repository.*;
import com.boratok.service.IPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PackageService implements IPackageService {

    @Autowired
    private CargoPackageRepository cargoPackageRepository;

    @Autowired
    private DeliveryPointRepository deliveryPointRepository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private RouteAssignmentRepository routeAssignmentRepository;

    @Autowired
    private StatusRepository statusRepository;


    @Override
    public boolean savePackage(DtoRequestPackage dtoRequestPackage) {
        Optional<DeliveryPoint> optDeliveryPoint = deliveryPointRepository.findById(dtoRequestPackage.getDeliveryPointId());
        if (optDeliveryPoint.isEmpty()) {
            System.out.println("bu storge id değerinde bir depo yok");
            return false;
        }
        CargoPackage newCargoPackage = new CargoPackage();
        newCargoPackage.setWeight(dtoRequestPackage.getWeight());
        newCargoPackage.setSize(dtoRequestPackage.getPackageSize());
        newCargoPackage.setDeliveryPoint(optDeliveryPoint.get());
        //Optional<EventLog> status = statusRepository.findById(dtoRequestPackage.getStatus());
        //newCargoPackage.setStatus(status.get());
        cargoPackageRepository.save(newCargoPackage);
        return true;
    }

    @Override
    public DtoResponsePackage getPackageInfoById(Long id) {

        Optional<CargoPackage> optPackage = cargoPackageRepository.findById(id);
        if (optPackage.isEmpty()) {
            System.out.println("girdiginz id degeriyle eslesen bir paket bulunmamakta");
            return null;
        }

        DtoResponsePackage dtoResponsePackage = new DtoResponsePackage();

        DtoResponseRouteAssgmnt dtoResponseRouteAssgmnt = new DtoResponseRouteAssgmnt();

        RouteAssignment routeAssignment = optPackage.get().getRouteAssignment();
        dtoResponseRouteAssgmnt.setCar(new DtoResponseCar(routeAssignment.getCar().getLicensePlate(),
                routeAssignment.getCar().getStatus(),
                routeAssignment.getCar().getCarType()
        ));
        dtoResponseRouteAssgmnt.setDeliveryPoint(optPackage.get().getDeliveryPoint());
        dtoResponseRouteAssgmnt.setDriver(new DtoResponseDriver(optPackage.get().getRouteAssignment().getDriver().getNameSurname(),
                optPackage.get().getRouteAssignment().getDriver().getUsers()));
        dtoResponseRouteAssgmnt.setStartDate(optPackage.get().getRouteAssignment().getStartDate());
        dtoResponseRouteAssgmnt.setStartDate(optPackage.get().getRouteAssignment().getEndDate());


        dtoResponsePackage.setWeight(optPackage.get().getWeight());
        dtoResponsePackage.setResponseRouteAssgmnt(dtoResponseRouteAssgmnt);
        dtoResponsePackage.setStatus(optPackage.get().getStatus());
        dtoResponsePackage.setDeliveryPoint(optPackage.get().getDeliveryPoint());

        return dtoResponsePackage;
    }

    @Override
    public boolean deletePackage(Long id) {
        Optional<CargoPackage> optPackage = cargoPackageRepository.findById(id);
        if (optPackage.isEmpty()) {
            System.out.println("girdiginz id degeriyle eslesen bir paket bulunmamakta");
            return false;
        }
        cargoPackageRepository.delete(optPackage.get());
        return true;
    }

    @Override
    public DtoResponsePackage updatePackage(Long id, DtoRequestPackage dtoRequestPackage) {
        Optional<CargoPackage> optPackage = cargoPackageRepository.findById(id);
        if (optPackage.isEmpty()) {
            System.out.println("girdiginz id degeriyle eslesen bir paket bulunmamakta");
            return null;
        }

        Optional<RouteAssignment> optRouteAssignment = routeAssignmentRepository.findById(dtoRequestPackage.getRouteAssignmentId());
        if (optRouteAssignment.isEmpty()) {
            System.out.println("girdiginz id degeriyle eslesen bir rota bulunmamakta");
            return null;
        }

        Optional<DeliveryPoint> optDeliveryPoint = deliveryPointRepository.findById(dtoRequestPackage.getDeliveryPointId());
        if (optDeliveryPoint.isEmpty()) {
            System.out.println("girdiginz id degeriyle eslesen bir arac bulunmamakta");
            return null;
        }

        Optional<EventLog> optEventLog = eventLogRepository.findById(dtoRequestPackage.getStatus());
        if (optEventLog.isEmpty()) {
            System.out.println("girdiginz id degeriyle eslesen bir obje bulunmamakta");
            return null;
        }
        CargoPackage findCargoPackage = optPackage.get();
        findCargoPackage.setRouteAssignment(optRouteAssignment.get());
        findCargoPackage.setStatus(optEventLog.get());
        findCargoPackage.setWeight(dtoRequestPackage.getWeight());
        findCargoPackage.setSize(dtoRequestPackage.getPackageSize());
        findCargoPackage.setDeliveryPoint(optDeliveryPoint.get());

        cargoPackageRepository.save(findCargoPackage);

        optPackage = cargoPackageRepository.findById(id);

        DtoResponseRouteAssgmnt dtoResponseRouteAssgmnt = new DtoResponseRouteAssgmnt();
        DtoResponsePackage dtoResponsePackage = new DtoResponsePackage();

        RouteAssignment routeAssignment = optPackage.get().getRouteAssignment();
        dtoResponseRouteAssgmnt.setCar(new DtoResponseCar(routeAssignment.getCar().getLicensePlate(),
                routeAssignment.getCar().getStatus(),
                routeAssignment.getCar().getCarType()
        ));
        dtoResponseRouteAssgmnt.setDeliveryPoint(optPackage.get().getDeliveryPoint());
        dtoResponseRouteAssgmnt.setDriver(new DtoResponseDriver(optPackage.get().getRouteAssignment().getDriver().getNameSurname(),
                optPackage.get().getRouteAssignment().getDriver().getUsers()));
        dtoResponseRouteAssgmnt.setStartDate(optPackage.get().getRouteAssignment().getStartDate());
        dtoResponseRouteAssgmnt.setStartDate(optPackage.get().getRouteAssignment().getEndDate());

        dtoResponsePackage.setWeight(optPackage.get().getWeight());
        dtoResponsePackage.setResponseRouteAssgmnt(dtoResponseRouteAssgmnt);
        dtoResponsePackage.setStatus(optPackage.get().getStatus());
        dtoResponsePackage.setDeliveryPoint(optPackage.get().getDeliveryPoint());
        return dtoResponsePackage;
    }
}

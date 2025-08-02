package com.boratok.service;

import com.boratok.dto.response.DtoResponseCarWithCapacityInfo;
import com.boratok.entity.*;
import com.boratok.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class paketYuklemeService {

    @Autowired
    CarRepository carRepository;

    @Autowired
    CargoPackageRepository cargoPackageRepository;

    @Autowired
    EventLogRepository eventLogRepository;

    @Autowired
    DistanceRepository distanceRepository;

    @Autowired
    DeliveryPointRepository deliveryPointRepository;

    public void paketYukleme() {

        // status durumu avaible olan araclarin kapasiteleriyle beraber cektim
        List<DtoResponseCarWithCapacityInfo> avaibleCarsWithCapacity = carRepository.avaibleCarsWithCapacity();

        //burada hareket etmemis status avaible olan kargo paketlerini cekiyyorum
        List<CargoPackage> getAvaiblePackages = cargoPackageRepository.getCargoPackagesOrderByCreateTime();

        // burada hangi paketin hangi aracla gidecegini tutmak icin bir map yapisi kullaniyorum
        Map<Long, List<CargoPackage>> cargoPackagesInCarMap = new HashMap<>();
        List<CargoPackage> cargoPackageList = null;// o aracla gidecek paketlerin listesini tutmak icibn

        // burada ise paketlerin gidecegi yerleri aracin idsi ile mapleyerek tutuyorum ki sonradan rotayi belirleyebileyim
        // ve order_number atayabileyim
        Map<Long, List<DeliveryPoint>> deliveryPointsWithCarIdMap = new HashMap<>();
        List<DeliveryPoint> packagesDeliveryPoints = null;

        double carCapacity = 0;
        double carSize = 0;

        double packageSize = 0;
        double packageCapacity = 0;

        Optional<EventLog> optStatus=eventLogRepository.findById(4L);
        EventLog statusYuklendi=optStatus.get();

        optStatus=eventLogRepository.findById(6L);
        EventLog statusAracDoldu=optStatus.get();

        Optional<Car> optCar=null;

        //tum araclar doldurulacak sekilde for dongusu kurdum
        for (DtoResponseCarWithCapacityInfo dtoResponseCarWithCapacityInfo : avaibleCarsWithCapacity) {

            cargoPackageList = new ArrayList<>();
            packagesDeliveryPoints = new ArrayList<>();

            carCapacity = dtoResponseCarWithCapacityInfo.getCapacity();
            carSize = dtoResponseCarWithCapacityInfo.getSize();

            for (CargoPackage cargoPackage : getAvaiblePackages) {
                packageCapacity = cargoPackage.getWeight();
                packageSize = cargoPackage.getSize();
                if (packageSize < carSize && packageCapacity < carCapacity) {
                    carSize -= packageSize;// alacagi hacim azaldi
                    carCapacity -= packageCapacity;// aracin kapasitesi dustu
                    cargoPackageList.add(cargoPackage);//listeye koydum
                    packagesDeliveryPoints.add(cargoPackage.getDeliveryPoint());// burada delivery pointini ekledik
                    cargoPackage.setStatus(statusYuklendi);
                }
            }
            //araca herhangi bir sey yuklenmediyse bu islemleri yapmamasi icin
            if(carCapacity!=dtoResponseCarWithCapacityInfo.getCapacity()&&carSize!=dtoResponseCarWithCapacityInfo.getSize()) {

                //car id ile paketleri ve deliveryPointleri maplemis olduk burada
                cargoPackagesInCarMap.put(dtoResponseCarWithCapacityInfo.getCarId(), cargoPackageList);
                deliveryPointsWithCarIdMap.put(dtoResponseCarWithCapacityInfo.getCarId(), packagesDeliveryPoints);

                //aracin durumunu doldu olarak yaptik
                optCar=carRepository.findById(dtoResponseCarWithCapacityInfo.getCarId());
                Car car=optCar.get();
                car.setStatus(statusAracDoldu);
            }
            // kullandigimiz paketleri tekrardan almamak adina koyulmamis paketleri cektik
            getAvaiblePackages = cargoPackageRepository.getCargoPackagesOrderByCreateTime();
        }

        Long startPoint=1L;
        Optional<DeliveryPoint> optionalDeliveryPoint=deliveryPointRepository.findById(1L);
        DeliveryPoint storage=optionalDeliveryPoint.get();
        Long carId=null;
        double sumDistanc=0;
        double distanceBtw=0;
        double minDistance=Double.MAX_VALUE;
        double tempDistance=0;
        Long fisinhPoint=null;
        int sayac=0;
        int index=0;
        int order=0;
        Long deliveryPointId=null;

        for(Map.Entry<Long, List<DeliveryPoint>> entry : deliveryPointsWithCarIdMap.entrySet()){
            List<DeliveryPoint> tempDeliveryList = new ArrayList<>(entry.getValue());
            RouteAssignment routeAssignment=new RouteAssignment();
            Optional<Car>optCarForRoute=carRepository.findById(entry.getKey());
            Car carForRoute=optCarForRoute.get();
            routeAssignment.setCar(carForRoute);
            routeAssignment.setStorage(storage);
            while (!tempDeliveryList.isEmpty()) {

                for (DeliveryPoint deliveryPoint : tempDeliveryList) {
                    fisinhPoint = deliveryPoint.getId();
                    tempDistance = distanceRepository.getDistanceBtw(startPoint, fisinhPoint);
                    if (tempDistance < minDistance) {
                        minDistance = tempDistance;
                        distanceBtw = tempDistance;
                        index=sayac;
                    }
                    sayac++;
                }
                List<CargoPackage>packageList=cargoPackagesInCarMap.get(entry.getKey());
                for(CargoPackage cargoPackage:packageList){
                    if(cargoPackage.getDeliveryPoint().getId()==fisinhPoint){
                        cargoPackage.setRouteAssignment(routeAssignment);
                        cargoPackage.setOrderNumber(order);
                    }
                }
                tempDeliveryList.remove(index);
                order+=1;
                sayac=0;
            }
            order=0;
        }
    }
}

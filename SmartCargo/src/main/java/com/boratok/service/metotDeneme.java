package com.boratok.service;

import com.boratok.dto.response.DtoResponseCarWithCapacityInfo;
import com.boratok.entity.*;
import com.boratok.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class metotDeneme {

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

    @Autowired
    RouteAssignmentRepository routeAssignmentRepository;

    @Autowired
    DriverRepository driverRepository;

    public void paketYukleme() {

        // status durumu avaible olan araclarin kapasiteleriyle beraber cektim
        List<DtoResponseCarWithCapacityInfo> avaibleCarsWithCapacity = carRepository.avaibleCarsWithCapacity();

        //burada hareket etmemis status avaible olan kargo paketlerini cekiyyorum
        List<CargoPackage> getAvaiblePackages = cargoPackageRepository.getAvaibleCargoPackages();

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

        Optional<EventLog> optStatus = eventLogRepository.findById(4L);
        EventLog statusYuklendi = optStatus.get();

        optStatus = eventLogRepository.findById(6L);
        EventLog statusAracDoldu = optStatus.get();

        Optional<Car> optCar = null;

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
                    cargoPackageRepository.save(cargoPackage);
                }
            }
            //araca herhangi bir sey yuklenmediyse bu islemleri yapmamasi icin
            if (carCapacity != dtoResponseCarWithCapacityInfo.getCapacity() && carSize != dtoResponseCarWithCapacityInfo.getSize()) {

                //car id ile paketleri ve deliveryPointleri maplemis olduk burada
                cargoPackagesInCarMap.put(dtoResponseCarWithCapacityInfo.getCarId(), cargoPackageList);
                deliveryPointsWithCarIdMap.put(dtoResponseCarWithCapacityInfo.getCarId(), packagesDeliveryPoints);

                //aracin durumunu doldu olarak yaptik
                optCar = carRepository.findById(dtoResponseCarWithCapacityInfo.getCarId());
                Car car = optCar.get();
                car.setStatus(statusAracDoldu);
                carRepository.save(car);
            }
            // kullandigimiz paketleri tekrardan almamak adina koyulmamis paketleri cektik
            getAvaiblePackages = cargoPackageRepository.getAvaibleCargoPackages();
        }

        Long startPoint = 1L;
        Optional<DeliveryPoint> optionalDeliveryPoint = deliveryPointRepository.findById(1L);
        DeliveryPoint storage = optionalDeliveryPoint.get();
        double minDistance = Double.MAX_VALUE;
        double tempDistance = 0;
        Long fisinhPoint = null;
        Long tempPoint = null;

        int order = 0;

        for (Map.Entry<Long, List<DeliveryPoint>> entry : deliveryPointsWithCarIdMap.entrySet()) {
            List<DeliveryPoint> tempDeliveryList = new ArrayList<>(entry.getValue());
            while (!tempDeliveryList.isEmpty()) {
                for (DeliveryPoint deliveryPoint : tempDeliveryList) {
                    fisinhPoint = deliveryPoint.getId();
                    tempDistance = distanceRepository.getDistanceBtw(startPoint, fisinhPoint);
                    if (tempDistance < minDistance) {
                        minDistance = tempDistance;
                        tempPoint = fisinhPoint;
                    }
                }
                startPoint = tempPoint;
                List<CargoPackage> packagesToSave = new ArrayList<>();
                List<CargoPackage> packageList = cargoPackagesInCarMap.get(entry.getKey());
                for (CargoPackage cargoPackage : packageList) {
                    if (cargoPackage.getDeliveryPoint().getId().equals(tempPoint)) {
                        cargoPackage.setOrderNumber(order);
                        packagesToSave.add(cargoPackage);
                    }
                }
                final Long tempDeliveryId = tempPoint;
                cargoPackageRepository.saveAll(packagesToSave);
                tempDeliveryList.removeIf(dp -> dp.getId().equals(tempDeliveryId));
                order += 1;
                minDistance = Double.MAX_VALUE;
                ;

            }
            order = 0;

        }
        for (Map.Entry<Long, List<CargoPackage>> getEntry : cargoPackagesInCarMap.entrySet()) {

            RouteAssignment routeAssignment = new RouteAssignment();
            Optional<Car> optCarForRoute = carRepository.findById(getEntry.getKey());
            Car carForRoute = optCarForRoute.get();
            routeAssignment.setCar(carForRoute);
            routeAssignment.setStorage(storage);
            routeAssignmentRepository.save(routeAssignment);
            List<CargoPackage> cargoPackagesRouteSet = getEntry.getValue();

            for (CargoPackage cargoPackage : cargoPackagesRouteSet) {
                cargoPackage.setRouteAssignment(routeAssignment);
            }
            cargoPackageRepository.saveAll(cargoPackagesRouteSet);
        }
    }


    public boolean loadPackage() {
        try {
            List<DtoResponseCarWithCapacityInfo> avaibleCarsWithCapacity = carRepository.avaibleCarsWithCapacity();
            List<CargoPackage> getAvaiblePackages = cargoPackageRepository.getAvaibleCargoPackages();

            Map<Long, List<CargoPackage>> cargoPackagesInCarMap = new HashMap<>();
            Map<Long, List<DeliveryPoint>> deliveryPointsWithCarIdMap = new HashMap<>();

            double carCapacity, carSize, packageSize, packageCapacity;

            Optional<EventLog> optStatus = eventLogRepository.findById(4L);
            EventLog statusYuklendi = optStatus.orElseThrow(() -> new RuntimeException("EventLog ID 4 not found"));

            optStatus = eventLogRepository.findById(6L);
            EventLog statusAracDoldu = optStatus.orElseThrow(() -> new RuntimeException("EventLog ID 6 not found"));

            for (DtoResponseCarWithCapacityInfo carInfo : avaibleCarsWithCapacity) {
                List<CargoPackage> cargoPackageList = new ArrayList<>();
                List<DeliveryPoint> packagesDeliveryPoints = new ArrayList<>();

                carCapacity = carInfo.getCapacity();
                carSize = carInfo.getSize();

                Iterator<CargoPackage> iterator = getAvaiblePackages.iterator();
                while (iterator.hasNext()) {
                    CargoPackage cargoPackage = iterator.next();
                    packageCapacity = cargoPackage.getWeight();
                    packageSize = cargoPackage.getSize();

                    if (packageSize < carSize && packageCapacity < carCapacity) {
                        carSize -= packageSize;
                        carCapacity -= packageCapacity;

                        cargoPackage.setStatus(statusYuklendi);
                        cargoPackageList.add(cargoPackage);
                        packagesDeliveryPoints.add(cargoPackage.getDeliveryPoint());
                        cargoPackageRepository.save(cargoPackage);
                        iterator.remove(); // tekrar almamak için
                    }
                }

                if (!cargoPackageList.isEmpty()) {
                    cargoPackagesInCarMap.put(carInfo.getCarId(), cargoPackageList);
                    deliveryPointsWithCarIdMap.put(carInfo.getCarId(), packagesDeliveryPoints);

                    Car car = carRepository.findById(carInfo.getCarId())
                            .orElseThrow(() -> new RuntimeException("Car ID " + carInfo.getCarId() + " not found"));
                    car.setStatus(statusAracDoldu);
                    carRepository.save(car);
                }
            }

            Long startPoint = 1L;
            Long nearestPoint = null;
            Long tempPoint = null;

            DeliveryPoint storage = deliveryPointRepository.findById(startPoint)
                    .orElseThrow(() -> new RuntimeException("Start Point not found"));

            for (Map.Entry<Long, List<DeliveryPoint>> entry : deliveryPointsWithCarIdMap.entrySet()) {
                List<DeliveryPoint> tempDeliveryList = new ArrayList<>(entry.getValue());
                List<CargoPackage> packageList = cargoPackagesInCarMap.get(entry.getKey());
                int order = 0;

                while (!tempDeliveryList.isEmpty()) {
                    double minDistance = Double.MAX_VALUE;


                    for (DeliveryPoint dp : tempDeliveryList) {
                        double distance = distanceRepository.getDistanceBtw(startPoint, dp.getId());
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestPoint = dp.getId();
                        }
                    }

                    startPoint = nearestPoint;
                    List<CargoPackage> packagesToSave = new ArrayList<>();

                    for (CargoPackage cp : packageList) {
                        if (cp.getDeliveryPoint().getId().equals(nearestPoint)) {
                            cp.setOrderNumber(order);
                            packagesToSave.add(cp);
                        }
                    }
                    final Long deletedDeliveryId = tempPoint;

                    cargoPackageRepository.saveAll(packagesToSave);
                    tempDeliveryList.removeIf(dp -> dp.getId().equals(deletedDeliveryId));
                    order++;
                }
            }

            for (Map.Entry<Long, List<CargoPackage>> entry : cargoPackagesInCarMap.entrySet()) {
                RouteAssignment routeAssignment = new RouteAssignment();
                Car car = carRepository.findById(entry.getKey())
                        .orElseThrow(() -> new RuntimeException("Car ID " + entry.getKey() + " not found"));

                routeAssignment.setCar(car);
                routeAssignment.setStorage(storage);
                routeAssignmentRepository.save(routeAssignment);

                List<CargoPackage> cargoPackages = entry.getValue();
                for (CargoPackage cp : cargoPackages) {
                    cp.setRouteAssignment(routeAssignment);
                }
                cargoPackageRepository.saveAll(cargoPackages);
            }

            return true;

        } catch (Exception e) {
            // ❗ Hatanın logunu yaz
            System.err.println("Hata oluştu: " + e.getMessage());
            return false;
        }
    }


    //todo yola cik metodu yazicam paketleri orderNumber a gore birakacak


    //todo burada car repodan doldu gozuken araclari cekip araca yuklenen paketleri car id si ile esleyip
    public void yolaCik(Long routeAssgmntID) {
        List<Driver> avaibleDriverList = driverRepository.getAvaibleDriver();
        List<RouteAssignment> nullDriverRouteAssignmnet = routeAssignmentRepository.getNullDriverRoute();
        Optional<EventLog> optStatus = eventLogRepository.findById(7L);
        EventLog statusAracaAtandi = optStatus.get();

        Driver tempDriver = null;
        RouteAssignment tempRouteAsg = null;

        List<RouteAssignment> updatedAssignments = new ArrayList<>();
        List<Driver> updatedDrivers = new ArrayList<>();

        // müsait olan sürücüleri aldım ve
        // route assignmnet da sürücü atanmamışları topladım
        for (int i = 0; i < avaibleDriverList.size(); i++) {
            tempDriver = avaibleDriverList.get(i);
            tempRouteAsg = nullDriverRouteAssignmnet.get(i);

            tempRouteAsg.setDriver(tempDriver);
            tempDriver.setStatus(statusAracaAtandi);

            updatedAssignments.add(tempRouteAsg);
            updatedDrivers.add(tempDriver);
        }

        routeAssignmentRepository.saveAll(updatedAssignments);
        driverRepository.saveAll(updatedDrivers);

        //buradan araca yuklendi olarak gözüken ve yola çıkmasını isteğim route id eşleşenleri çekeceğiz
        List<CargoPackage> findByRouteAssgmnt = cargoPackageRepository.findByRouteAssgmnt(routeAssgmntID);

        optStatus = eventLogRepository.findById(2L);
        EventLog statusPackageOnRoad = optStatus.get();

        List<CargoPackage> updateCargoPackageList = new ArrayList<>();
        for (CargoPackage cargoPackage : findByRouteAssgmnt) {
            cargoPackage.setStatus(statusPackageOnRoad);

            updateCargoPackageList.add(cargoPackage);
        }
        cargoPackageRepository.saveAll(updateCargoPackageList);
    }

    public boolean startRoad(Long routeAssgmntID) {
        try {
            List<Driver> avaibleDriverList = driverRepository.getAvaibleDriver();
            List<RouteAssignment> nullDriverRouteAssignmnet = routeAssignmentRepository.getNullDriverRoute();

            if (avaibleDriverList.isEmpty() || nullDriverRouteAssignmnet.isEmpty()) {
                System.err.println("Uygun sürücü veya atanacak rota bulunamadı.");
                return false;
            }

            Optional<EventLog> optStatus = eventLogRepository.findById(7L);
            EventLog statusAracaAtandi = optStatus.orElseThrow(() -> new RuntimeException("EventLog ID 7 (araca atandı) bulunamadı."));

            List<RouteAssignment> updatedAssignments = new ArrayList<>();
            List<Driver> updatedDrivers = new ArrayList<>();

            for (int i = 0; i < avaibleDriverList.size(); i++) {
                Driver tempDriver = avaibleDriverList.get(i);
                RouteAssignment tempRouteAsg = nullDriverRouteAssignmnet.get(i);

                tempRouteAsg.setDriver(tempDriver);
                tempDriver.setStatus(statusAracaAtandi);

                updatedAssignments.add(tempRouteAsg);
                updatedDrivers.add(tempDriver);
            }

            routeAssignmentRepository.saveAll(updatedAssignments);
            driverRepository.saveAll(updatedDrivers);

            // Araca atanan paketleri bulup "yolda" durumuna çekiyoruz
            List<CargoPackage> findByRouteAssgmnt = cargoPackageRepository.findByRouteAssgmnt(routeAssgmntID);

            if (findByRouteAssgmnt.isEmpty()) {
                System.err.println("Belirtilen rota ID'siyle eşleşen paket bulunamadı.");
                return false;
            }

            optStatus = eventLogRepository.findById(2L);
            EventLog statusPackageOnRoad = optStatus.orElseThrow(() -> new RuntimeException("EventLog ID 2 (yolda) bulunamadı."));

            for (CargoPackage cargoPackage : findByRouteAssgmnt) {
                cargoPackage.setStatus(statusPackageOnRoad);
            }

            cargoPackageRepository.saveAll(findByRouteAssgmnt);

            return true;

        } catch (Exception e) {
            System.err.println("Yola çıkarken bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    //todo paket dağıtma işlemi
    public boolean completeDelivery(Long routeAssignmnetID, Long deliveryPointID) {
        try {
            Optional<EventLog> optStatus = eventLogRepository.findById(3L);
            if (optStatus.isEmpty()) {
                throw new RuntimeException("Teslim edildi durumu (EventLog id:3) bulunamadı.");
            }
            EventLog status = optStatus.get();

            optStatus = eventLogRepository.findById(5L);
            if (optStatus.isEmpty()) {
                throw new RuntimeException("Uygun (avaible) durumu (EventLog id:5) bulunamadı.");
            }
            EventLog avaibleStatus = optStatus.get();

            List<CargoPackage> onRoadPackageList = cargoPackageRepository.getCargoPackagesByRouteID(routeAssignmnetID);
            if (onRoadPackageList == null || onRoadPackageList.isEmpty()) {
                throw new RuntimeException("Bu route'a ait yolda paket bulunamadı.");
            }

            if (!onRoadPackageList.get(0).getDeliveryPoint().getId().equals(deliveryPointID)) {
                System.err.println("Bu siparişten önce sipariş edilmesi gereken paketler var.");
                return false;
            } else {
                List<CargoPackage> pointPackage = cargoPackageRepository.getCargoPackagesByRouteID(routeAssignmnetID);
                Set<Long> uniqueDeliveryPoints = pointPackage.stream()
                        .map(pkg -> pkg.getDeliveryPoint().getId())
                        .collect(Collectors.toSet());

                if (uniqueDeliveryPoints.size() > 1) {
                    System.out.println("Listede birden fazla farklı DeliveryPoint ID'si var.");
                    for (CargoPackage cargoPackage : pointPackage) {
                        cargoPackage.setStatus(status);
                    }
                } else {
                    System.out.println("Tüm paketler aynı DeliveryPoint'e ait.");

                    for (CargoPackage cargoPackage : pointPackage) {

                        Optional<Car> optCar = carRepository.findById(pointPackage.get(0).getRouteAssignment().getCar().getId());
                        if (optCar.isEmpty()) {
                            throw new RuntimeException("İlgili araç veritabanında bulunamadı.");
                        }


                        Optional<Driver> optDriver = driverRepository.findById(pointPackage.get(0).getRouteAssignment().getDriver().getId());
                        if (optDriver.isEmpty()) {
                            throw new RuntimeException("İlgili şoför veritabanında bulunamadı.");
                        }
                        Driver driver = optDriver.get();
                        driver.setStatus(avaibleStatus);

                        Car car = optCar.get();
                        car.setStatus(avaibleStatus);

                        cargoPackage.setStatus(status);
                    }
                }
            }

            return true; // ✅ işlemler başarıyla biterse burada return edilir
        } catch (Exception e) {
            System.err.println("Teslimat tamamlanırken hata oluştu: " + e.getMessage());
            return false;
        }
    }

    public void optimizeRoute() {

        List<Car> avaibleCar = carRepository.avaibleCars();
        for (int m = 0; m < avaibleCar.size(); m++) {

            List<CargoPackage> getPackages = cargoPackageRepository.getAvaibleCargoPackages();
            Map<Long, List<CargoPackage>> map = getPackages.stream()
                    .collect(Collectors.groupingBy(cp -> cp.getDeliveryPoint().getId()));

            //gidecek paketlerin bölgelerini aldım
            List<DeliveryPoint> deliveryPointList = cargoPackageRepository.cargoPackagesGroupByDeliveryPoint();
            double minDistance = Double.MAX_VALUE;
            List<DeliveryPoint> enKisaRota = null;

            List<List<DeliveryPoint>> tumPermutasyonlar = new ArrayList<>();

            List<DtoResponseCarWithCapacityInfo> avaibleCarsWithCapacity = carRepository.avaibleCarsWithCapacity();
            DtoResponseCarWithCapacityInfo carWithInfo = avaibleCarsWithCapacity.get(m);

            List<CargoPackage> getAllAvaiblaPackages = cargoPackageRepository.getAvaibleCargoPackages();
            List<CargoPackage> sigabilenPaketler = new ArrayList<>();

            //tum gidebileceği yol ihtimallerini tuttum
            permute(deliveryPointList, 0, tumPermutasyonlar);

            Long startPoint = 1L;

            for (List<DeliveryPoint> rota : tumPermutasyonlar) {
                double gecerliMesafe = 0;
                gecerliMesafe += distanceRepository.getDistanceBtw(startPoint, rota.get(0).getId());
                for (int i = 0; i < rota.size() - 1; i++) {
                    gecerliMesafe += distanceRepository.getDistanceBtw(rota.get(i).getId(), rota.get(i + 1).getId());
                }

                if (gecerliMesafe < minDistance) {
                    minDistance = gecerliMesafe;
                    enKisaRota = new ArrayList<>(rota);
                }
            }

            Optional<EventLog> optStatus = eventLogRepository.findById(4L);
            EventLog statusYuklendi = optStatus.get();

            optStatus = eventLogRepository.findById(4L);
            EventLog statusAracDoldu = optStatus.get();


            double carSize = carWithInfo.getSize();
            double carCapacity = carWithInfo.getCapacity();

            double packageSize = 0;
            double packageCapacity = 0;

            List<CargoPackage> cargoPackageList = new ArrayList<>();
            for (int i = 0; i < enKisaRota.size(); i++) {
                enKisaRota.get(i);
                List<CargoPackage> packages = map.get(enKisaRota.get(i).getId());
                for (CargoPackage cargoPackage : packages) {
                    cargoPackage.setOrderNumber(i);
                    packageSize = cargoPackage.getSize();
                    packageCapacity = cargoPackage.getWeight();
                    if (packageSize < carSize && packageCapacity < carCapacity) {
                        carSize -= packageSize;
                        carCapacity -= packageCapacity;
                        cargoPackageList.add(cargoPackage);
                        cargoPackage.setStatus(statusYuklendi);
                    }
                }
            }

            if (carCapacity != carWithInfo.getCapacity() && carSize != carWithInfo.getSize()) {
                //cargoPackageRepository.saveAll(cargoPackageList);
                Optional<Car> optCar = carRepository.findById(carWithInfo.getCarId());
                if (optCar.isEmpty()) {
                    throw new RuntimeException("Araç bulunamadı (Car id: " + carWithInfo.getCarId() + ")");
                }
                Car car = optCar.get();
                car.setStatus(statusAracDoldu);
                carRepository.save(car);
            }

            Optional<DeliveryPoint> optStorage = deliveryPointRepository.findById(1L);
            DeliveryPoint storage = optStorage.get();

            RouteAssignment routeAssignment = new RouteAssignment();
            Optional<Car> optCar = carRepository.findById(carWithInfo.getCarId());
            Car car = optCar.get();
            if (optCar.isEmpty()) {
                throw new RuntimeException("Route için araç bulunamadı (Car id: " + car.getId() + ")");
            }
            Car carForRoute = optCar.get();
            routeAssignment.setCar(carForRoute);
            routeAssignment.setStorage(storage);
            routeAssignmentRepository.save(routeAssignment);

            for (CargoPackage cargoPackage : cargoPackageList) {
                cargoPackage.setRouteAssignment(routeAssignment);
            }
            cargoPackageRepository.saveAll(cargoPackageList);
        }

    }

    private void permute(List<DeliveryPoint> deliveryPoints, int k, List<List<DeliveryPoint>> tumIhtimaller) {

        if (k == deliveryPoints.size()) {
            tumIhtimaller.add(new ArrayList<>(deliveryPoints));
        }

        for (int i = k; i < deliveryPoints.size(); i++) {
            Collections.swap(deliveryPoints, i, k);
            permute(deliveryPoints, k + 1, tumIhtimaller);
            Collections.swap(deliveryPoints, k, i);
        }
    }
}


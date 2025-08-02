package com.boratok.service.impls;

import com.boratok.dto.request.DtoRequestRouteAssgmnt;
import com.boratok.dto.response.*;
import com.boratok.entity.*;
import com.boratok.repository.*;
import com.boratok.service.IRouteAssignmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RouteAssignmentService implements IRouteAssignmentService {

    @Autowired
    private CargoPackageRepository cargoPackageRepository;

    @Autowired
    private RouteAssignmentRepository routeAssignmentRepository;

    @Autowired
    private DeliveryPointRepository deliveryPointRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarTypeRepository carTypeRepository;

    @Autowired
    private DistanceRepository distanceRepository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Override
    public DtoResponseRouteAssgmnt saveRouteAssgmnt(DtoRequestRouteAssgmnt dtoRequestRouteAssgmnt) {

        Optional<DeliveryPoint> optDelivery = deliveryPointRepository.findById(dtoRequestRouteAssgmnt.getStorage());
        if (optDelivery.isEmpty()) {
            System.out.println("bu storge id değerinde bir depo yok");
            return null;
        }

        Optional<Driver> optDriver = driverRepository.findById(dtoRequestRouteAssgmnt.getDriver());
        if (optDriver.isEmpty()) {
            System.out.println("bu driver id değerinde bir sürücü yok");
            return null;
        }

        Optional<Car> optCar = carRepository.findById(dtoRequestRouteAssgmnt.getCar());
        if (optCar.isEmpty()) {
            System.out.println("bu car id değerinde bir car yok");
            return null;
        }
        RouteAssignment newRoute = new RouteAssignment();
        newRoute.setDriver(optDriver.get());
        newRoute.setCar(optCar.get());
        newRoute.setStorage(optDelivery.get());
        routeAssignmentRepository.save(newRoute);

        Optional<RouteAssignment> optRouteAssignment = routeAssignmentRepository.findById(dtoRequestRouteAssgmnt.getId());
        if (optRouteAssignment.isEmpty()) {
            System.out.println("bu id değerinde bir varlık yok");
            return null;
        }
        DtoResponseRouteAssgmnt dtoResponseRouteAssgmnt = new DtoResponseRouteAssgmnt();
        DtoResponseDriver dtoResponseDriver = new DtoResponseDriver();
        DtoResponseCar dtoResponseCar = new DtoResponseCar();

        Driver driver = optRouteAssignment.get().getDriver();
        Car car = optRouteAssignment.get().getCar();
        DeliveryPoint deliveryPoint = optRouteAssignment.get().getStorage();

        BeanUtils.copyProperties(driver, dtoResponseDriver);
        BeanUtils.copyProperties(car, dtoResponseCar);

        dtoResponseRouteAssgmnt.setCar(dtoResponseCar);
        dtoResponseRouteAssgmnt.setDriver(dtoResponseDriver);
        dtoResponseRouteAssgmnt.setDeliveryPoint(deliveryPoint);

        return dtoResponseRouteAssgmnt;
    }

    @Override
    public DtoResponseRouteAssgmnt getRouteAssgmntById(Long id) {
        Optional<RouteAssignment> optRouteAssignment = routeAssignmentRepository.findById(id);
        if (optRouteAssignment.isEmpty()) {
            System.out.println("bu id değerinde bir varlık yok");
            return null;
        }
        DtoResponseRouteAssgmnt dtoResponseRouteAssgmnt = new DtoResponseRouteAssgmnt();
        DtoResponseDriver dtoResponseDriver = new DtoResponseDriver();
        DtoResponseCar dtoResponseCar = new DtoResponseCar();

        Driver driver = optRouteAssignment.get().getDriver();
        Car car = optRouteAssignment.get().getCar();
        DeliveryPoint storage = optRouteAssignment.get().getStorage();


        dtoResponseDriver.setNameSurname(driver.getNameSurname());
        dtoResponseDriver.setUsers(driver.getUsers());
        dtoResponseCar.setCarType(optRouteAssignment.get().getCar().getCarType());

        BeanUtils.copyProperties(driver, dtoResponseDriver);
        BeanUtils.copyProperties(car, dtoResponseCar);

        List<DtoResponsePackage> cargoPackage = cargoPackageRepository.findByRouteAssgmnt(optRouteAssignment.get().getId())
                .stream().map(cp -> {
                    return DtoResponsePackage.builder()
                            .weight(cp.getWeight())
                            .orderNumber(cp.getOrderNumber())
                            .status(cp.getStatus())
                            .deliveryPoint(cp.getDeliveryPoint())
                            .build();
                }).toList();


        dtoResponseRouteAssgmnt.setCar(dtoResponseCar);
        dtoResponseRouteAssgmnt.setDriver(dtoResponseDriver);
        dtoResponseRouteAssgmnt.setDeliveryPoint(storage);
        dtoResponseRouteAssgmnt.setCargoPackage(cargoPackage);


        paketYukleme();
        return dtoResponseRouteAssgmnt;

    }

    @Override
    public boolean deleteRouteAssigmnt(Long id) {
        Optional<RouteAssignment> optRouteAssignment = routeAssignmentRepository.findById(id);
        if (optRouteAssignment.isEmpty()) {
            System.out.println("bu id değerinde bir varlık yok");
            return false;
        }
        routeAssignmentRepository.delete(optRouteAssignment.get());
        return true;
    }

    @Override
    public DtoResponseRouteAssgmnt updateRouteAssigmnt(Long id, DtoRequestRouteAssgmnt dtoRequestRouteAssgmnt) {
        Optional<RouteAssignment> optRouteAssignment = routeAssignmentRepository.findById(id);
        if (optRouteAssignment.isEmpty()) {
            System.out.println("bu id değerinde bir varlık yok");
            return null;
        }

        RouteAssignment findRoute = optRouteAssignment.get();

        Optional<DeliveryPoint> optDelivery = deliveryPointRepository.findById(dtoRequestRouteAssgmnt.getStorage());
        if (optDelivery.isEmpty()) {
            System.out.println("bu storge id değerinde bir depo yok");
            return null;
        }

        Optional<Driver> optDriver = driverRepository.findById(dtoRequestRouteAssgmnt.getDriver());
        if (optDriver.isEmpty()) {
            System.out.println("bu driver id değerinde bir sürücü yok");
            return null;
        }

        Optional<Car> optCar = carRepository.findById(dtoRequestRouteAssgmnt.getCar());
        if (optCar.isEmpty()) {
            System.out.println("bu car id değerinde bir car yok");
            return null;
        }

        findRoute.setDriver(optDriver.get());
        findRoute.setStorage(optDelivery.get());
        findRoute.setCar(optCar.get());

        routeAssignmentRepository.save(findRoute);

        optRouteAssignment = routeAssignmentRepository.findById(id);
        if (optRouteAssignment.isEmpty()) {
            System.out.println("bu id değerinde bir varlık yok");
            return null;
        }
        DtoResponseRouteAssgmnt dtoResponseRouteAssgmnt = new DtoResponseRouteAssgmnt();
        DtoResponseDriver dtoResponseDriver = new DtoResponseDriver();
        DtoResponseCar dtoResponseCar = new DtoResponseCar();

        Driver driver = optRouteAssignment.get().getDriver();
        Car car = optRouteAssignment.get().getCar();
        DeliveryPoint deliveryPoint = optRouteAssignment.get().getStorage();

        dtoResponseDriver.setNameSurname(driver.getNameSurname());
        dtoResponseDriver.setUsers(driver.getUsers());
        dtoResponseCar.setCarType(car.getCarType());


        BeanUtils.copyProperties(driver, dtoResponseDriver);
        BeanUtils.copyProperties(car, dtoResponseCar);

        dtoResponseRouteAssgmnt.setCar(dtoResponseCar);
        dtoResponseRouteAssgmnt.setDriver(dtoResponseDriver);
        dtoResponseRouteAssgmnt.setDeliveryPoint(optRouteAssignment.get().getStorage());

        return dtoResponseRouteAssgmnt;

    }

    // todo burada paketleri grupla

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
                    cargoPackageRepository.save(cargoPackage);
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
                carRepository.save(car);
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
                List<CargoPackage> packagesToSave = new ArrayList<>();
                List<CargoPackage>packageList=cargoPackagesInCarMap.get(entry.getKey());
                for(CargoPackage cargoPackage:packageList){
                    if(cargoPackage.getDeliveryPoint().getId().equals(fisinhPoint)){
                        cargoPackage.setOrderNumber(order);
                        packagesToSave.add(cargoPackage);
                    }
                }
                cargoPackageRepository.saveAll(packagesToSave);
                tempDeliveryList.remove(index);
                order+=1;
                sayac=0;
                minDistance=Double.MAX_VALUE;;

            }
            order=0;
            for (Map.Entry<Long, List<CargoPackage>> getEntry :cargoPackagesInCarMap.entrySet()) {

                RouteAssignment routeAssignment=new RouteAssignment();
                Optional<Car>optCarForRoute=carRepository.findById(getEntry.getKey());
                Car carForRoute=optCarForRoute.get();
                routeAssignment.setCar(carForRoute);
                routeAssignment.setStorage(storage);
                routeAssignmentRepository.save(routeAssignment);
                List<CargoPackage> cargoPackagesRouteSet = getEntry.getValue();

                for(CargoPackage cargoPackage:cargoPackagesRouteSet){
                    cargoPackage.setRouteAssignment(routeAssignment);
                }
                cargoPackageRepository.saveAll(cargoPackagesRouteSet);
            }
        }
    }

    public void paketIslem() {

        EventLog statusEvent = eventLogRepository.getReferenceById(4L);// araca yüklendi statusu

        EventLog dolduStatus = eventLogRepository.getReferenceById(6L);// araç doldu statusu

        List<DtoResponseCarWithCapacityInfo> tempAvaibleCarList = carRepository.avaibleCarsWithCapacity();// müsait araçları aldık
        List<DtoResponseCarWithCapacityInfo> doldurugumuzAraclar = carRepository.avaibleCarsWithCapacity();

        //burada ise müsait durumdaki paketleri çektim
        List<CargoPackage> cargoPackagesList = cargoPackageRepository.getCargoPackagesOrderByCreateTime();

        List<CargoPackage> gonderilecekler = new ArrayList<>();

        List<CargoPackage> cargoPackagesInCar = new ArrayList<>();
        List<DeliveryPoint> cargoDeliveryPoints = new ArrayList<>();

        //todo buraya car repo sundan cektigim degerlerin eger kapasitesi dolarsa diger araci doldurmaya gececek
        //todo arabayi full doldurduktan sonra listeyi bir daha cekip bastan sonra siralamam gerekiyor ve bunu statusa gore kontrol edebilirim

        double carCapacity = 0;
        double cargoWeight = 0;

        while (!tempAvaibleCarList.isEmpty() && !cargoPackagesList.isEmpty()) {// eğer doldurulacak araba veya koyacak paket kalmadıysa çık
            DtoResponseCarWithCapacityInfo avaibleCar = tempAvaibleCarList.get(0); //müsait olan araçları çektim
            carCapacity = avaibleCar.getCapacity(); // o aracın kapasitesini aldım
            for (CargoPackage cargoPackage : cargoPackagesList) {
                cargoWeight = cargoPackage.getWeight();// paketin ağırlığını aldım
                if (cargoWeight < carCapacity) { // ağırlık aracın kapasitesini aşıyor mu
                    gonderilecekler.add(cargoPackage);
                    cargoPackagesInCar.add(cargoPackage);// araca yükledim
                    cargoPackage.setStatus(statusEvent);// paketin durumu araçta diye setledim
                    carCapacity = carCapacity - cargoPackage.getWeight();//aracın kapasitesi eksiliyor
                    cargoDeliveryPoints.add(cargoPackage.getDeliveryPoint());//paketin noktasını tuttum
                }

            }
            if(carCapacity!=avaibleCar.getCapacity()) {// araç müsait oldugu halde ona hiçbir yükleme işlemi yapılmamışsa bu işlemleri yapma
                cargoPackageRepository.saveAll(cargoPackagesInCar);
                Optional<Car> optCar = carRepository.findById(avaibleCar.getCarId());
                Car car = optCar.get();
                car.setStatus(dolduStatus);
                carRepository.save(car);
                tempAvaibleCarList = carRepository.avaibleCarsWithCapacity(); // tekrardan müsait olan araçları çektik
                cargoPackagesList = cargoPackageRepository.getCargoPackagesOrderByCreateTime();// araca yüklenmemiş paketleri tekrardan çektik
            }
        }

        // araca yuklemis olduk

        long startPointId = 1; //depo


        List<DeliveryPoint> deliveryPointsList = new ArrayList<>(cargoDeliveryPoints);//hash de get(i) yapamıyordum
        double minDistance = Double.MAX_VALUE;
        double sumDistance = 0;
        long packagePointId = 0;

        List<Long> path = new ArrayList<>();
        path.add(startPointId);

        long tempPoint = 0;
        int j = 0;
        int tempDeger = 0;

        // todo bu kısımda sadece bir aracın rotası çıkıyor
        // todo bunu düzeltmem gerek
        while (deliveryPointsList.size() > 0) {
            //DeliveryPoint pointsCargo = deliveryPointsList.get(1);
            for (int i = 0; i < deliveryPointsList.size(); i++) {
                DeliveryPoint pointsCargo = deliveryPointsList.get(i);
                packagePointId = pointsCargo.getId();
                double tempDistance = distanceRepository.getDistanceBtw(startPointId, packagePointId);
                if (tempDistance < minDistance) {
                    tempDeger = i;
                    tempPoint = packagePointId;
                    minDistance = tempDistance;
                }
            }
            deliveryPointsList.remove(tempDeger);
            j++;
            path.add(tempPoint);
            sumDistance += minDistance;
            minDistance = Double.MAX_VALUE;
            startPointId = tempPoint;
        }

        for (Long yol : path) {
            System.out.println("yol: " + yol);
        }
        // todo sirasiyla buyuk aractan kucuk araca dogru kaydedicem
        // todo bu metodu dogru konuma tasiycam
        // todo r.a yi belirleyecegim degerleri set edecegim

        Driver driver=driverRepository.getReferenceById(1L);
        DeliveryPoint deliveryPointStorage=deliveryPointRepository.getReferenceById(1L);
        for (DtoResponseCarWithCapacityInfo dtoResponseCarWithCapacityInfo : doldurugumuzAraclar) {
            RouteAssignment routeAssignment = new RouteAssignment();
            Optional<Car> optCar = carRepository.findById(dtoResponseCarWithCapacityInfo.getCarId());
            Car car = optCar.get();
            routeAssignment.setCar(car);
            routeAssignment.setDriver(driver);
            routeAssignment.setStorage(deliveryPointStorage);
            routeAssignment.setCreateTime(LocalDateTime.now());
            routeAssignmentRepository.save(routeAssignment);
        }


        /*List<CargoPackage> packages = packageRepository.findAllPackages();

        List<CargoPackage> sortedPackages=packages.stream()
                .sorted(Comparator
                        .comparing(CargoPackage::getSize, Comparator.reverseOrder())      // size alanı
                        .thenComparing(CargoPackage::getWeight, Comparator.reverseOrder())) // weight alanı
                .collect(Collectors.toList());

        //bölgeye göre gruplayıp sıraladığımız map
        /*
        Map<Long, List<CargoPackage>> grouped = packages.stream()
                .collect(Collectors
                        .groupingBy(cp -> cp.getDeliveryPoint().getId()));

        Map<Long,List<CargoPackage>> sortedBySizeHeight=grouped.entrySet().stream()
                .collect(Collectors.toMap(
                   Map.Entry::getKey,
                   entry-> entry.getValue().stream()
                           .sorted(Comparator
                                   .comparing(CargoPackage::getSize, Comparator.reverseOrder())      // size alanı
                                   .thenComparing(CargoPackage::getWeight, Comparator.reverseOrder())) // weight alanı
                           .collect(Collectors.toList())
                ));
        */
        //gruplamanın sonucunu görmek için yazdım
        /*
        grouped.forEach((bolge, liste) -> {
            System.out.println("Bölge: " + bolge);
            liste.forEach(s -> System.out.println("  " + s));
        });*/

        /*List<CargoPackage> allPackagesSorted = sortedByVolumeWeight.values()
                .stream()
                .flatMap(List::stream)  // Tüm listeleri birleştir
                .sorted(Comparator
                        .comparing(CargoPackage::getSize, Comparator.reverseOrder())
                        .thenComparing(CargoPackage::getWeight, Comparator.reverseOrder()))
                .collect(Collectors.toList());*/

        // todo startpointe en yakın noktayı bul ( başlangıç id miz 1 diğer tüm id leri gez sırayla distance tablosunda mesafesi en kısa olanı çek birden fazlaysa random.)
        // todo çektikten sonra artık başlangıç nokatmız çektğimiz delivery point id. sonrasında bu  id yi göz ardı et ve delivery point leri gezip yukardaki aynı işlemi yap ve başlangıç noktanı yine son aldığın nokta ile setle.
        // todo delivery point listesi bitene kadar bu işlemi yap. her setlediğin startpointi bir listede sıralı tut.
        // todo sonra araç içindeki paketleri gez bu listedeki delivery point id si ile kontrol et sıralı şekilde. Paketlerin sıra nosunu ata ve durumlarını değiştir(araca yüklendi.)
        // todo bir route assigment oluştur. Araç ve şoför atamayı unutma. Yukarda aldığın paketlerin r.a. id lerini ata!
        // todo bir aksiyon alacak method daha yaz bu da route ass. id alsın (Yolculuğu başlat aksiyonu) burda araç ve paketlerin durumlarını değiştir. Çünkü yolculuğu başlattık.
        // todo bir de teslimatı bitir aksiyonu olsun. şimdilik tümünü bitir. araç ve paketlerşin durumu değişiecek.

    }
}

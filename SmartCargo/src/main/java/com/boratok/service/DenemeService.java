package com.boratok.service;

import com.boratok.dto.response.DtoResponseCarWithCapacityInfo;
import com.boratok.entity.*;
import com.boratok.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;


public class DenemeService {

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


    public void paketIslem() {


        // todo status degerlerini revize edicem direkt olarak reposundan cekmem daha dogru olur gibi geldi
        EventLog statusEvent = eventLogRepository.getReferenceById(4L);// araca yüklendi statusu

        EventLog dolduStatus = eventLogRepository.getReferenceById(6L);// araç doldu statusu

        List<DtoResponseCarWithCapacityInfo> tempAvaibleCarList = carRepository.avaibleCarsWithCapacity();// müsait araçları aldık


        //burada ise müsait durumdaki paketleri çektim
        List<CargoPackage> cargoPackagesList = cargoPackageRepository.getCargoPackagesOrderByCreateTime();

        List<CargoPackage> gonderilecekler = new ArrayList<>();

        Map<Long, List<CargoPackage>> cargoPackagesWithCarID = new HashMap<>();

        List<CargoPackage> cargoPackagesInCar = new ArrayList<>();
        Map<Long, List<DeliveryPoint>> cargoDeliveryPointsWithCarID = new HashMap<>(); // burada verileri carId ve deliveryPoint obje turunde almak icin

        //todo buraya car repo sundan cektigim degerlerin eger kapasitesi dolarsa diger araci doldurmaya gececek
        //todo arabayi full doldurduktan sonra listeyi bir daha cekip bastan sonra siralamam gerekiyor ve bunu statusa gore kontrol edebilirim

        double carCapacity = 0;
        double cargoWeight = 0;

        while (!tempAvaibleCarList.isEmpty() && !cargoPackagesList.isEmpty()) {// eğer doldurulacak araba veya koyacak paket kalmadıysa çık

            List<DeliveryPoint> carDeliveryPoints = new ArrayList<>();
            DtoResponseCarWithCapacityInfo avaibleCar = tempAvaibleCarList.get(0); //müsait olan araçları çektim
            carCapacity = avaibleCar.getCapacity(); // o aracın kapasitesini aldım
            for (CargoPackage cargoPackage : cargoPackagesList) {
                cargoWeight = cargoPackage.getWeight();// paketin ağırlığını aldım
                if (cargoWeight < carCapacity) { // ağırlık aracın kapasitesini aşıyor mu

                    gonderilecekler.add(cargoPackage);
                    cargoPackagesInCar.add(cargoPackage);// araca yükledim
                    cargoPackage.setStatus(statusEvent);// paketin durumu araçta diye setledim
                    carCapacity = carCapacity - cargoPackage.getWeight();//aracın kapasitesi eksiliyor
                    carDeliveryPoints.add(cargoPackage.getDeliveryPoint());//paketin noktasını tuttum
                    cargoDeliveryPointsWithCarID.put(avaibleCar.getCarId(), carDeliveryPoints);// gönderecek aracın gideceği yerleri mapledik
                }

            }
            if (carCapacity != avaibleCar.getCapacity()) {// araç müsait oldugu halde ona hiçbir yükleme işlemi yapılmamışsa bu işlemleri yapma

                cargoPackageRepository.saveAll(cargoPackagesInCar);
                Optional<Car> optCar = carRepository.findById(avaibleCar.getCarId());
                Car car = optCar.get();
                cargoPackagesWithCarID.put(car.getId(), cargoPackagesInCar);

                car.setStatus(dolduStatus);
                carRepository.save(car);
                tempAvaibleCarList = carRepository.avaibleCarsWithCapacity(); // tekrardan müsait olan araçları çektik
                cargoPackagesList = cargoPackageRepository.getCargoPackagesOrderByCreateTime();// araca yüklenmemiş paketleri tekrardan çektik
            }
        }
        List<Car> doldurugumuzAraclar = carRepository.avaibleCars();// doldurma islemi yaparken kullandigimiz araclar
        // ve de diyelim daha onceden araci doldurmustuk ve harekete gecirme islemini yapmamistik burada onceden doldurup da
        // gondermedigimiz araclari da cekmemizi sagliyor

        // araca yuklemis olduk

        long startPointId = 1; //depo nun oldugu bolgenin id


        double minDistance = Double.MAX_VALUE;
        double sumDistance = 0;

        long packagePointId = 0;

        List<Long> path = new ArrayList<>();
        path.add(startPointId);

        long tempPoint = 0;
        int j = 0;
        int tempDeger = 0;
        // todo bir tane daha map dönücem
        // todo bu kısımda sadece bir aracın rotası çıkıyor
        // todo bunu düzeltmem gerek

        Map<Long, List<Long>> aracRotaBilgisi = new HashMap<>();
        for (Car car : doldurugumuzAraclar) {

            List<DeliveryPoint> deliveryPointsList = cargoDeliveryPointsWithCarID.get(car.getId());
            // bu if eger ben mapledigim degerdeki bir aracin icine doldurulmus nesne var mi
            if (deliveryPointsList == null || deliveryPointsList.isEmpty()) continue;

            path = new ArrayList<>();

            j = 0;

            while (!deliveryPointsList.isEmpty()) {
                for (int i = 0; i < deliveryPointsList.size(); i++) {
                    DeliveryPoint pointsCargo = deliveryPointsList.get(i);
                    packagePointId = pointsCargo.getId();
                    double tempDistance = distanceRepository.getDistanceBtw(startPointId, packagePointId);
                    if (tempDistance < minDistance) {
                        minDistance = tempDistance;
                        tempPoint = packagePointId;
                        tempDeger = i;
                        //Long carID=cargoPackagesWithCarID.get()
                        CargoPackage cargoPackage = gonderilecekler.get(i);
                        cargoPackage.setOrderNumber(j); // sira numarasini atiyoruz paketin
                    }
                }
                deliveryPointsList.remove(tempDeger);// silmemizin sebebi gidilen yere bir daha gitmeye calisamasin
                j++;
                path.add(tempPoint);
                sumDistance += minDistance;
                minDistance = Double.MAX_VALUE;
                startPointId = tempPoint;
            }
            aracRotaBilgisi.put(car.getId(), path);
        }


        for (Long yol : path) {
            System.out.println("yol: " + yol);
        }
        // todo sirasiyla buyuk aractan kucuk araca dogru kaydedicem
        // todo bu metodu dogru konuma tasiycam
        // todo r.a yi belirleyecegim degerleri set edecegim

        Driver driver = driverRepository.getReferenceById(1L);
        DeliveryPoint deliveryPointStorage = deliveryPointRepository.getReferenceById(1L);
        for (Car car : doldurugumuzAraclar) {
            RouteAssignment routeAssignment = new RouteAssignment();
            routeAssignment.setCar(car);
            routeAssignment.setDriver(driver);
            routeAssignment.setStorage(deliveryPointStorage);
            routeAssignment.setCreateTime(LocalDateTime.now());
            routeAssignmentRepository.save(routeAssignment);
        }
    }
}

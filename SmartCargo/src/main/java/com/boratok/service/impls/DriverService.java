package com.boratok.service.impls;

import com.boratok.dto.request.DtoRequestDriver;
import com.boratok.dto.response.DtoResponseDriver;
import com.boratok.entity.Driver;
import com.boratok.entity.Users;
import com.boratok.repository.DriverRepository;
import com.boratok.repository.UsersRepository;
import com.boratok.service.IDriverService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverService implements IDriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public DtoRequestDriver saveDriver(DtoRequestDriver dtoDriver) {
        Driver driver = new Driver();
        driver.setNameSurname(dtoDriver.getNameSurname());

        Optional<Users> optDriver = usersRepository.findById(dtoDriver.getUsers());
        if (optDriver.isPresent()) {
            driver.setUsers(optDriver.get());
        } else {
            System.out.println("girdiginiz id degerinde bir surucu bulunmamakta");
            return null;
        }
        driverRepository.save(driver);

        return dtoDriver;
    }

    @Override
    public DtoResponseDriver getDriverById(Long id) {

        Optional<Driver> optDriver = driverRepository.findById(id);
        if (optDriver.isEmpty()) {
            System.out.println("Boyle bir deger yok");
            return null;
        }
        Driver driver = optDriver.get();
        Users users = optDriver.get().getUsers();
        DtoResponseDriver dtoResponseDriver = new DtoResponseDriver();

        BeanUtils.copyProperties(driver, dtoResponseDriver);

        dtoResponseDriver.setUsers(users);
        return dtoResponseDriver;
    }

    @Override
    public boolean deleteDriver(Long id) {

        Optional<Driver> optDriver = driverRepository.findById(id);
        if (optDriver.isEmpty()) {
            System.out.println("bu id de silinecek birisi yok");
            return false;
        }
        driverRepository.delete(optDriver.get());
        return true;
    }

    @Override
    public DtoResponseDriver updateDriver(Long id, DtoRequestDriver dtoRequestDriver) {
        Optional<Driver> optDriver = driverRepository.findById(id);
        if (optDriver.isEmpty()) {
            System.out.println("guncelleme yapabilmek icin gecerli bir id giriniz");
            return null;
        }
        Driver findDriver = new Driver();
        findDriver.setNameSurname(dtoRequestDriver.getNameSurname());

        Optional<Users> optUsers = usersRepository.findById(dtoRequestDriver.getUsers());
        if (optUsers.isEmpty()) {
            System.out.println("bu id de bir kullanici turu bulunmamakta");
            return null;
        }
        findDriver.setUsers(optUsers.get());

        driverRepository.save(findDriver);

        //postman gormek icin yaptigimiz kisim
        Users role = optDriver.get().getUsers();

        DtoResponseDriver dtoResponseDriver = new DtoResponseDriver();
        dtoResponseDriver.setNameSurname(findDriver.getNameSurname());
        dtoResponseDriver.setUsers(role);

        return dtoResponseDriver;
    }
}
